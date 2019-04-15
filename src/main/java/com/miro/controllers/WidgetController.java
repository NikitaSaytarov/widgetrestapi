package com.miro.controllers;

import com.miro.core.Widget;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.services.stringSerializer.JsonSerializer;
import com.miro.services.widgetManager.WidgetServiceImpl;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class WidgetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);

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
    public ResponseEntity<String> CreateWidget(HttpServletRequest request) {
        LOGGER.debug(request.toString());
        try {
            String widthText = request.getParameter("width");
            String heightText = request.getParameter("height");
            String xText = request.getParameter("x");
            String yText = request.getParameter("y");
            String zIndexText = request.getParameter("zIndex");

            double width;
            double height;
            double x;
            double y;
            Integer zIndex = null;

            try {
                width = Double.parseDouble(widthText);
                height = Double.parseDouble(heightText);
                x = Double.parseDouble(xText);
                y = Double.parseDouble(yText);

                if(zIndexText != null){
                    zIndex = Integer.parseInt(zIndexText);
                }
            }
            catch (NullPointerException | NumberFormatException ex){
                LOGGER.info("invalid parameters");
                return new  ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
            }

            var widget =  widgetService.createWidget(x,y,width,height,zIndex);
            return ResponseEntity.ok(jsonSerializer.serialize(widget));
        }
        catch (Exception ex){
            LOGGER.error(ex.toString());
            return new ResponseEntity<>(ex.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/widget/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String>  GetWidget(HttpServletRequest request) {
        LOGGER.debug(request.toString());

        String guidText = request.getParameter("guid");
        UUID guid;
        try {
            guid = UUID.fromString(guidText);
        }
        catch(IllegalArgumentException ex){
            LOGGER.info("invalid parameters");
            return new  ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
        Widget widget = null;
        try {
            widget = widgetService.getWidget(guid);
        } catch (WidgetNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(jsonSerializer.serialize(widget));
    }

}

