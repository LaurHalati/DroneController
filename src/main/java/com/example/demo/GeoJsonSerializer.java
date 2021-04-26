package com.example.demo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.elasticsearch.core.geo.GeoJsonLineString;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;
import org.springframework.data.geo.Point;

import java.io.IOException;

public class GeoJsonSerializer extends StdSerializer<GeoJsonPolygon> {
    public GeoJsonSerializer() {
        super(GeoJsonPolygon.class);
    }

    @Override
    public void serialize(final GeoJsonPolygon geoJsonPolygon,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", geoJsonPolygon.getType());
        jsonGenerator.writeFieldName("coordinates");
        jsonGenerator.writeStartArray();
        for (final GeoJsonLineString coordinateList : geoJsonPolygon.getCoordinates()) {
            jsonGenerator.writeStartArray();
            for (final Point coordinate : coordinateList.getCoordinates()) {
                jsonGenerator.writeStartArray();
                jsonGenerator.writeNumber(coordinate.getX());
                jsonGenerator.writeNumber(coordinate.getY());
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
