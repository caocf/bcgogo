package com.bcgogo.common.jackson;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-16
 * Time: 下午5:34
 */
public class LongSerializer extends JsonSerializer<Long> {
  @Override
  public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    jsonGenerator.writeString(aLong.toString());
  }
}
