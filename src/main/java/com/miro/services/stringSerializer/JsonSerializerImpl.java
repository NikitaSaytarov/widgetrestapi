package com.miro.services.stringSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miro.core.dto.WidgetDto;
import org.springframework.stereotype.Service;

@Service
public class JsonSerializerImpl implements StringSerializer {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonSerializerImpl() {
    }

    @Override
    public <T> String serialize(T object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T deserialize(String objectString) {
        WidgetDto widgetDto = gson.fromJson(objectString, WidgetDto.class);
        return (T) widgetDto;
    }
}
