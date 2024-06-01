package com.kreuterkeule.food.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kreuterkeule.food.entity.Tag;

import java.io.IOException;

public class TagSerializer extends JsonSerializer<Tag> {

    @Override
    public void serialize(
            Tag value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeStringField("name", value.getName());
        jgen.writeNumberField("recipes", value.getRecipes().size());
        jgen.writeEndObject();
    }

}
