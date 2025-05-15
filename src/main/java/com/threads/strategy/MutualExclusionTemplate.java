package com.threads.strategy;

import com.threads.models.Position;
import com.threads.models.RoadMap;

public abstract class MutualExclusionTemplate {
    protected final RoadMap roadMap;

    public MutualExclusionTemplate(RoadMap roadMap) {
        this.roadMap = roadMap;
    }

    // Template method
    public final boolean tryAcquire(Position position) {
        if (!roadMap.isValidPosition(position)) {
            return false;
        }

        return tryAcquirePosition(position);
    }

    public final void release(Position position) {
        if (roadMap.isValidPosition(position)) {
            releasePosition(position);
        }
    }

    // Primitive operations to be implemented by subclasses
    protected abstract boolean tryAcquirePosition(Position position);
    protected abstract void releasePosition(Position position);
}