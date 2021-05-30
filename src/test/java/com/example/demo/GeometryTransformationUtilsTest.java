package com.example.demo;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

class GeometryTransformationUtilsTest {

    @Test
    void scaleGeometry() {
        System.out.println(GeometryTransformationUtils.scaleGeometry(getTestGeometry(), 1.5));
    }

    @Test
    void translateGeometry() {
        System.out.println(GeometryTransformationUtils.translateGeometry(getTestGeometry(), 1e-4, -5e-3));
    }

    @Test
    void rotateGeometry() {
        System.out.println(GeometryTransformationUtils.rotateGeometry(getTestGeometry(), 45));
    }

    private Geometry getTestGeometry() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(23.567390441894528, 46.75938556555028),
                new Coordinate(23.56696128845215, 46.7577979560333),
                new Coordinate(23.57013702392578, 46.75332887809298),
                new Coordinate(23.57142448425293, 46.752152743349626),
                new Coordinate(23.572368621826172, 46.75227035797897),
                new Coordinate(23.57245445251465, 46.752976040365034),
                new Coordinate(23.57288360595703, 46.75356410196157),
                new Coordinate(23.572540283203125, 46.75438737741651),
                new Coordinate(23.572368621826172, 46.75644551102998),
                new Coordinate(23.567390441894528, 46.75938556555028)
        });
    }
}
