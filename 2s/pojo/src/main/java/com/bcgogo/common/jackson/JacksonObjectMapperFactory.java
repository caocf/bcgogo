package com.bcgogo.common.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.deser.CustomDeserializerFactory;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.springframework.beans.factory.FactoryBean;

/**
 * 此类是配置servlet-context时为Jackson Json生成objectMapper的工厂类.
 * User: Jimuchen
 * Date: 12-7-10
 * Time: 下午5:38
 */
public class JacksonObjectMapperFactory implements FactoryBean<ObjectMapper>{

  @Override
  public ObjectMapper getObject() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    //生成Json时不生成null值字段
//    mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

    //将null值转为空字符串
    StdSerializerProvider sp = new StdSerializerProvider();
    sp.setNullValueSerializer(new NullSerializer());
    mapper.setSerializerProvider(sp);

    CustomSerializerFactory csf = new CustomSerializerFactory();
    csf.addSpecificMapping(Long.class, new LongSerializer());
    mapper.setSerializerFactory(csf);

    CustomDeserializerFactory cdf = new CustomDeserializerFactory();
    mapper.setDeserializerProvider(new StdDeserializerProvider(cdf));

    mapper.getSerializationConfig().set(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    return mapper;
  }

  @Override
  public Class<?> getObjectType() {
    return ObjectMapper.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
