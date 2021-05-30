package com.example.demo;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GeometryTransformationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeometryTransformationUtils.class);
    private static CoordinateReferenceSystem geographicCrs;

    static Geometry scaleGeometry(Geometry polygon, double scaleFactor) throws FactoryException, TransformException {
        LOGGER.info("Scaling geometry [{}] with a scale factor of [{}].", polygon.toString(), scaleFactor);
        Point centroid = getCentroidInCartesianCrs(polygon);
        return applyTransformation(
                polygon, AffineTransformation.scaleInstance(scaleFactor, scaleFactor, centroid.getX(), centroid.getY())
        );
    }

    static Geometry translateGeometry(Geometry polygon, double metersEastbound, double metersNorthBound)
            throws FactoryException, TransformException {
        LOGGER.info(
                "Translating geometry [{}] with {} meters to the East and {} meters to the North.",
                polygon.toString(), metersEastbound, metersNorthBound
        );
        return applyTransformation(
                polygon, AffineTransformation.translationInstance(metersNorthBound, metersEastbound)
        );
    }

    static Geometry rotateGeometry(Geometry polygon, double angleInDegrees)
            throws FactoryException, TransformException {
        LOGGER.info("Rotating geometry [{}] by [{}] degrees.", polygon.toString(), angleInDegrees);
        double rotationAngleRad = angleInDegrees / 57.295779513;
        Point centroid = getCentroidInCartesianCrs(polygon);
        return applyTransformation(
                polygon, AffineTransformation.rotationInstance(rotationAngleRad, centroid.getX(), centroid.getY())
        );
    }

    private static Point getCentroidInCartesianCrs(Geometry polygonInGeographicCrs)
            throws FactoryException, TransformException {
        MathTransform toCartesian = CRS.findMathTransform(getGeographicCrs(), getCartesianCrs(polygonInGeographicCrs));
        return JTS.transform(polygonInGeographicCrs, toCartesian).getCentroid();
    }

    private static Geometry applyTransformation(Geometry polygonToTransform, AffineTransformation transformation)
            throws FactoryException, TransformException {
        CoordinateReferenceSystem cartesianCrs = getCartesianCrs(polygonToTransform);
        MathTransform toCartesian = CRS.findMathTransform(getGeographicCrs(), cartesianCrs);
        MathTransform toGeographic = CRS.findMathTransform(cartesianCrs, getGeographicCrs());
        Geometry transformedPolygon = transformation.transform(JTS.transform(polygonToTransform, toCartesian));
        return JTS.transform(transformedPolygon, toGeographic);
    }

    private static CoordinateReferenceSystem getCartesianCrs(Geometry polygonInGeographicCrs) throws FactoryException {
        return CRS.decode(
                "AUTO:42001," +
                polygonInGeographicCrs.getCentroid().getY() + "," +
                polygonInGeographicCrs.getCentroid().getX()
        );
    }

    private static CoordinateReferenceSystem getGeographicCrs() throws FactoryException {
        if (geographicCrs == null) {
            geographicCrs = CRS.decode("EPSG:4326");
        }
        return geographicCrs;
    }
}
