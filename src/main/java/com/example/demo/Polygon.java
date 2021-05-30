package com.example.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.elasticsearch.core.geo.GeoJson;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Polygon {
    @JsonProperty("type")
    private String type = "Polygon";
    @JsonProperty("coordinates")
    private List<List<List<Double>>> coordinates = new ArrayList<>();
    public Polygon(){

    }

    public String getType() {
        return type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates.isEmpty() ? Collections.emptyList() : coordinates.get(0);
    }

    public void setType(String type) {
        this.type = type;
    }

    public Polygon(List<List<List<Double>>> coordinates) {

        this.coordinates = coordinates;
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {

        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
        return Objects.equals(type, polygon.type) && Objects.equals(coordinates, polygon.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, coordinates);
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}