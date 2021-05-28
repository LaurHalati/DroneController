package com.example.demo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.data.elasticsearch.core.geo.GeoJsonPolygon;
import org.springframework.data.geo.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJsonDeserializer extends StdDeserializer<GeoJsonPolygon> {
    public GeoJsonDeserializer() {
        super(GeoJsonPolygon.class);
    }

    @Override
    public GeoJsonPolygon deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Polygon polygon = jsonParser.readValueAs(Polygon.class);
        List<Point> points = new ArrayList<>();
        for (final List<Double> coordinates : polygon.getCoordinates()) {
            points.add(new Point(coordinates.get(0), coordinates.get(1)));
        }
        return GeoJsonPolygon.of(points);
    }
}