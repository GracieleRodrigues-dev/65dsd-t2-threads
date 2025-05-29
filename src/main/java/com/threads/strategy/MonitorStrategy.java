package com.threads.strategy;

import com.threads.models.Position;
import com.threads.models.RoadMap;

public class MonitorStrategy extends MutualExclusionTemplate {
    private final Object[][] locks;
    private final boolean[][] occupied;

    public MonitorStrategy(RoadMap roadMap) {
        super(roadMap);
        int rows = roadMap.getRows();
        int cols = roadMap.getCols();
        locks = new Object[rows][cols];
        occupied = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                locks[i][j] = new Object();
                occupied[i][j] = false;
            }
        }
    }

    @Override
    protected boolean tryAcquirePosition(Position position) {
        int x = position.getX();
        int y = position.getY();

        synchronized (locks[x][y]) {
            if (!occupied[x][y] && roadMap.isPositionFree(position)) {
                occupied[x][y] = true;
                return true;
            }
            return false;
        }
    }

    @Override
    protected void releasePosition(Position position) {
        int x = position.getX();
        int y = position.getY();

        synchronized (locks[x][y]) {
            occupied[x][y] = false;
        }
    }
}
