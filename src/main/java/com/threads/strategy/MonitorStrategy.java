package com.threads.strategy;

import com.threads.models.Position;
import com.threads.models.RoadMap;

public class MonitorStrategy extends MutualExclusionTemplate {
    public MonitorStrategy(RoadMap roadMap) {
        super(roadMap);
    }

    @Override
    protected boolean tryAcquirePosition(Position position) {
        return false;
    }

    @Override
    protected void releasePosition(Position position) {

    }
}