package com.miro.core.data.internal;

import org.apache.commons.lang3.Validate;

public final class ImmutableSize implements Cloneable{
    private final double width;
    public double getWidth() {
        return width;
    }

    private final double height;
    public double getHeight() {
        return height;
    }

    ImmutableSize(double width, double height) {
        Validate.notNaN(width, "ImmutableSize width can't be NaN");
        Validate.notNaN(height, "ImmutableSize height can't be NaN");

        this.width = width;
        this.height = height;
    }

    @Override
    protected Object clone() {
        return new ImmutableSize(width,height);
    }

    @Override
    public String toString() {
        return String.format("ImmutableSize {width=%s, height=%s}", width, height);
    }
}
