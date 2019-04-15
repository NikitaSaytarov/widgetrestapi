package com.miro.services.stringSerializer;

import com.google.gson.Gson;
import com.miro.core.Widget;
import org.springframework.stereotype.Service;

@Service
public class JsonSerializer implements StringSerializer {

    private final Gson gson = new Gson();

    public JsonSerializer() {
    }

    @Override
    public <T extends Widget> String serialize(T widget) {
        return gson.toJson(widget);
    }

    @Override
    public <T extends Widget> T deserialize(String widgetString) {
        Widget widget = gson.fromJson(widgetString, Widget.class);
        return (T) widget;
    }
}
