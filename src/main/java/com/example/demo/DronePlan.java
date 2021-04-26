package com.example.demo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoShapeField;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;


@Document(indexName="name")

public class DronePlan {
    private @Id @GeneratedValue String id;
    private String name;
    //private String geometryType;
   // private List<List<Double>> coordinates;

    private Polygon geometry;
    private String startTime;

    private String endTime;
    public DronePlan(){

    }

    public DronePlan(String id, String name, Polygon geometry, String startTime, String endTime) {
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

//    public List<List<Double>> getCoordinates() {
//        return coordinates;
//    }
//
//    public void setCoordinates(List<List<Double>> coordinates) {
//        this.coordinates = coordinates;
//    }

    public Polygon getGeometry() {
        return geometry;
    }

    public void setGeometry(Polygon geometry) {
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
 class Polygon {
    @JsonProperty("type")
     private String type = "Polygon";
     @JsonProperty("coordinates")
     private JsonNode coordinates;
    public Polygon(){

    }

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public Polygon(JsonNode coordinates) {

         this.coordinates = coordinates;
     }


     public  JsonNode getCoordinates() {
         return coordinates;
     }

     public void setCoordinates( JsonNode coordinates) {

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
