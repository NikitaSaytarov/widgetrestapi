package com.miro.services.widgetManager;

import com.miro.core.Widget;
import com.miro.core.data.internal.WidgetInternal;
import com.miro.core.data.internal.WidgetLayoutInfo;
import com.miro.core.exceptions.WidgetNotFoundException;
import com.miro.core.mapping.WidgetMapper;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


@Service
public final class WidgetServiceImpl implements WidgetService {

    private static final ReadWriteLock locker = new ReentrantReadWriteLock();
    private static final ConcurrentSkipListSet<WidgetInternal> widgets = new ConcurrentSkipListSet<>(WidgetInternal.SORT_COMPARATOR);
    private final WidgetMapper widgetMapper = WidgetMapper.INSTANCE;

    public WidgetServiceImpl() {
    }
    /**
     * Create widget
     * @param x widget x coordinate
     * @param y widget y coordinate
     * @param width widget width
     * @param height widget height
     * @param zIndex widget zIndex. zIndex can be null
     * @return [Widget] object
     * @throws IllegalArgumentException if the input parameters are wrong
     */
    @Override
    public Widget createWidget(double x, double y, double width, double height, Integer zIndex) {

        Validate.finite(x, "X can't be infinite");
        Validate.finite(y, "Y can't be infinite");
        Validate.finite(width, "Width can't be infinite");
        Validate.finite(height, "Height can't be infinite");

        Validate.notNaN(x, "X can't be NaN");
        Validate.notNaN(y, "Y can't be NaN");
        Validate.notNaN(width, "Width can't be NaN");
        Validate.notNaN(height, "Height can't be NaN");

        Validate.isTrue(width > 0, "Width can't be less than zero");
        Validate.isTrue(height > 0, "Height can't be less than zero");

        if(zIndex != null)
            Validate.isTrue(zIndex >= 0, "Invalid value for zIndex");

        var widgetInternal = new WidgetInternal(UUID.randomUUID());

        var widgetLayoutInfo = new WidgetLayoutInfo();
        widgetLayoutInfo.setX(x);
        widgetLayoutInfo.setY(y);
        widgetLayoutInfo.setWidth(width);
        widgetLayoutInfo.setHeight(height);

        if(zIndex == null){
            locker.readLock().lock();
            try {
                if (widgets.isEmpty()) {
                    zIndex = 0;
                } else {
                    var topWidget = widgets.last();
                    zIndex = topWidget.getLayout().getzIndex() + 1;
                }
            }
            finally {
                locker.readLock().unlock();
            }
        }

        widgetLayoutInfo.setzIndex(zIndex);
        widgetInternal.createWidgetLayout(widgetLayoutInfo);

        locker.writeLock().lock();
        try {
            Optional<WidgetInternal> widgetWithSameIndex = widgets.stream()
                .filter(w -> w.getLayout().getzIndex() == widgetInternal.getLayout().getzIndex())
                .findFirst();

            if(widgetWithSameIndex.isPresent()){
                shiftTailWidgets(widgetWithSameIndex.get());
            }

            widgets.add(widgetInternal);
        }
        finally {
            locker.writeLock().unlock();
        }

        var widget = widgetMapper.map(widgetInternal);
        return widget;
    }

    /**
     * Get widget with a specific guid
     * @param widgetGuid widget guid
     * @return [Widget] object
     * @throws NullPointerException if the widgetGuid or widgetLayoutInfo is null
     * @throws WidgetNotFoundException if widget not found
     */
    @Override
    public Widget getWidget(UUID widgetGuid) throws WidgetNotFoundException {

        Optional<WidgetInternal> widgetAvailability;
        locker.readLock().lock();
        try {
                widgetAvailability = widgets.stream()
                .filter(w -> w.getGuid().compareTo(widgetGuid) == 0)
                .findFirst();
        }
        finally {
            locker.readLock().unlock();
        }

        if(widgetAvailability.isPresent()){
            return widgetMapper.map(widgetAvailability.get());
        }
        throw new WidgetNotFoundException();
    }

    /**
     * Update widget with a specific guid
     * @param widgetGuid widget guid
     * @param widgetLayoutInfo widget layout information
     * @throws NullPointerException if the widgetGuid or widgetLayoutInfo is null
     * @throws WidgetNotFoundException if widget not found
     */
    @Override
    public void updateWidget(UUID widgetGuid, WidgetLayoutInfo widgetLayoutInfo) throws WidgetNotFoundException {
        Validate.notNull(widgetGuid, "widgetGuid can't be null");
        Validate.notNull(widgetLayoutInfo, "widgetLayoutInfo can't be null");

        Optional<WidgetInternal> widgetAvailability;
        locker.readLock().lock();
        try {
            widgetAvailability = widgets.stream()
                    .filter(w -> w.getGuid().compareTo(widgetGuid) == 0)
                    .findFirst();
        }
        finally {
            locker.readLock().unlock();
        }

        if(widgetAvailability.isPresent()){
            WidgetInternal widget;
            try {
                widget = widgetAvailability.get();
            }
            catch (NoSuchElementException e){
                throw new WidgetNotFoundException();
            }
            var zIndex = widgetLayoutInfo.getzIndex();
            var isZIndexWillChange = zIndex != null && widget.getLayout().getzIndex() != zIndex;

            locker.writeLock().lock();
            try {
                if(isZIndexWillChange){
                    var removeResult = widgets.remove(widget);
                    if(!removeResult) {
                        throw new WidgetNotFoundException();
                    };
                    var updatedWidget = new WidgetInternal(widgetGuid);
                    updatedWidget.createWidgetLayout(widgetLayoutInfo);
                    var widgetWithSameIndex = widgets.stream()
                            .filter(w -> w.getLayout().getzIndex() == updatedWidget.getLayout().getzIndex())
                            .findFirst();
                    if(widgetWithSameIndex.isPresent())
                        shiftTailWidgets(widgetWithSameIndex.get());
                    widgets.add(updatedWidget);
                    return;
                }
                widget.updateWidgetLayout(widgetLayoutInfo);
            }
            finally {
                locker.writeLock().unlock();
            }
            return;
        }
        throw new WidgetNotFoundException();
    }

    private void shiftTailWidgets(WidgetInternal widget) {

        var subSet = widgets.tailSet(widget);
        var elementsNeedShift = subSet.toArray(new WidgetInternal[subSet.size()]);

        for (WidgetInternal item : elementsNeedShift) {
            widgets.remove(item);
            item.IncrementZIndex();
            widgets.add(item);
        }
    }

    /**
     * Get all widgets sorted by zIndex
     * @return [Widget]'s array
     */
    @Override
    public Widget[] getAllWidgets() {

        WidgetInternal[] widgetsRaw;
        locker.readLock().lock();
        try {
            widgetsRaw = widgets.toArray(new WidgetInternal[widgets.size()]);
        }
        finally {
            locker.readLock().unlock();
        }

        if(widgetsRaw.length > 0){
            return widgetMapper.mapArray(widgetsRaw);
        }
        return new Widget[0];
    }

    /**
     * Get widgets sorted by zIndex with limit and offset
     * @param limit limit
     * @param offset offset
     * @return [Widget]'s array
     */
    @Override
    public Widget[] getWidgets(int limit, int offset) {
        Validate.isTrue(limit >= 0, "limit can't be negative");
        Validate.isTrue(offset >= 0, "offset can't be negative");

        List<WidgetInternal> setOfWidgets;
        locker.readLock().lock();
        try {
            setOfWidgets = widgets.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
        }
        finally {
            locker.readLock().unlock();
        }

        var setOfWidgetsRaw = setOfWidgets.toArray(new WidgetInternal[setOfWidgets.size()]);

        if(setOfWidgetsRaw.length > 0){
            return widgetMapper.mapArray(setOfWidgetsRaw);
        }
        return new Widget[0];
    }

    @Override
    public Widget[] filterAndGetWidgets(double x1, double x2, double y1, double y2) {
        Validate.isTrue(x1 >= 0, "x1 can't be negative");
        Validate.isTrue(y1 >= 0, "y1 can't be negative");
        Validate.isTrue(x2 >= 0, "x2 can't be negative");
        Validate.isTrue(y2 >= 0, "y2 can't be negative");

        List<WidgetInternal> setOfWidgets;
        locker.readLock().lock();
        try {
            setOfWidgets = widgets.stream()
            .filter(w -> isOverlappingPredicate(new Rectangle2D.Double(
                    w.getLayout().getVertex().getX(),
                    w.getLayout().getVertex().getY(),
                    w.getLayout().getVertex().getX() + w.getLayout().getSize().getWidth(),
                    w.getLayout().getVertex().getY() + w.getLayout().getSize().getHeight()),
                    new Rectangle2D.Double(x1,x2,y1,y2)))
            .collect(Collectors.toList());
        }
        finally {
            locker.readLock().unlock();
        }

        var setOfWidgetsRaw = setOfWidgets.toArray(new WidgetInternal[setOfWidgets.size()]);

        if(setOfWidgetsRaw.length > 0){
            return widgetMapper.mapArray(setOfWidgetsRaw);
        }
        return new Widget[0];
    }

    private boolean isOverlappingPredicate(Rectangle2D widgetRectangle, Rectangle2D area) {
        return widgetRectangle.intersects(area);
    }

    /**
     * Remove widget with a specific guid
     * @param widgetGuid widget guid
     * @throws NullPointerException if the widgetGuid is null
     * @throws WidgetNotFoundException if widget not found
     */
    @Override
    public void removeWidget(UUID widgetGuid) throws WidgetNotFoundException {

        locker.readLock().lock();
        try {
            Optional<WidgetInternal> widgetAvailability = widgets.stream()
                    .filter(w -> w.getGuid().compareTo(widgetGuid) == 0)
                    .findFirst();

            if(widgetAvailability.isPresent()){
                var removedWidget = widgetAvailability.get();
                var result = widgets.remove(removedWidget);
                if(result)
                    return;
            }

            throw new WidgetNotFoundException();
        }
        finally {
            locker.readLock().unlock();
        }
    }
}


