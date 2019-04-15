package com.miro.services.stringSerializer;

import com.miro.core.Widget;

public interface StringSerializer {
    <T extends Widget> String serialize(T type);
    <T extends Widget> T deserialize(String string);
}
