package com.miro.core.data.internal;

public final class WidgetLayoutInfo {
    private Double width;
    public Double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    private Double height;
    public Double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    private Double x;
    public Double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    private Double y;
    public Double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    private Integer zIndex;
    public Integer getzIndex() {
        return zIndex;
    }
    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
