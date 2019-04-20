package com.miro.core.mapping;

import com.miro.core.dto.WidgetDto;
import com.miro.core.data.internal.WidgetInternal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface WidgetMapper {

    WidgetMapper INSTANCE = Mappers.getMapper( WidgetMapper.class );

    WidgetDto[] mapArray(WidgetInternal[] widgetsInternal);

    default WidgetDto map(WidgetInternal widgetInternal) {
        var widget = new WidgetDto();
        widget.setX(widgetInternal.getLayout().getVertex().getX());
        widget.setY(widgetInternal.getLayout().getVertex().getY());
        widget.setWidth(widgetInternal.getLayout().getSize().getWidth());
        widget.setHeight(widgetInternal.getLayout().getSize().getHeight());
        widget.setzIndex(widgetInternal.getLayout().getzIndex());
        widget.setUpdatedAtUtc(widgetInternal.getLayout().getUpdatedAtUtc());
        widget.setGuid(widgetInternal.getGuid());

        return widget;
    }

}
