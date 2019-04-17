package com.miro.core.data.internal;

import org.apache.commons.lang3.Validate;

public final class ImmutableVertex implements Cloneable{
    private final double x;
    public double getX() {
        return x;
    }

    private final double y;
    public double getY() {
        return y;
    }

    public ImmutableVertex(double x, double y) {
        Validate.notNaN(x, "ImmutableVertex x can't be NaN");
        Validate.notNaN(y, "ImmutableVertex y can't be NaN");

        this.x = x;
        this.y = y;
    }

    @Override
    protected Object clone() {
        return new ImmutableVertex(x,y);
    }

    @Override
    public String toString() {
        return String.format("ImmutableVertex {x=%s, y=%s}", x, y);
    }
}
