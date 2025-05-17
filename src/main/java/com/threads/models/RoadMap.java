package com.threads.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoadMap {
	private final int rows;
	private final int cols;
	private final SegmentType[][] grid;
	private final List<Position> entryPoints;
	private final List<Position> exitPoints;

	public RoadMap(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.grid = new SegmentType[rows][cols];
		this.entryPoints = new ArrayList<>();
		this.exitPoints = new ArrayList<>();
	}

	public void setSegment(int row, int col, SegmentType type) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new IllegalArgumentException("Invalid position (" + row + ", " + col + ")");
		}
		grid[row][col] = type;
	}

	public SegmentType getSegment(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			return SegmentType.EMPTY;
		}
		return grid[row][col];
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public List<Position> getEntryPoints() {
		return new ArrayList<>(entryPoints);
	}

	public List<Position> getExitPoints() {
		return new ArrayList<>(exitPoints);
	}

	public void addEntryPoint(Position position) {
		entryPoints.add(position);
	}

	public void addExitPoint(Position position) {
		exitPoints.add(position);
	}

	public boolean isValidPosition(Position position) {
		return position.getX() >= 0 && position.getX() < rows && position.getY() >= 0 && position.getY() < cols;
	}

	public boolean isPositionFree(Position position) {
		if (!isValidPosition(position)) {
			return false;
		}
		SegmentType type = getSegment(position.getX(), position.getY());
		return type != null && type != SegmentType.EMPTY;
	}

	public Position getNextVehiclePosition(Position current) {
	    int x = current.getX();
	    int y = current.getY();
	    SegmentType currentSegment = getSegment(x, y);
	    List<Position> candidates = new ArrayList<>();
	    Random random = new Random();

	    switch (currentSegment) {
	        case ROAD_UP:
	        case CROSS_UP:
	            if (isValidPosition(new Position(x - 1, y, SegmentType.EMPTY))) {
	                SegmentType upType = getSegment(x - 1, y);
	                if (upType != null && upType != SegmentType.EMPTY) {
	                    return new Position(x - 1, y, upType);
	                }
	            }
	            break;

	        case ROAD_DOWN:
	        case CROSS_DOWN:
	            if (isValidPosition(new Position(x + 1, y, SegmentType.EMPTY))) {
	                SegmentType downType = getSegment(x + 1, y);
	                if (downType != null && downType != SegmentType.EMPTY) {
	                    return new Position(x + 1, y, downType);
	                }
	            }
	            break;

	        case ROAD_LEFT:
	        case CROSS_LEFT:
	            if (isValidPosition(new Position(x, y - 1, SegmentType.EMPTY))) {
	                SegmentType leftType = getSegment(x, y - 1);
	                if (leftType != null && leftType != SegmentType.EMPTY) {
	                    return new Position(x, y - 1, leftType);
	                }
	            }
	            break;

	        case ROAD_RIGHT:
	        case CROSS_RIGHT:
	            if (isValidPosition(new Position(x, y + 1, SegmentType.EMPTY))) {
	                SegmentType rightType = getSegment(x, y + 1);
	                if (rightType != null && rightType != SegmentType.EMPTY) {
	                    return new Position(x, y + 1, rightType);
	                }
	            }
	            break;

	        case CROSS_UP_RIGHT:
	            if (isValidPosition(new Position(x - 1, y, SegmentType.EMPTY))) {
	                SegmentType up = getSegment(x - 1, y);
	                if (up != null && up != SegmentType.EMPTY) {
	                    candidates.add(new Position(x - 1, y, up));
	                }
	            }
	            if (isValidPosition(new Position(x, y + 1, SegmentType.EMPTY))) {
	                SegmentType right = getSegment(x, y + 1);
	                if (right != null && right != SegmentType.EMPTY) {
	                    candidates.add(new Position(x, y + 1, right));
	                }
	            }
	            break;

	        case CROSS_UP_LEFT:
	            if (isValidPosition(new Position(x - 1, y, SegmentType.EMPTY))) {
	                SegmentType up = getSegment(x - 1, y);
	                if (up != null && up != SegmentType.EMPTY) {
	                    candidates.add(new Position(x - 1, y, up));
	                }
	            }
	            if (isValidPosition(new Position(x, y - 1, SegmentType.EMPTY))) {
	                SegmentType left = getSegment(x, y - 1);
	                if (left != null && left != SegmentType.EMPTY) {
	                    candidates.add(new Position(x, y - 1, left));
	                }
	            }
	            break;

	        case CROSS_RIGHT_DOWN:
	            if (isValidPosition(new Position(x, y + 1, SegmentType.EMPTY))) {
	                SegmentType right = getSegment(x, y + 1);
	                if (right != null && right != SegmentType.EMPTY) {
	                    candidates.add(new Position(x, y + 1, right));
	                }
	            }
	            if (isValidPosition(new Position(x + 1, y, SegmentType.EMPTY))) {
	                SegmentType down = getSegment(x + 1, y);
	                if (down != null && down != SegmentType.EMPTY) {
	                    candidates.add(new Position(x + 1, y, down));
	                }
	            }
	            break;

	        case CROSS_DOWN_LEFT:
	            if (isValidPosition(new Position(x + 1, y, SegmentType.EMPTY))) {
	                SegmentType down = getSegment(x + 1, y);
	                if (down != null && down != SegmentType.EMPTY) {
	                    candidates.add(new Position(x + 1, y, down));
	                }
	            }
	            if (isValidPosition(new Position(x, y - 1, SegmentType.EMPTY))) {
	                SegmentType left = getSegment(x, y - 1);
	                if (left != null && left != SegmentType.EMPTY) {
	                    candidates.add(new Position(x, y - 1, left));
	                }
	            }
	            break;

	        default:
	            return current;
	    }

	    if (!candidates.isEmpty()) {
	        return candidates.get(random.nextInt(candidates.size()));
	    }

	    return current;
	}

}