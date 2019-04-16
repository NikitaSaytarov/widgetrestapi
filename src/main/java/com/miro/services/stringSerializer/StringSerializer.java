package com.miro.services.stringSerializer;

import com.miro.core.Widget;

public interface StringSerializer {
    <T> String serialize(T object);
    <T> T deserialize(String objectString);
}
