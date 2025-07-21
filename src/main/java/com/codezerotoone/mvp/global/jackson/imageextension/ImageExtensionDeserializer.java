package com.codezerotoone.mvp.global.jackson.imageextension;

import com.codezerotoone.mvp.domain.image.constant.ImageExtension;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ImageExtensionDeserializer extends JsonDeserializer<ImageExtension> {

    @Override
    public ImageExtension deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return ImageExtension.valueOf(p.getValueAsString().toUpperCase());
    }
}
