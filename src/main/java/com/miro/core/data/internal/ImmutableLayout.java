package com.miro.core.data.internal;

import org.apache.commons.lang3.Validate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class ImmutableLayout{

    private final ImmutableSize size;
    public ImmutableSize getSize() {
        return size;
    }

    private final ImmutableVertex vertex;
    public ImmutableVertex getVertex() {
        return vertex;
    }

    private final int zIndex;
    public int getzIndex() {
        return zIndex;
    }

    private final OffsetDateTime updatedAtUtc;
    public OffsetDateTime getUpdatedAtUtc() {
        return updatedAtUtc;
    }

    ImmutableLayout(ImmutableSize size, ImmutableVertex vertex, int zIndex) {
        Validate.notNull(size, "ImmutableLayout size can't be null");
        Validate.notNull(vertex, "ImmutableLayout vertex can't be null");
        if (zIndex < 0)
            throw new IllegalArgumentException("Invalid value for zIndex");

        this.size = size;
        this.vertex = vertex;
        this.zIndex = zIndex;
        updatedAtUtc = OffsetDateTime.now( ZoneOffset.UTC );
    }

    @Override
    public String toString() {
        return String.format("WidgetInternal layout {size=%s, vertex=%s, zIndex=%s}", size, vertex, zIndex);
    }
}
