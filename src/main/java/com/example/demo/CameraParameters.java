package com.example.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.elasticsearch.annotations.GeoShapeField;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;

import java.util.Objects;

public class CameraParameters {
    @GeoShapeField
    private GeoJsonPolygon geometry;
    @JsonProperty
    private double spatialResolution;
    @JsonProperty
    private double digitalResolutionWidth ;
    @JsonProperty
    private double digitalResolutionHeight ;
    @JsonProperty
    private double sensorWidth ;
    @JsonProperty
    private double focalLength ;

    public CameraParameters(){

    }

    public CameraParameters(GeoJsonPolygon geometry, double spatialResolution, double digitalResolutionWidth, double digitalResolutionHeight, double sensorWidth, double focalLength) {
        this.geometry = geometry;
        this.spatialResolution = spatialResolution;
        this.digitalResolutionWidth = digitalResolutionWidth;
        this.digitalResolutionHeight = digitalResolutionHeight;
        this.sensorWidth = sensorWidth;
        this.focalLength = focalLength;
    }

    public GeoJsonPolygon getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonPolygon geometry) {
        this.geometry = geometry;
    }

    public double getSpatialResolution() {
        return spatialResolution;
    }

    public void setSpatialResolution(double spatialResolution) {
        this.spatialResolution = spatialResolution;
    }

    public double getDigitalResolutionWidth() {
        return digitalResolutionWidth;
    }

    public void setDigitalResolutionWidth(double digitalResolutionWidth) {
        this.digitalResolutionWidth = digitalResolutionWidth;
    }

    public double getDigitalResolutionHeight() {
        return digitalResolutionHeight;
    }

    public void setDigitalResolutionHeight(double digitalResolutionHeight) {
        this.digitalResolutionHeight = digitalResolutionHeight;
    }

    public double getSensorWidth() {
        return sensorWidth;
    }

    public void setSensorWidth(double sensorWidth) {
        this.sensorWidth = sensorWidth;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CameraParameters that = (CameraParameters) o;
        return Double.compare(that.spatialResolution, spatialResolution) == 0 && Double.compare(that.digitalResolutionWidth, digitalResolutionWidth) == 0 && Double.compare(that.digitalResolutionHeight, digitalResolutionHeight) == 0 && Double.compare(that.sensorWidth, sensorWidth) == 0 && Double.compare(that.focalLength, focalLength) == 0 && Objects.equals(geometry, that.geometry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geometry, spatialResolution, digitalResolutionWidth, digitalResolutionHeight, sensorWidth, focalLength);
    }

    @Override
    public String toString() {
        return "CameraParameters{" +
                "geometry=" + geometry +
                ", spatialResolution=" + spatialResolution +
                ", digitalResolutionWidth=" + digitalResolutionWidth +
                ", digitalResolutionHeight=" + digitalResolutionHeight +
                ", sensorWidth=" + sensorWidth +
                ", focalLength=" + focalLength +
                '}';
    }
}

