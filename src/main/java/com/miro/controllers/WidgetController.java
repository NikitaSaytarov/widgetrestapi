package com.miro.controllers;

import com.miro.core.Widget;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.services.stringSerializer.JsonSerializer;
import com.miro.services.widgetManager.WidgetServiceImpl;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class WidgetController {

    private final WidgetServiceImpl widgetService;
    private final JsonSerializer jsonSerializer;

    public WidgetController(WidgetServiceImpl widgetService, JsonSerializer jsonSerializer) {
        Validate.notNull(widgetService, "widgetService can't be null");
        Validate.notNull(jsonSerializer, "jsonSerializer can't be null");

        this.jsonSerializer = jsonSerializer;
        this.widgetService = widgetService;
    }

    @RequestMapping(value = "/widget/create", method = RequestMethod.PUT)
    @ResponseBody
    String CreateWidget(HttpServletRequest request) {
        String widthText = request.getParameter("width");
        String heightText = request.getParameter("height");
        String xText = request.getParameter("x");
        String yText = request.getParameter("y");
        String zIndexText = request.getParameter("zIndex");

        double width = Double.parseDouble(widthText);
        double height = Double.parseDouble(heightText);
        double x = Double.parseDouble(xText);
        double y = Double.parseDouble(yText);

        Integer zIndex = null;
        if(zIndexText != null){
            zIndex = Integer.parseInt(zIndexText);
        }

        Widget widget =  widgetService.createWidget(x,y,width,height,zIndex);
        return jsonSerializer.serialize(widget);
    }

    @RequestMapping(value = "/widget/get", method = RequestMethod.GET)
    @ResponseBody
    String GetWidget(HttpServletRequest request) {
        String guidText = request.getParameter("guid");

        UUID guid = UUID.fromString(guidText);
        Widget widget = null;
        try {
            widget = widgetService.getWidget(guid);
        } catch (WidgetNotFoundException e) {
            e.printStackTrace();
        }
        return jsonSerializer.serialize(widget);
    }

}

