package com.kreuterkeule.food.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kreuterkeule.food.entity.UserEntity;

import java.io.IOException;

public class UserSerializer extends JsonSerializer<UserEntity> {

    @Override
    public void serialize(
            UserEntity value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeStringField("username", value.getUsername());
        jgen.writeNumberField("info", value.getId());
        jgen.writeNumberField("own_recipes", value.getOwn_recipes().size());
        jgen.writeEndObject();
    }

}
