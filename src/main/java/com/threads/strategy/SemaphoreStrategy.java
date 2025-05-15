package com.threads.strategy;

import com.threads.models.Position;
import com.threads.models.RoadMap;

import java.util.concurrent.Semaphore;

public class SemaphoreStrategy extends MutualExclusionTemplate {
    private final Semaphore[][] semaphores;

    public SemaphoreStrategy(RoadMap roadMap) {
        super(roadMap);
        this.semaphores = new Semaphore[roadMap.getRows()][roadMap.getCols()];

        for (int i = 0; i < roadMap.getRows(); i++) {
            for (int j = 0; j < roadMap.getCols(); j++) {
                semaphores[i][j] = new Semaphore(1, true);
            }
        }
    }

    @Override
    protected boolean tryAcquirePosition(Position position) {
        try {
            if (semaphores[position.getX()][position.getY()].tryAcquire()) {
                return roadMap.isPositionFree(position);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void releasePosition(Position position) {
        semaphores[position.getX()][position.getY()].release();
    }
}