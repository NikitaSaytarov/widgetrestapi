package com.miro.services.widgetManager;

import com.miro.core.dto.WidgetDto;
import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.exceptions.WidgetNotFoundException;

import java.util.UUID;

interface WidgetService {
    WidgetDto createWidget(double x, double y, double width, double height, Integer zIndex);
    WidgetDto getWidget(UUID widgetGuid) throws WidgetNotFoundException;
    void updateWidget(UUID widgetGuid, WidgetLayoutInfo widgetInfo) throws WidgetNotFoundException;
    WidgetDto[] getAllWidgets();
    void removeWidget(UUID widgetGuid) throws WidgetNotFoundException;
    WidgetDto[] getWidgets(int limit, int offset);
    WidgetDto[] filterAndGetWidgets(double x1, double x2, double y1, double y2);
}



