package com.example.demo;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;

public class GeoJsonModule extends SimpleModule {
    public GeoJsonModule() {
        this.addSerializer(GeoJsonPolygon.class, new GeoJsonSerializer());
        this.addDeserializer(GeoJsonPolygon.class, new GeoJsonDeserializer());
    }
}