package com.example.demo;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utility {

    public Geometry example(List<List<List<Double>>> polygonCoordinates) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        List<List<Double>> borderCoordinates = polygonCoordinates.get(0);
        Coordinate[] coordinates =
                borderCoordinates.stream().map(c -> new Coordinate(c.get(0), c.get(1))).toArray(Coordinate[]::new);

        geometryFactory.createPoint();
        return geometryFactory.createPolygon(coordinates);
    }
    public List<List<List<Double>>> example2(Geometry polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();
        return Arrays.stream(coordinates)
                .map(c -> Collections.singletonList(Arrays.asList(c.getX(), c.getY())))
                .collect(Collectors.toList());
    }


    public Geometry createLine(ArrayList<ArrayList<Double>> lineStringCoords) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        LineString[] ls = new LineString[lineStringCoords.toArray().length];
        for (int i=0;i<lineStringCoords.toArray().length;i++){
            ArrayList<Double> borderCoordinates = lineStringCoords.get(i);
            Coordinate[] coordinates =
                    borderCoordinates.stream().map(c -> new Coordinate(borderCoordinates.get(0), borderCoordinates.get(1))).toArray(Coordinate[]::new);
            ls[i]=geometryFactory.createLineString(coordinates);
            geometryFactory.createPoint();
        }

        return geometryFactory.createMultiLineString(ls);
    }
}
