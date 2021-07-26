package com.j6crypto.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.j6crypto.to.setup.SetupBase;

import java.io.IOException;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class StateDeserializer extends StdDeserializer<SetupBase> {
  protected StateDeserializer(){
    super(SetupBase.class);
  }
  protected StateDeserializer(Class<?> vc) {
    super(vc);
  }

  protected StateDeserializer(JavaType valueType) {
    super(valueType);
  }

  protected StateDeserializer(StdDeserializer<?> src) {
    super(src);
  }

  @Override
  public SetupBase deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {

    }
    return null;
  }
}
