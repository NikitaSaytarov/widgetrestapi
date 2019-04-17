package com.miro.services.widgetManager;

import com.miro.core.Widget;
import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.exceptions.WidgetNotFoundException;

import java.util.UUID;

interface WidgetService {
    Widget createWidget(double x, double y, double width, double height, Integer zIndex);
    Widget getWidget(UUID widgetGuid) throws WidgetNotFoundException;
    void updateWidget(UUID widgetGuid, WidgetLayoutInfo widgetInfo) throws WidgetNotFoundException;
    Widget[] getAllWidgets();
    void removeWidget(UUID widgetGuid) throws WidgetNotFoundException;
    Widget[] getWidgets(int limit, int offset);
    Widget[]  filterAndGetWidgets(double x1, double x2, double y1, double y2);
}



