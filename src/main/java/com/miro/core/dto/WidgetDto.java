package com.miro.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@ApiModel(value="WidgetDto", description="Widget resource representation")
public final class WidgetDto {

    private Double width;
    public Double getWidth() {
        return width;
    }
    @ApiModelProperty(value = "Widget width", allowableValues = "non-negative double")
    public void setWidth(double width) {
        this.width = width;
    }

    private Double height;
    public Double getHeight() {
        return height;
    }
    @ApiModelProperty(value = "Widget height", allowableValues = "non-negative double")
    public void setHeight(double height) {
        this.height = height;
    }

    private Double x;
    public Double getX() {
        return x;
    }
    @ApiModelProperty(value = "Widget x coordinate", allowableValues = "non-negative double")
    public void setX(double x) {
        this.x = x;
    }

    private Double y;
    public Double getY() {
        return y;
    }
    @ApiModelProperty(value = "Widget y coordinate", allowableValues = "non-negative double")
    public void setY(double y) {
        this.y = y;
    }

    private Integer zIndex;
    public Integer getzIndex() {
        return zIndex;
    }
    @ApiModelProperty(value = "Widget z-Index", allowableValues = "non-negative integer")
    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    private LocalDateTime updatedAtUtc;
    public LocalDateTime  getUpdatedAtUtc() {
        return updatedAtUtc;
    }
    @ApiModelProperty(value = "Widget updated timestamp in utc", allowableValues = "LocalDateTime")
    public void setUpdatedAtUtc(LocalDateTime updatedAtUtc) {
        this.updatedAtUtc = updatedAtUtc;
    }

    private UUID guid;
    public UUID getGuid() {
        return guid;
    }
    @ApiModelProperty(value = "Widget guid", allowableValues = "UUID")
    public void setGuid(UUID guid) {
        this.guid = guid;
    }
}
