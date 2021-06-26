package com.example.demo;

import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.geotools.referencing.GeodeticCalculator;
import org.joda.time.LocalDateTime;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class DroneController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroneController.class);
    private final DroneRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;
    static Random rand = new Random();
    private double rEarth = 6371.01; // Earth's average radius in km
    private double epsilon = 0.000001; // threshold for floating-point equality


    DroneController(DroneRepository repository, ElasticsearchOperations elasticsearchOperations) {
        this.repository = repository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/drones")
    List<DronePlan> all() {
        return repository.findAll();
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/drones", consumes = "application/json")
    List<DronePlan> newDronePlan(@RequestBody DronePlan newDronePlan) throws IOException {

        if (checkIntersection(newDronePlan).toArray().length != 0) {
            System.out.println("Intersectie in spatiu");
            return checkIntersection(newDronePlan);
        } else {
            System.out.println("New drone plan saved");
            System.out.println(newDronePlan.toString());
            repository.save(newDronePlan);
        }

        List<DronePlan> toReturn = new ArrayList<>();
        toReturn.add(newDronePlan);
        return toReturn;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/drones/{startDate}_{endDate}")
    List<DronePlan> byDate(@PathVariable String startDate, @PathVariable String endDate) {
        List<DronePlan> resultsMapped = null;
        QueryBuilder timeQuery = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery("startTime").
                        lte(endDate))
                .must(QueryBuilders.rangeQuery("endTime").
                        gte(startDate));
        Query query = new NativeSearchQueryBuilder().withQuery(timeQuery).build();
        SearchHits<DronePlan> results = elasticsearchOperations.search(query, DronePlan.class);
        resultsMapped = (results.get().map(SearchHit::getContent).collect(Collectors.toList()));

        return resultsMapped;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/drones/getTrajectory", consumes = "application/json")
    ArrayList<ArrayList<Double>> newCameraParameters(@RequestBody CameraParameters newCameraParameters) {
        List<List<List<Double>>> coords = returnAsArray(newCameraParameters);
        double spatialResolution = newCameraParameters.getSpatialResolution(); //7.6(cm/px)
        double digitalResolutionWidth = newCameraParameters.getDigitalResolutionWidth(); // 4000; //(px)
        double digitalResolutionHeight = newCameraParameters.getDigitalResolutionHeight();  //3000;//(px)
        double sensorWidth = newCameraParameters.getSensorWidth();     //2.9; //(mm)
        double focalLength = newCameraParameters.getFocalLength();    // 3.6;// (mm)
        return getTrajectoryFromParams(coords, spatialResolution, digitalResolutionWidth, digitalResolutionHeight, sensorWidth, focalLength);

    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/drones/trajectory/region", consumes = "application/json")
    Coordinate[] recommendedRegion(@RequestBody CameraParameters newCameraParameters) {
        List<List<List<Double>>> coords = returnAsArray(newCameraParameters);
        double spatialResolution = newCameraParameters.getSpatialResolution();
        double digitalResolutionWidth = newCameraParameters.getDigitalResolutionWidth();
        double digitalResolutionHeight = newCameraParameters.getDigitalResolutionHeight();
        double sensorWidth = newCameraParameters.getSensorWidth();
        double focalLength = newCameraParameters.getFocalLength();
        ArrayList<ArrayList<Double>> lineString = new ArrayList<>();
        lineString = getTrajectoryFromParams(coords, spatialResolution, digitalResolutionWidth, digitalResolutionHeight, sensorWidth, focalLength);
        Geometry sourceGeom = new Utility().example(coords);
        Geometry sourcePath = new Utility().createLine(lineString);
        Geometry pathBound = sourcePath.getEnvelope();
        DronePlan toReturn = new DronePlan();
        return pathBound.getCoordinates();
    }

    @DeleteMapping("/drones/{id}")
    void deleteEmployee(@PathVariable String id) {
        repository.deleteById(id);
    }

    List<List<List<Double>>> returnAsArray(CameraParameters newCameraParameters) {
        List<List<List<Double>>> coords = new ArrayList<>();
        List<List<Double>> inner = new ArrayList<>();
        System.out.println(newCameraParameters.getGeometry().getCoordinates().get(0).getCoordinates());
        for (int j = 0; j < newCameraParameters.getGeometry().getCoordinates().get(0).getCoordinates().toArray().length; j++) {
            List<Double> toAdd = new ArrayList<>();
            toAdd.add(newCameraParameters.getGeometry().getCoordinates().get(0).getCoordinates().get(j).getX());
            toAdd.add(newCameraParameters.getGeometry().getCoordinates().get(0).getCoordinates().get(j).getY());
            inner.add(toAdd);
        }
        coords.add(inner);
        return coords;
    }


    ArrayList<ArrayList<Double>> getTrajectoryFromParams(List<List<List<Double>>> coords, double spatialResolution, double digitalResolutionWidth,
                                                         double digitalResolutionHeight, double sensorWidth, double focalLength) {
        double spatialResolutionInMillimeters = spatialResolution * 10;
        double requiredFlightAltitudeInMillimeters = (spatialResolutionInMillimeters * digitalResolutionWidth * focalLength) / sensorWidth;
        double requiredFlightAltitudeInMeters = requiredFlightAltitudeInMillimeters * 0.001;
        double photoWidthInMeters = (digitalResolutionWidth * spatialResolutionInMillimeters) * 0.001;
        double photoHeightInMeters = (digitalResolutionHeight * spatialResolutionInMillimeters) * 0.001;

        System.out.println("Height:" + photoHeightInMeters + " width:" + photoWidthInMeters);

        Geometry sourceGeom = new Utility().example(coords);
        Geometry boundingGeometry = sourceGeom.getEnvelope();

        System.out.println(sourceGeom.toText());
        System.out.println(boundingGeometry.toString());
        double boundingWidthInM = distanceBetweenPoints(boundingGeometry.getCoordinates()[0].getY(), boundingGeometry.getCoordinates()[0].getX()
                , boundingGeometry.getCoordinates()[3].getY(), boundingGeometry.getCoordinates()[3].getX());
        double boundingHeightInM = distanceBetweenPoints(boundingGeometry.getCoordinates()[0].getY(), boundingGeometry.getCoordinates()[0].getX()
                , boundingGeometry.getCoordinates()[1].getY(), boundingGeometry.getCoordinates()[1].getX());
        System.out.println("Width of the bounding region:" + boundingWidthInM + "m");
        System.out.println("Height of the bounding region: " + boundingHeightInM + "m");

        ArrayList<Double> firstPoint = new ArrayList<>();
        ArrayList<Double> leftCorner = new ArrayList<>();
        ArrayList<Double> rightCorner = new ArrayList<>();
        leftCorner.add(boundingGeometry.getCoordinates()[0].getX());
        leftCorner.add(boundingGeometry.getCoordinates()[0].getY());
        rightCorner.add(boundingGeometry.getCoordinates()[3].getX());
        rightCorner.add(boundingGeometry.getCoordinates()[3].getY());
        ArrayList<ArrayList<Double>> lineString = new ArrayList<>();
        for (int j = 0; j <= Math.ceil((boundingHeightInM) / (photoWidthInMeters)) + 2; j++) {
            firstPoint.clear();
            if (j % 2 == 0) {
                firstPoint.add(destinationPoint(leftCorner.get(0), leftCorner.get(1), 90, j * photoHeightInMeters).get(1));
                firstPoint.add(destinationPoint(leftCorner.get(0), leftCorner.get(1), 90, j * photoHeightInMeters).get(0));
            } else {
                firstPoint.add(destinationPoint(rightCorner.get(0), rightCorner.get(1), 90, j * photoHeightInMeters).get(1));
                firstPoint.add(destinationPoint(rightCorner.get(0), rightCorner.get(1), 90, j * photoHeightInMeters).get(0));
            }
            for (int i = 0; i <= Math.ceil((boundingWidthInM) / (photoHeightInMeters)); i++) {
                ArrayList<Double> secondPoint = new ArrayList<>();
                ArrayList<Double> thirdPoint = new ArrayList<>();
                ArrayList<Double> fourthPoint = new ArrayList<>();
                if (j % 2 == 0) {
                    secondPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 0, photoWidthInMeters).get(1));
                    secondPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 0, photoWidthInMeters).get(0));
                    thirdPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 90, photoHeightInMeters).get(1));
                    thirdPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 90, photoHeightInMeters).get(0));
                    fourthPoint.add(destinationPoint(thirdPoint.get(0), thirdPoint.get(1), 0, photoWidthInMeters).get(1));
                    fourthPoint.add(destinationPoint(thirdPoint.get(0), thirdPoint.get(1), 0, photoWidthInMeters).get(0));
                } else {
                    secondPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 180, photoWidthInMeters).get(1));
                    secondPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 180, photoWidthInMeters).get(0));
                    thirdPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 90, photoHeightInMeters).get(1));
                    thirdPoint.add(destinationPoint(firstPoint.get(0), firstPoint.get(1), 90, photoHeightInMeters).get(0));
                    fourthPoint.add(destinationPoint(thirdPoint.get(0), thirdPoint.get(1), 180, photoWidthInMeters).get(1));
                    fourthPoint.add(destinationPoint(thirdPoint.get(0), thirdPoint.get(1), 180, photoWidthInMeters).get(0));
                }
                List<List<Double>> polygonCoords = new ArrayList<>();
                polygonCoords.add(firstPoint);
                polygonCoords.add(secondPoint);
                polygonCoords.add(fourthPoint);
                polygonCoords.add(thirdPoint);
                polygonCoords.add(firstPoint);
                List<List<List<Double>>> polygonFinalCoords = new ArrayList<>();
                polygonFinalCoords.add(polygonCoords);
                firstPoint = secondPoint;

                Geometry currentRectangle = new Utility().example(polygonFinalCoords);
                if (currentRectangle.intersects(sourceGeom)) {
                    ArrayList<Double> points = new ArrayList<>();
                    Point center = currentRectangle.getCentroid();
                    points.add(center.getX());
                    points.add(center.getY());
                    lineString.add(points);
                }
            }
        }
        return lineString;
    }


    public List<DronePlan> checkIntersection(DronePlan toCheck) throws IOException {
        LocalDateTime startTime = new LocalDateTime(toCheck.getStartTime());
        String start = startTime.toString().substring(0, startTime.toString().length() - 7);

        LocalDateTime endTime = new LocalDateTime(toCheck.getEndTime());
        String end = endTime.toString().substring(0, endTime.toString().length() - 7);

        List<Coordinate> list = toCheck.getGeometry().getCoordinates().get(0).getCoordinates()
                .stream().map(point -> new Coordinate(point.getX(), point.getY())).collect(Collectors.toList());

        GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery("geometry",
                new PolygonBuilder(new CoordinatesBuilder()
                        .coordinates(list)).buildGeometry());
        qb.relation(ShapeRelation.INTERSECTS);
        QueryBuilder timeQuery = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery("startTime").
                        lte(end))
                .must(QueryBuilders.rangeQuery("endTime").
                        gte(start)).must(qb);

        Query spaceQuery = new NativeSearchQueryBuilder().withQuery(timeQuery).build();

        SearchHits<DronePlan> results = elasticsearchOperations.search(spaceQuery, DronePlan.class);
        List<DronePlan> resultsMapped = (results.get().map(SearchHit::getContent).collect(Collectors.toList()));
        for (int i = 0; i < resultsMapped.toArray().length; i++) {
            resultsMapped.get(i).setName("intersectie");
        }
        System.out.println(resultsMapped);
        System.out.println(resultsMapped.toArray().length);
        return resultsMapped;
    }


    private static double getRandomDouble(double lowerBound, double upperBound) {
        Random r = new Random(System.currentTimeMillis());
        return lowerBound + (upperBound - lowerBound) * r.nextDouble();
    }

    ArrayList<Double> destinationPoint(double lat1, double lon1, double bearing, double distance) {

        GeodeticCalculator gc = new GeodeticCalculator();
        gc.setStartingGeographicPoint(lon1, lat1);
        gc.setDirection(bearing, distance);
        Point2D p = gc.getDestinationGeographicPoint();
        ArrayList<Double> toReturn = new ArrayList<>();
        toReturn.add(p.getX());
        toReturn.add(p.getY());
        return toReturn;
    }

    public double distanceBetweenPoints(double lat1, double lon1, double lat2, double lon2) {
        GeodeticCalculator gc = new GeodeticCalculator();
        gc.setStartingGeographicPoint(lon1, lat1);
        gc.setDestinationGeographicPoint(lon2, lat2);
        return gc.getOrthodromicDistance();

    }
}
