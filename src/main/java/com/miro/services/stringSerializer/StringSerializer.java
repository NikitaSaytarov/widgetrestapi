package com.miro.services.stringSerializer;

public interface StringSerializer {
    <T> String serialize(T object);
    <T> T deserialize(String objectString);
}
