package io.scalecube.gateway.rabbitmq.serialization.json;

import io.scalecube.gateway.rabbitmq.MessageSerialization;

import com.dyuproject.protostuff.JsonIOUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import com.dyuproject.protostuff.Schema;


public class JsonMessageSerialization implements MessageSerialization{

private static final RecyclableLinkedBuffer recyclableLinkedBuffer = new RecyclableLinkedBuffer();
  
  @Override
  public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
    
    Schema<T> schema = SchemaCache.getOrCreate(clazz);
    T message = schema.newMessage();
    ByteBuf bb =  Unpooled.copiedBuffer(data, 0, data.length);

    try {
      JsonIOUtil.mergeFrom(data, message, schema, false);
    } catch (Exception e) {
      throw new DecoderException(e.getMessage(), e);
    }

    return message;
  }

  @Override
  public <T> byte[] serialize(T value, Class<T> clazz) throws Exception {
    
    Schema<T> schema = SchemaCache.getOrCreate(clazz);
    
    try (RecyclableLinkedBuffer rlb = recyclableLinkedBuffer.get()) {
      try {
        return JsonIOUtil.toByteArray(value, schema, false) ;
      } catch (Exception e) {
        throw new EncoderException(e.getMessage(), e);
      }
    } 
  }

  
  public <T> byte[] serialize(Object value) throws Exception {
    return null;
  }
 
}

