package com.example.demo;

import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.index.query.GeoShapeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.*;

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
    private static Random rand = new Random();

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
    DronePlan newDronePlan(@RequestBody DronePlan newDronePlan) {


//        List<Coordinate> list = repository.findAll().get(repository.findAll().toArray().length).getGeometry().getCoordinates()
//                .get(repository.findAll().toArray().length).getCoordinates().stream()
//                .map(point -> new Coordinate(point.getX(), point.getY())).collect(Collectors.toList());
//        GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery("geometry",
//                new PolygonBuilder(new CoordinatesBuilder()
//                        .coordinates(list)).buildGeometry());
//        qb.relation(ShapeRelation.INTERSECTS);
//        Query spaceQuery = new NativeSearchQueryBuilder().withQuery(qb).build();
//        SearchHits<DronePlan> results = elasticsearchOperations.search(spaceQuery, DronePlan.class);
//        List<DronePlan> resultsMapped = (results.get().map(SearchHit::getContent).collect(Collectors.toList()));
//        System.out.println(resultsMapped);
//        System.out.println(resultsMapped.get(0));


        System.out.println("New drone plan saved");
        System.out.println(newDronePlan.toString());

        return repository.save(newDronePlan);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/drones/{startDate}_{endDate}_{ok}")
    List<DronePlan> byDate(@PathVariable String startDate, @PathVariable String endDate, @PathVariable int ok) throws IOException {
        List<DronePlan> returns = null;
        if (ok == 1) {
            System.out.println("Verifica doar timp");
            QueryBuilder timeQuery = QueryBuilders
                    .boolQuery()
                    .must(QueryBuilders.rangeQuery("startTime").
                            lte(endDate))
                    .must(QueryBuilders.rangeQuery("endTime").
                            gte(startDate));
            Query query = new NativeSearchQueryBuilder().withQuery(timeQuery).build();
            SearchHits<DronePlan> results = elasticsearchOperations.search(query, DronePlan.class);
            List<DronePlan> resultsMapped = (results.get().map(SearchHit::getContent).collect(Collectors.toList()));
            System.out.println(resultsMapped);
            returns = resultsMapped;
        } else if (ok == 2) {
            System.out.println("Verifica si spatiu");
            List<Coordinate> list = repository.findAll().get(repository.findAll().toArray().length-1).getGeometry().getCoordinates()
                    .get(0).getCoordinates().stream()
                    .map(point -> new Coordinate(point.getX(), point.getY())).collect(Collectors.toList());
            GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery("geometry",
                    new PolygonBuilder(new CoordinatesBuilder()
                            .coordinates(list)).buildGeometry());
            qb.relation(ShapeRelation.INTERSECTS);
            QueryBuilder timeQuery = QueryBuilders
                    .boolQuery()
                    .must(QueryBuilders.rangeQuery("startTime").
                            lte(endDate))
                    .must(QueryBuilders.rangeQuery("endTime").
                            gte(startDate))
                    .must(qb);
            Query spaceQuery = new NativeSearchQueryBuilder().withQuery(timeQuery).build();

            SearchHits<DronePlan> results = elasticsearchOperations.search(spaceQuery, DronePlan.class);
            List<DronePlan> resultsMapped = (results.get().map(SearchHit::getContent).collect(Collectors.toList()));
            returns = resultsMapped;
        }
        return returns;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/drones/test")
    DronePlan testPlan() throws FactoryException, TransformException {

        for (int i = 0; i < 2; i++) {
            List<List<List<Double>>> coords = new ArrayList<>();
            List<List<Double>> inner = new ArrayList<>();
            int index = rand.nextInt(33);
            System.out.println(repository.findAll().get(index).getGeometry().getCoordinates().get(0).getCoordinates());
            for (int j = 0; j < repository.findAll().get(index).getGeometry().getCoordinates().get(0).getCoordinates().toArray().length; j++) {
                List<Double> toAdd = new ArrayList<>();
                toAdd.add(repository.findAll().get(index).getGeometry().getCoordinates().get(0).getCoordinates().get(j).getX());
                toAdd.add(repository.findAll().get(index).getGeometry().getCoordinates().get(0).getCoordinates().get(j).getY());
                inner.add(toAdd);
            }
            coords.add(inner);
            Geometry sourceGeom = new Utility().example(coords);
            Geometry scaledGeom = GeometryTransformationUtils.scaleGeometry(
                    sourceGeom,
                    getRandomDouble(1, 2)
            );
            Geometry translatedGeom = GeometryTransformationUtils.translateGeometry(
                    sourceGeom,
                    getRandomDouble(-500, 500),
                    getRandomDouble(-500, 500)
            );
            Geometry rotatedGeom = GeometryTransformationUtils.rotateGeometry(
                    sourceGeom,
                    getRandomDouble(0, 360)
            );
            LOGGER.info("Source geom: [{}]", sourceGeom.toText());
            LOGGER.info("Scaled geom: [{}]", scaledGeom.toText());
            LOGGER.info("Translated geom: [{}]", translatedGeom.toText());
            LOGGER.info("Rotated geom: [{}]", rotatedGeom.toText());

        }
        return repository.findAll().get(0);
    }


    @DeleteMapping("/drones/{id}")
    void deleteEmployee(@PathVariable String id) {
        repository.deleteById(id);
    }

    private static double getRandomDouble(double lowerBound, double upperBound) {
        Random r = new Random(System.currentTimeMillis());
        return lowerBound + (upperBound - lowerBound) * r.nextDouble();
    }
}
