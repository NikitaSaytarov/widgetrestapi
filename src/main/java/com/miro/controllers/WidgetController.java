package com.miro.controllers;

import com.miro.core.Widget;
import com.miro.core.data.internal.ImmutableVertex;
import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.core.utils.CustomStringBuilder;
import com.miro.services.stringSerializer.JsonSerializerImpl;
import com.miro.services.widgetManager.WidgetServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
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
@Api("Widget management")
public class WidgetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetController.class);
    private final WidgetServiceImpl widgetService;
    private final JsonSerializerImpl jsonSerializer;
    private final WidgetControllerInputParametersValidator validator = new WidgetControllerInputParametersValidator();

    public WidgetController(WidgetServiceImpl widgetService, JsonSerializerImpl jsonSerializer) {
        Validate.notNull(widgetService, "widgetService can't be null");
        Validate.notNull(jsonSerializer, "jsonSerializer can't be null");

        this.jsonSerializer = jsonSerializer;
        this.widgetService = widgetService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create widget with specific parameters")
    public ResponseEntity<?> CreateWidget(@RequestParam(value = "x") String xText,
                                          @RequestParam(value = "y") String yText,
                                          @RequestParam(value = "width") String widthText,
                                          @RequestParam(value = "height") String heightText,
                                          @RequestParam(value = "zIndex", required = false) String zIndexText,
                                          HttpServletRequest request,
                                          UriComponentsBuilder componentsBuilder) {
        try {
            var widgetLayoutInfo = validator.ValidateAndGetWidgetLayoutParameters(xText,
                    yText,
                    widthText,
                    heightText,
                    zIndexText);

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
    @ApiOperation(value = "Get widget using GUID key")
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
    @ApiOperation(value = "Update widget layout parameters using GUID key")
    public ResponseEntity<?> UpdateWidget(@PathVariable("guid") String guidText,
                                          @RequestParam(value = "x", required = false) String xText,
                                          @RequestParam(value = "y", required = false) String yText,
                                          @RequestParam(value = "width", required = false) String widthText,
                                          @RequestParam(value = "height", required = false) String heightText,
                                          @RequestParam(value = "zIndex", required = false) String zIndexText,
                                          HttpServletRequest request,
                                          UriComponentsBuilder componentsBuilder){
        try {
            var widgetLayoutInfo = validator.ValidateAndGetWidgetLayoutParameters(xText,
                    yText,
                    widthText,
                    heightText,
                    zIndexText);
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
    @ApiOperation(value = "Get all widget guid's sorted by zIndex")
    public ResponseEntity<?> GetWidgets(HttpServletRequest request){
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

    @RequestMapping(value = "/limit", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get widgets using pagination(limit and offset)")
    public ResponseEntity<?> Pagination(@RequestParam(value = "limit", required = false) String limitText,
                                        @RequestParam(value = "offset", required = false) String offsetText,
                                        HttpServletRequest request){
        try {
            var parametersPair = validator.ValidateAndGetPaginationInputParameter(limitText, offsetText);
            var widgets = widgetService.getWidgets(parametersPair.getKey(), parametersPair.getValue());
            return getResponseEntityForWidgetArray(widgets);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get widgets filtered by intersect specific area")
    public ResponseEntity<?> Filtration(@RequestParam(value = "x1") String x1Text,
                                        @RequestParam(value = "x2") String x2Text,
                                        @RequestParam(value = "y1") String y1Text,
                                        @RequestParam(value = "y2") String y2Text,
                                        HttpServletRequest request){
        try {
            var vertexParametersPair = validator.ValidateAndGetFilterInputParameter(x1Text, y1Text, x2Text, y2Text);
            var x1 = vertexParametersPair.getKey().getX();
            var y1 = vertexParametersPair.getKey().getY();
            var x2 = vertexParametersPair.getValue().getX();
            var y2 = vertexParametersPair.getValue().getY();

            var widgets = widgetService.filterAndGetWidgets(x1, x2, y1, y2);
            return getResponseEntityForWidgetArray(widgets);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex){
            LOGGER.error("Request - " + request.getMethod() + ". Server unhandled error.", ex);
            return new ResponseEntity<>("Server handle request error. Details: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{guid}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete widget using GUID key")
    public ResponseEntity<?> DeleteWidget(@PathVariable("guid") String guidText,
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

    private ResponseEntity<?> getResponseEntityForWidgetArray(Widget[] widgets) {
        if(widgets.length > 0){
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            responseHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            return  new ResponseEntity<>(jsonSerializer.serialize(widgets),responseHeaders, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private final class WidgetControllerInputParametersValidator {

        public WidgetLayoutInfo ValidateAndGetWidgetLayoutParameters(String xText, String yText, String widthText, String heightText, String zIndexText){

            var sb = new CustomStringBuilder();

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
                    width = Double.parseDouble(widthText);
                    if(width < 0){
                        sb.appendLine("The 'width' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    throw  new IllegalArgumentException("The 'width' parameter has wrong format");
                }
            }

            if (heightText == null || heightText.isEmpty()) {
                sb.appendLine("The 'height' parameter must not be null or empty");
            }
            else{
                try {
                    height = Double.parseDouble(heightText);
                    if(height < 0){
                        sb.appendLine("The 'height' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'height height' parameter has wrong format");
                }
            }

            if (xText == null || xText.isEmpty()) {
                sb.appendLine("The 'x' parameter must not be null or empty");
            }
            else{
                try {
                    x = Double.parseDouble(xText);
                    if(x < 0){
                        sb.appendLine("The 'x' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'x' parameter has wrong format");
                }
            }

            if (yText == null || yText.isEmpty()) {
                sb.appendLine("The 'y' parameter must not be null or empty");
            }
            else{
                try {
                    y = Double.parseDouble(yText);
                    if(y < 0){
                        sb.appendLine("The 'y' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'y' parameter has wrong format");
                }
            }

            if(zIndexText != null){
                try {
                    zIndex = Integer.parseInt(zIndexText);
                    if(zIndex < 0){
                        sb.appendLine("The 'zIndex' can't be negative");
                    }}
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

        public Pair<Integer, Integer> ValidateAndGetPaginationInputParameter(String limitText, String offsetText) {
            Integer limit = 0;
            Integer offset = 0;

            var sb = new CustomStringBuilder();

            if (limitText == null) {
                limit = 10;
            }
            else{
                try {
                    limit = Integer.parseInt(limitText);

                    if(limit < 0){
                        sb.appendLine("The 'limit' can't be negative");
                    }
                    else if(limit > 500){
                        sb.appendLine("The 'limit' parameter more than 500");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'limit' parameter has wrong format");
                }
            }

            if (offsetText != null && !offsetText.isEmpty()) {
                try {
                    offset = Integer.parseInt(offsetText);
                    if(offset < 0){
                        sb.appendLine("The 'offset' can't be negative");
                    }}
                catch (NumberFormatException ex){
                    sb.appendLine("The 'offset' parameter has wrong format");
                }
            }

            if(sb.length() > 0){
                throw new IllegalArgumentException(sb.toString());
            }
            else{
                return Pair.of(limit, offset);
            }
        }

        public Pair<ImmutableVertex, ImmutableVertex> ValidateAndGetFilterInputParameter(String x1Text, String x2Text, String y1Text, String y2Text) {
            double x1 =0.0d;
            double y1 =0.0d;
            double x2 =0.0d;
            double y2 =0.0d;

            var sb = new CustomStringBuilder();

            if (x1Text == null || x1Text.isEmpty()) {
                sb.appendLine("The 'x1' parameter must not be null or empty");
            }
            else{
                try {
                    x1 = Double.parseDouble(x1Text);
                    if(x1 < 0){
                        sb.appendLine("The 'x1' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'x1' parameter has wrong format");
                }
            }
            if (y1Text == null || y1Text.isEmpty()) {
                sb.appendLine("The 'y1' parameter must not be null or empty");
            }
            else{
                try {
                    y1 = Double.parseDouble(y1Text);
                    if(y1 < 0){
                        sb.appendLine("The 'y1' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'y1' parameter has wrong format");
                }
            }
            if (x2Text == null || x2Text.isEmpty()) {
                sb.appendLine("The 'x2' parameter must not be null or empty");
            }
            else{
                try {
                    x2 = Double.parseDouble(x2Text);
                    if(x2 < 0){
                        sb.appendLine("The 'x2' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'x2' parameter has wrong format");
                }
            }
            if (y2Text == null || y2Text.isEmpty()) {
                sb.appendLine("The 'y2' parameter must not be null or empty");
            }
            else{
                try {
                    y2 = Double.parseDouble(y2Text);
                    if(y2 < 0){
                        sb.appendLine("The 'y2' can't be negative");
                    }
                }
                catch (NumberFormatException ex){
                    sb.appendLine("The 'y2' parameter has wrong format");
                }
            }

            if(sb.length() > 0){
                throw new IllegalArgumentException(sb.toString());
            }
            else{
                return Pair.of(new ImmutableVertex(x1,y1), new ImmutableVertex(x2,y2));
            }
        }
    }
}

