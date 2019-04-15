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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
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

        if (widthText == null || widthText.isEmpty()) {
            return new  ResponseEntity<>("The 'width' parameter must not be null or empty", HttpStatus.BAD_REQUEST);
        }

        if (heightText == null || heightText.isEmpty()) {
            return new  ResponseEntity<>("The 'height' parameter must not be null or empty", HttpStatus.BAD_REQUEST);
        }

        if (xText == null || xText.isEmpty()) {
            return new  ResponseEntity<>("The 'x' parameter must not be null or empty", HttpStatus.BAD_REQUEST);
        }

        if (yText == null || yText.isEmpty()) {
            return new  ResponseEntity<>("The 'y' parameter must not be null or empty", HttpStatus.BAD_REQUEST);
        }

        try {
            width = Double.parseDouble(widthText);            }
        catch (NumberFormatException ex){
            return new  ResponseEntity<>("The 'width' parameter has wrong format", HttpStatus.BAD_REQUEST);
        }
        try {
            height = Double.parseDouble(heightText);            }
        catch (NumberFormatException ex){
            return new  ResponseEntity<>("The 'height' parameter has wrong format", HttpStatus.BAD_REQUEST);
        }
        try {
            x = Double.parseDouble(xText);      }
        catch (NumberFormatException ex){
            return new  ResponseEntity<>("The 'x' parameter has wrong format", HttpStatus.BAD_REQUEST);
        }
        try {
            y = Double.parseDouble(yText);            }
        catch (NumberFormatException ex){
            return new  ResponseEntity<>("The 'y' parameter has wrong format", HttpStatus.BAD_REQUEST);
        }

        if(zIndexText != null){
            try {
                zIndex = Integer.parseInt(zIndexText);            }
            catch (NumberFormatException ex){
                return new  ResponseEntity<>("The 'zIndex' parameter has wrong format", HttpStatus.BAD_REQUEST);
            }

        }
        try {
            var widget =  widgetService.createWidget(x,y,width,height,zIndex);
            return ResponseEntity.ok(jsonSerializer.serialize(widget));
        }
        catch (Exception ex){
            LOGGER.error("Server handle request error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/widget/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String>  GetWidget(HttpServletRequest request) {

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

