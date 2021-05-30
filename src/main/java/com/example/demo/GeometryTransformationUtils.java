package com.example.demo;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GeometryTransformationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryTransformationUtils.class);

    static Geometry scaleGeometry(Geometry polygon, double scaleFactor) {
        LOGGER.info("Scaling geometry [{}] with a scale factor of [{}].", polygon.toString(), scaleFactor);
        return AffineTransformation
                .scaleInstance(scaleFactor, scaleFactor, polygon.getCentroid().getX(), polygon.getCentroid().getY())
                .transform(polygon);
    }

    static Geometry translateGeometry(Geometry polygon, double degreesWestbound, double degreesNorthbound) {
        LOGGER.info(
                "Translating geometry [{}] with {} degrees to the East and {} degrees to the North.",
                polygon.toString(), degreesWestbound, degreesNorthbound
        );
        return AffineTransformation.translationInstance(degreesWestbound, degreesNorthbound).transform(polygon);
    }

    static Geometry rotateGeometry(Geometry polygon, double angleInDegrees) {
        LOGGER.info("Rotating geometry [{}] by [{}] degrees.", polygon.toString(), angleInDegrees);
        double rotationAngleRad = angleInDegrees / 57.295779513;
        return  AffineTransformation
                .rotationInstance(rotationAngleRad, polygon.getCentroid().getX(), polygon.getCentroid().getY())
                .transform(polygon);
    }
}
