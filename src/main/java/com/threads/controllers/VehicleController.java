package com.threads.controllers;

import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.SegmentType;
import com.threads.models.Vehicle;
import com.threads.services.SseEmitterService;
import com.threads.strategy.MutualExclusionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VehicleController extends Thread {
	private final Vehicle vehicle;
	private final RoadMap roadMap;
	private final MutualExclusionTemplate mutualExclusion;
	private final Random random = new Random();
	private List<Position> crossingPath;
	private final SseEmitterService sseEmitterService;

	public VehicleController(Vehicle vehicle, RoadMap roadMap, MutualExclusionTemplate mutualExclusion,
			SseEmitterService sseEmitterService) {
		this.vehicle = vehicle;
		this.roadMap = roadMap;
		this.mutualExclusion = mutualExclusion;
		this.sseEmitterService = sseEmitterService;
		this.start();
	}

	public void notifySSE(Vehicle vehicle) {
		try {
			sseEmitterService.sendPositionUpdate(vehicle);
		} catch (Exception e) {
			System.err.println("Erro ao notificar SSE: " + e.getMessage());
		}
	}

	@Override
	public void run() {
		while (vehicle.isActive()) {
			try {
				// Calculate the next position based on current road/crossing
				Position nextPosition = calculateNextPosition();

				// Special handling when entering a crossing
				if (isEnteringCrossing(nextPosition)) {
					// Calculate full path through the crossing
					List<Position> path = calculateCrossingPath(nextPosition);

					System.out.println("Crossing path:");
					for (Position pos : path) {
						System.out.println(" - " + pos);
					}

					// Try to reserve all positions in the crossing path
					if (reserveFullPath(path)) {

						for (Position pos : path) {
							System.out.println("X: " + pos.toString());
							moveVehicle(pos);
							sleep(vehicle.getSpeed());
						}

						crossingPath.clear();
					} else {
						System.out.println("Lá");
						// If reservation fails, wait and retry in next iteration
						sleep(vehicle.getSpeed());
						continue;
					}

				} else if (mutualExclusion.tryAcquire(nextPosition)) {
					// If successful, move the vehicle to new position
					moveVehicle(nextPosition);
				}
				// Respect the vehicle's speed by sleeping
				sleep(vehicle.getSpeed());
			} catch (Exception e) {
				vehicle.setActive(false);
				System.err.println("Erro na thread do veículo: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Position calculateNextPosition() {
		if (crossingPath != null && !crossingPath.isEmpty()) {
			return crossingPath.get(0);
		}
		return roadMap.getNextVehiclePosition(vehicle.getCurrentPosition());
	}

	private boolean isEnteringCrossing(Position nextPos) {
		if (nextPos == null || crossingPath != null)
			return false;
		SegmentType nextType = roadMap.getSegment(nextPos.getX(), nextPos.getY());
		return nextType.isCross();
	}

	private List<Position> crossingUpToUp(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position up1 = roadMap.getPositionAtUpFrom(entryPosition);
		path.add(up1);

		Position up2 = roadMap.getPositionAtUpFrom(up1);
		path.add(up2);

		return path;
	}

	private List<Position> crossingUpToRight(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position right = roadMap.getPositionAtRightFrom(entryPosition);
		path.add(right);

		return path;
	}

	private List<Position> crossingUpToLeft(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position up = roadMap.getPositionAtLeftFrom(entryPosition);
		path.add(up);

		Position left1 = roadMap.getPositionAtLeftFrom(up);
		path.add(left1);

		Position left2 = roadMap.getPositionAtLeftFrom(left1);
		path.add(left2);

		return path;
	}

	private List<Position> crossingDownToLeft(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position left = roadMap.getPositionAtLeftFrom(entryPosition);
		path.add(left);

		return path;
	}

	private List<Position> crossingDownToRight(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position down = roadMap.getPositionAtDownFrom(entryPosition);
		path.add(down);

		Position right1 = roadMap.getPositionAtRightFrom(down);
		path.add(right1);

		Position right2 = roadMap.getPositionAtRightFrom(right1);
		path.add(right2);

		return path;
	}

	private List<Position> crossingDownToUp(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position down1 = roadMap.getPositionAtUpFrom(entryPosition);
		path.add(down1);

		Position down2 = roadMap.getPositionAtUpFrom(down1);
		path.add(down2);

		return path;
	}

	private List<Position> crossingRightToDown(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position down = roadMap.getPositionAtDownFrom(entryPosition);
		path.add(down);

		return path;
	}

	private List<Position> crossingRightToLeft(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position right1 = roadMap.getPositionAtLeftFrom(entryPosition);
		path.add(right1);

		Position right2 = roadMap.getPositionAtLeftFrom(right1);
		path.add(right2);

		return path;
	}

	private List<Position> crossingRightToUp(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position right1 = roadMap.getPositionAtRightFrom(entryPosition);
		path.add(right1);

		Position up1 = roadMap.getPositionAtUpFrom(right1);
		path.add(up1);

		Position up2 = roadMap.getPositionAtUpFrom(up1);
		path.add(up2);

		return path;
	}

	private List<Position> crossingLeftToUp(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position up = roadMap.getPositionAtUpFrom(entryPosition);
		path.add(up);

		return path;
	}

	private List<Position> crossingLeftToRight(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position left1 = roadMap.getPositionAtLeftFrom(entryPosition);
		path.add(left1);

		Position left2 = roadMap.getPositionAtLeftFrom(left1);
		path.add(left2);

		return path;
	}

	private List<Position> crossingLeftToDown(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position left = roadMap.getPositionAtLeftFrom(entryPosition);
		path.add(left);

		Position down1 = roadMap.getPositionAtDownFrom(left);
		path.add(down1);

		Position down2 = roadMap.getPositionAtDownFrom(down1);
		path.add(down2);

		return path;
	}

	private List<Position> calculateCrossingPath(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		int option = 0;

		switch (entryPosition.getPositionType()) {
		case ROAD_UP:
			switch (option) {
			case 0:
				// UpToRight
				System.out.println("FromUpToRight");
				path = crossingUpToRight(entryPosition);
				break;
			case 1:
				// UpToUp
				System.out.println("FromUpToUp");
				path = crossingUpToUp(entryPosition);
				break;
			case 2:
				// UpToLeft
				System.out.println("FromUpToLeft");
				path = crossingUpToLeft(entryPosition);
				break;
			}
			break;

		case ROAD_DOWN:
			switch (option) {
			case 0:
				// DownToLeft
				System.out.println("FromDownToLeft");
				path = crossingDownToLeft(entryPosition);
				break;
			case 1:
				// DownToUp
				System.out.println("FromDownToUp");
				path = crossingDownToUp(entryPosition);
				break;
			case 2:
				// DownToRight
				System.out.println("FromDownToRight");
				path = crossingDownToRight(entryPosition);
				break;
			}
			break;

		case ROAD_RIGHT:
			switch (option) {
			case 0:
				// RightToDown
				System.out.println("FromRightToDown");
				path = crossingRightToDown(entryPosition);
				break;
			case 1:
				// RightToLeft
				System.out.println("FromRightToLeft");
				path = crossingRightToLeft(entryPosition);
				break;
			case 2:
				// RightToUp
				System.out.println("FromRightToUp");
				path = crossingRightToUp(entryPosition);
				break;
			}
			break;

		case ROAD_LEFT:
			switch (option) {
			case 0:
				// LeftToUp
				System.out.println("FromLeftToUp");
				path = crossingLeftToUp(entryPosition);
				break;
			case 1:
				// LeftToRight
				System.out.println("FromLeftToRight");
				path = crossingLeftToRight(entryPosition);
				break;
			case 2:
				// LeftToDown
				System.out.println("FromLeftToDown");
				path = crossingLeftToDown(entryPosition);
				break;
			}
			break;

		default:
			// Log ou tratamento para tipos inesperados
			System.out.println("Unknown Segment Type: " + entryPosition.getPositionType());
			break;
		}

		return path;
	}

	private boolean reserveFullPath(List<Position> path) {
		List<Position> reservedPositions = new ArrayList<>();
		for (Position pos : path) {
			if (mutualExclusion.tryAcquire(pos)) {
				reservedPositions.add(pos);
			} else {
				reservedPositions.forEach(mutualExclusion::release);
				return false;
			}
		}
		this.crossingPath = path;
		return true;
	}

	private void moveVehicle(Position newPos) {
		Position current = vehicle.getCurrentPosition();

		System.out.println("Vehicle " + vehicle.getId() + " moving from " + current + " to " + newPos);

		mutualExclusion.release(current);
		vehicle.setCurrentPosition(newPos);
		notifySSE(vehicle);
	}

}
