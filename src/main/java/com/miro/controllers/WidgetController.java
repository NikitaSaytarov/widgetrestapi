package com.miro.controllers;

import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.core.utils.CustomStringBuilder;
import com.miro.services.stringSerializer.JsonSerializer;
import com.miro.services.widgetManager.WidgetServiceImpl;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/widgets")
public class WidgetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);
    private final WidgetServiceImpl widgetService;
    private final JsonSerializer jsonSerializer;
    private final WidgetControllerInputParametersValidator validator = new WidgetControllerInputParametersValidator();

    public WidgetController(WidgetServiceImpl widgetService, JsonSerializer jsonSerializer) {
        Validate.notNull(widgetService, "widgetService can't be null");
        Validate.notNull(jsonSerializer, "jsonSerializer can't be null");

        this.jsonSerializer = jsonSerializer;
        this.widgetService = widgetService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> CreateWidget(HttpServletRequest request, UriComponentsBuilder componentsBuilder) {
        try {
            var widgetLayoutInfo = validator.ValidateAndGetWidgetLayoutParameters(request);
            var widget =  widgetService.createWidget(widgetLayoutInfo.getX(),
                    widgetLayoutInfo.getY(),
                    widgetLayoutInfo.getWidth(),
                    widgetLayoutInfo.getHeight(),
                    widgetLayoutInfo.getzIndex());

            HttpHeaders responseHeaders = new HttpHeaders();
            var uriComponents = componentsBuilder.path("api/v1/widgets/{guid}").buildAndExpand(widget.getGuid());
            responseHeaders.setLocation(uriComponents.toUri());
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            responseHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            return ResponseEntity.created(uriComponents.toUri())
                    .headers(responseHeaders)
                    .body(jsonSerializer.serialize(widget));
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{guid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> GetWidget(@PathVariable("guid") String guidText,
                                       Model model,
                                       HttpServletRequest request) {
        try {
            var guid = validator.ValidateAndGetGuidInputParameter(guidText);
            var widget = widgetService.getWidget(guid);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            responseHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            return  new ResponseEntity<>(jsonSerializer.serialize(widget),responseHeaders,HttpStatus.OK);
        } catch (WidgetNotFoundException e) {
            return new ResponseEntity<>("Widget not found",HttpStatus.NOT_FOUND);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{guid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity UpdateWidget(@PathVariable("guid") String guidText,
                                       HttpServletRequest request,
                                       UriComponentsBuilder componentsBuilder){
        try {
            var widgetLayoutInfo = validator.ValidateAndGetWidgetLayoutParameters(request);
            var guid = validator.ValidateAndGetGuidInputParameter(guidText);
            widgetService.updateWidget(guid, widgetLayoutInfo);

            HttpHeaders responseHeaders = new HttpHeaders();
            var uriComponents = componentsBuilder.path("api/v1/widgets/{guid}").buildAndExpand(guid);
            responseHeaders.setLocation(uriComponents.toUri());

            return new ResponseEntity(responseHeaders, HttpStatus.OK);
        }
        catch (WidgetNotFoundException e) {
            return new ResponseEntity<>("Widget not found",HttpStatus.NOT_FOUND);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> GetWidgets(HttpServletRequest request){
        try {
            var allWidgets = widgetService.getAllWidgets();
            if(allWidgets.length > 0){
                var widgetsGuid = Arrays.stream(allWidgets).map(w -> w.getGuid()).toArray();

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.APPLICATION_JSON);
                responseHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                return  new ResponseEntity<>(jsonSerializer.serialize(widgetsGuid),responseHeaders,HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{guid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> DeleteWidget(@PathVariable("guid") String guidText,
                                               HttpServletRequest request){
        try {
            var guid = validator.ValidateAndGetGuidInputParameter(guidText);
            widgetService.removeWidget(guid);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (WidgetNotFoundException e) {
            return new ResponseEntity(HttpStatus.OK); //DELETE is idempotent
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private final class WidgetControllerInputParametersValidator {

        public WidgetLayoutInfo ValidateAndGetWidgetLayoutParameters(HttpServletRequest request){

            var sb = new CustomStringBuilder();

            var widthText = request.getParameter("width");
            var heightText = request.getParameter("height");
            var xText = request.getParameter("x");
            var yText = request.getParameter("y");
            var zIndexText = request.getParameter("zIndex");

            double width = 0.0d;
            double height =0.0d;
            double x =0.0d;
            double y =0.0d;
            Integer zIndex = null;

            if (widthText == null || widthText.isEmpty()) {
                sb.appendLine("The 'width' parameter must not be null or empty");
            }
            else{
                try {
                    width = Double.parseDouble(widthText);}
                catch (NumberFormatException ex){
                    throw  new IllegalArgumentException("The 'width' parameter has wrong format");
                }
            }

            if (heightText == null || heightText.isEmpty()) {
                sb.appendLine("The 'height' parameter must not be null or empty");
            }
            else{
                try {
                    height = Double.parseDouble(heightText);}
                catch (NumberFormatException ex){
                    sb.appendLine("The 'height width' parameter has wrong format");
                }
            }

            if (xText == null || xText.isEmpty()) {
                sb.appendLine("The 'x' parameter must not be null or empty");
            }
            else{
                try {
                    x = Double.parseDouble(xText);      }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'x' parameter has wrong format");
                }
            }

            if (yText == null || yText.isEmpty()) {
                sb.appendLine("The 'y' parameter must not be null or empty");
            }
            else{
                try {
                    y = Double.parseDouble(yText);            }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'y' parameter has wrong format");
                }
            }

            if(zIndexText != null){
                try {
                    zIndex = Integer.parseInt(zIndexText);            }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'zIndex' parameter has wrong format");
                }
            }

            if(sb.length() > 0){
                throw new IllegalArgumentException(sb.toString());
            }
            else
            {
                var widgetLayoutInfo = new WidgetLayoutInfo();
                widgetLayoutInfo.setWidth(width);
                widgetLayoutInfo.setHeight(height);
                widgetLayoutInfo.setX(x);
                widgetLayoutInfo.setY(y);
                widgetLayoutInfo.setzIndex(zIndex);
                return widgetLayoutInfo;
            }
        }

        public UUID ValidateAndGetGuidInputParameter(String guidText){
            return ValidateGuidInputParameter(guidText);
        }

        private UUID ValidateGuidInputParameter(String guidText){
            if (guidText == null || guidText.isEmpty()) {
                throw  new IllegalArgumentException("The 'guid' parameter must not be null or empty");
            }
            try {
                return UUID.fromString(guidText);
            }
            catch(IllegalArgumentException ex){
                throw  new IllegalArgumentException("The 'guid' parameter has wrong format");
            }
        }

    }
}

