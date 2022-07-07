package com.exrade.runtime.kyc.qii;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DateStringSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String dateString,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
                          throws IOException, JsonProcessingException {
    	if(dateString != null && dateString.contains("T"))
    		jsonGenerator.writeObject(dateString.substring(0, dateString.indexOf("T")));
    	else
    		jsonGenerator.writeObject(dateString);
    }
}