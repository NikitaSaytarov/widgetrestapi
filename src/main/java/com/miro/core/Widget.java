package com.miro.core;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Widget {

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

    private LocalDateTime updatedAtUtc;
    public LocalDateTime  getUpdatedAtUtc() {
        return updatedAtUtc;
    }
    public void setUpdatedAtUtc(LocalDateTime updatedAtUtc) {
        this.updatedAtUtc = updatedAtUtc;
    }

    private UUID guid;
    public UUID getGuid() {
        return guid;
    }
    public void setGuid(UUID guid) {
        this.guid = guid;
    }
}
