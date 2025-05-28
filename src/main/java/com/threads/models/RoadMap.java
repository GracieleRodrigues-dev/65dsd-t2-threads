package com.threads.models;

import java.util.ArrayList;
import java.util.List;

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

	public Position getPositionAtUpFrom(Position position) {
		int x = position.getX() - 1;
		int y = position.getY();
		if (!isValidCoordinate(x, y)) return null;

		return new Position(x, y, grid[x][y]);
	}

	public Position getPositionAtDownFrom(Position position) {
		int x = position.getX() + 1;
		int y = position.getY();
		if (!isValidCoordinate(x, y)) return null;

		return new Position(x, y, grid[x][y]);
	}

	public Position getPositionAtRightFrom(Position position) {
		int x = position.getX();
		int y = position.getY() + 1;
		if (!isValidCoordinate(x, y)) return null;

		return new Position(x, y, grid[x][y]);
	}

	public Position getPositionAtLeftFrom(Position position) {
		int x = position.getX();
		int y = position.getY() - 1;
		if (!isValidCoordinate(x, y)) return null;

		return new Position(x, y, grid[x][y]);
	}

	private boolean isValidCoordinate(int x, int y) {
		return x >= 0 && x < rows && y >= 0 && y < cols;
	}

	public Position getNextVehiclePosition(Position currentPosition) {
		SegmentType currentType = getSegment(currentPosition.getX(), currentPosition.getY());

		//System.out.println("Type: " + currentPosition.getPositionType());

		switch (currentType) {
			case ROAD_UP:
				return getPositionAtUpFrom(currentPosition);
			case ROAD_DOWN:
				return getPositionAtDownFrom(currentPosition);
			case ROAD_LEFT:
				return getPositionAtLeftFrom(currentPosition);
			case ROAD_RIGHT:
				return getPositionAtRightFrom(currentPosition);

			default:
				return currentPosition;
		}
	}

	public boolean isExitPoint(Position position) {
		if (position == null) {
			return false;
		}
		return exitPoints.stream()
				.anyMatch(exit -> exit.getX() == position.getX() && exit.getY() == position.getY());
	}

}