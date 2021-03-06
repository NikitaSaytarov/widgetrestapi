package com.miro.core.data.internal;

import org.apache.commons.lang3.Validate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;


public final class WidgetInternal implements Comparable<WidgetInternal>{

    public static final Comparator<WidgetInternal> SORT_COMPARATOR =
            Comparator.comparing((WidgetInternal widget) -> widget.getLayout().getzIndex())
            .thenComparing(w -> w.getGuid());

    private volatile ImmutableLayout layout;
    public ImmutableLayout getLayout() {
        return layout;
    }

    private final UUID guid;
    public UUID getGuid() {
        return guid;
    }

    public WidgetInternal(UUID guid) {
        Validate.notNull(guid, "WidgetInternal guid can't be null");
        this.guid = guid;
    }

    public void createWidgetLayout(WidgetLayoutInfo widgetLayoutInfo) {
        Validate.notNull(widgetLayoutInfo, "widgetLayoutInfo can't be null");
        Validate.notNull(widgetLayoutInfo.getHeight(), "widgetLayoutInfo height can't be null");
        Validate.notNull(widgetLayoutInfo.getWidth(), "widgetLayoutInfo width  can't be null");
        Validate.notNull(widgetLayoutInfo.getX(), "widgetLayoutInfo x  can't be null");
        Validate.notNull(widgetLayoutInfo.getY(), "widgetLayoutInfo y can't be null");
        Validate.notNull(widgetLayoutInfo.getzIndex(), "widgetLayoutInfo zIndex can't be null");

        double width =  widgetLayoutInfo.getWidth();
        double height = widgetLayoutInfo.getHeight();
        var widgetLayoutSize = new ImmutableSize(width,height);

        double x = widgetLayoutInfo.getX();
        double y = widgetLayoutInfo.getY();
        var widgetLayoutVertex = new ImmutableVertex(x,y);

        var widgetLayoutZIndex = widgetLayoutInfo.getzIndex();

        var newWidgetLayout = new ImmutableLayout(widgetLayoutSize,widgetLayoutVertex,widgetLayoutZIndex);
        layout = newWidgetLayout;
    }

    public void updateWidgetLayout(WidgetLayoutInfo widgetLayoutInfo) {
        Validate.notNull(widgetLayoutInfo, "widgetLayoutInfo can't be null");
        if(layout == null)
            throw new IllegalStateException("layout == null");

        ImmutableSize layoutSize = layout.getSize();
        ImmutableVertex layoutVertex = layout.getVertex();

        ImmutableSize widgetLayoutSize;
        if(widgetLayoutInfo.getHeight() ==  null && widgetLayoutInfo.getWidth() == null){
            widgetLayoutSize = (ImmutableSize) layoutSize.clone();
        }
        else{
            double width = widgetLayoutInfo.getWidth() != null ? widgetLayoutInfo.getWidth() : layoutSize.getWidth();
            double height = widgetLayoutInfo.getHeight() != null ? widgetLayoutInfo.getHeight() : layoutSize.getHeight();
            widgetLayoutSize = new ImmutableSize(width,height);
        }

        ImmutableVertex widgetLayoutVertex;
        if(widgetLayoutInfo.getX() ==  null && widgetLayoutInfo.getY() == null){
            widgetLayoutVertex = (ImmutableVertex) layoutVertex.clone();
        }
        else{
            double x = widgetLayoutInfo.getX() != null ? widgetLayoutInfo.getX() : layoutVertex.getX();
            double y = widgetLayoutInfo.getY() != null ? widgetLayoutInfo.getY() : layoutVertex.getY();
            widgetLayoutVertex = new ImmutableVertex(x,y);
        }

        var widgetLayoutZIndex = widgetLayoutInfo.getzIndex() != null ? widgetLayoutInfo.getzIndex() : layout.getzIndex();
        var newWidgetLayout = new ImmutableLayout(widgetLayoutSize,widgetLayoutVertex,widgetLayoutZIndex);
        layout = newWidgetLayout;
    }

    public void IncrementZIndex() {
        if(layout == null)
            throw new IllegalStateException("layout == null");

        ImmutableSize layoutSize = layout.getSize();
        ImmutableVertex layoutVertex = layout.getVertex();

        var updatedLayout = new ImmutableLayout((ImmutableSize)layoutSize.clone(), (ImmutableVertex)layoutVertex.clone(),layout.getzIndex() + 1);
        layout = updatedLayout;
    }


    @Override
    public int compareTo(WidgetInternal o) {
        return Integer.compare(layout.getzIndex(), o.getLayout().getzIndex());
    }

    @Override
    public String toString() {
        return String.format("WidgetInternal guid=%s}", guid);
    }
}
