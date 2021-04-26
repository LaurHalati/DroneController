package com.example.demo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoShapeField;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Shape;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;


@Document(indexName="drone-plan-laurentiu")
public class DronePlan {
    @Id
    @GeneratedValue
    private String id;
    private String name;
    @Field
    private GeoJsonPolygon geometry;
    @Field(type = FieldType.Date)
    private String startTime;
    @Field(type = FieldType.Date)
    private String endTime;

    public DronePlan(){

    }

    public DronePlan(String id, String name, GeoJsonPolygon geometry, String startTime, String endTime) {
        this.id = id;
        this.name = name;
       // this.coordinates = coordinates;
        this.geometry = geometry;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoJsonPolygon getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonPolygon geometry) {
        this.geometry = geometry;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DronePlan dronePlan = (DronePlan) o;
        return Objects.equals(id, dronePlan.id) && Objects.equals(name, dronePlan.name) && Objects.equals(geometry, dronePlan.geometry) && Objects.equals(startTime, dronePlan.startTime) && Objects.equals(endTime, dronePlan.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, geometry, startTime, endTime);
    }

    @Override
    public String toString() {
        return "DronePlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", geometry=" + geometry +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
