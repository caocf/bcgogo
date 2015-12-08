package com.bcgogo.common.jackson;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-7-10
 * Time: 下午4:01
 */
public class NullSerializer extends JsonSerializer<Object> {

  @Override
  public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeString("");
  }
}
