package com.miro.core.mapping;

import com.miro.core.Widget;
import com.miro.core.data.internal.WidgetInternal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface WidgetMapper {

    WidgetMapper INSTANCE = Mappers.getMapper( WidgetMapper.class );

    Widget[] mapArray(WidgetInternal[] widgetsInternal);

    default Widget map(WidgetInternal widgetInternal) {
        var widget = new Widget();
        widget.setX(widgetInternal.getLayout().getVertex().getX());
        widget.setY(widgetInternal.getLayout().getVertex().getY());
        widget.setWidth(widgetInternal.getLayout().getSize().getWidth());
        widget.setHeight(widgetInternal.getLayout().getSize().getHeight());
        widget.setzIndex(widgetInternal.getLayout().getzIndex());
        widget.setUpdatedAtUtc(widgetInternal.getLayout().getUpdatedAtUtc().toLocalDateTime());
        widget.setGuid(widgetInternal.getGuid());

        return widget;
    }

}
