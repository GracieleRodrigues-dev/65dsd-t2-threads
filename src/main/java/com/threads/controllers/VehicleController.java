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
							moveVehicle(pos);
							sleep(vehicle.getSpeed());
						}

						crossingPath = null;
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
			}/* finally {
				mutualExclusion.release(vehicle.getCurrentPosition());
				System.out.println("Vehicle " + vehicle.getId() + " finalized");
			}*/
		}
	}

	private Position calculateNextPosition() {
		return roadMap.getNextVehiclePosition(vehicle.getCurrentPosition());
	}

	private boolean isEnteringCrossing(Position nextPos) {
		if (nextPos == null || crossingPath != null) {
			return false;
		}
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


	private List<Position> crossingRightToDown(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position down = roadMap.getPositionAtDownFrom(entryPosition);
		path.add(down);

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
		SegmentType crossingType = roadMap.getSegment(entryPosition.getX(), entryPosition.getY());

		// Determines the entry direction based on the previous segment type
		SegmentType entryDirection = vehicle.getCurrentPosition().getPositionType();

		// Randomly chooses one of the possible directions from the intersection
		List<SegmentType> possibleDirections = getPossibleDirections(crossingType);

		if (possibleDirections.isEmpty()) {
			System.out.println("No valid directions for crossing type: " + crossingType);
			return path;
		}

		SegmentType chosenDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
		System.out.println("Entry: " + entryPosition + entryDirection + ", Crossing: " + crossingType + ",Possible directions:"+ possibleDirections +", Chosen: " + chosenDirection);

		// Calculates the path based on the entry and exit directions
		System.out.println("DEBUG - EntryDir: " + entryDirection + " (" + entryDirection.getValue() + "), " +
				"Crossing: " + crossingType + " (" + crossingType.getValue() + ")");
		switch (entryDirection) {
			case ROAD_UP:
				if (chosenDirection == SegmentType.ROAD_UP) {
					path = crossingUpToUp(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_RIGHT) {
					path = crossingUpToRight(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_LEFT) {
					path = crossingUpToLeft(entryPosition);
				}
				break;

			case ROAD_DOWN:
				if (chosenDirection == SegmentType.ROAD_DOWN) {
					path = crossingDownToDown(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_LEFT) {
					path = crossingDownToLeft(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_RIGHT) {
					path = crossingDownToRight(entryPosition);
				}
				break;

			case ROAD_RIGHT:
				if (chosenDirection == SegmentType.ROAD_RIGHT) {
					path = crossingRightToRight(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_DOWN) {
					path = crossingRightToDown(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_UP) {
					path = crossingRightToUp(entryPosition);
				}
				break;

			case ROAD_LEFT:
				if (chosenDirection == SegmentType.ROAD_LEFT) {
					path = crossingLeftToLeft(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_UP) {
					path = crossingLeftToUp(entryPosition);
				} else if (chosenDirection == SegmentType.ROAD_DOWN) {
					path = crossingLeftToDown(entryPosition);
				}
				break;

			default:
				System.out.println("Unknown entry direction: " + entryDirection);
		}

		return path;
	}
	private List<SegmentType> getPossibleDirections(SegmentType crossingType) {
		List<SegmentType> directions = new ArrayList<>();

		switch (crossingType) {
			case CROSS_UP :
				directions.add(SegmentType.ROAD_UP);
				directions.add(SegmentType.ROAD_RIGHT);
				directions.add(SegmentType.ROAD_LEFT);
				break;

			case CROSS_RIGHT:
				directions.add(SegmentType.ROAD_RIGHT);
				directions.add(SegmentType.ROAD_UP);
				directions.add(SegmentType.ROAD_DOWN);
				break;

			case CROSS_DOWN:
				directions.add(SegmentType.ROAD_DOWN);
				directions.add(SegmentType.ROAD_RIGHT);
				directions.add(SegmentType.ROAD_LEFT);
				break;

			case CROSS_LEFT:
				directions.add(SegmentType.ROAD_LEFT);
				directions.add(SegmentType.ROAD_UP);
				directions.add(SegmentType.ROAD_DOWN);
				break;

			case CROSS_UP_RIGHT:
				directions.add(SegmentType.ROAD_UP);
				directions.add(SegmentType.ROAD_RIGHT);
				break;

			case CROSS_UP_LEFT:
				directions.add(SegmentType.ROAD_UP);
				directions.add(SegmentType.ROAD_LEFT);
				break;

			case CROSS_RIGHT_DOWN:
				directions.add(SegmentType.ROAD_RIGHT);
				directions.add(SegmentType.ROAD_DOWN);
				break;

			case CROSS_DOWN_LEFT:
				directions.add(SegmentType.ROAD_DOWN);
				directions.add(SegmentType.ROAD_LEFT);
				break;

			default:
				// Não é um cruzamento válido
				break;
		}
		return directions;
	}

	private List<Position> crossingDownToDown(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position down1 = roadMap.getPositionAtDownFrom(entryPosition);
		if (down1 != null) path.add(down1);

		Position down2 = roadMap.getPositionAtDownFrom(down1);
		if (down2 != null) path.add(down2);

		return path;
	}

	private List<Position> crossingRightToRight(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position right1 = roadMap.getPositionAtRightFrom(entryPosition);
		if (right1 != null) path.add(right1);

		Position right2 = roadMap.getPositionAtRightFrom(right1);
		if (right2 != null) path.add(right2);

		return path;
	}

	private List<Position> crossingLeftToLeft(Position entryPosition) {
		List<Position> path = new ArrayList<>();
		path.add(entryPosition);

		Position left1 = roadMap.getPositionAtLeftFrom(entryPosition);
		if (left1 != null) path.add(left1);

		Position left2 = roadMap.getPositionAtLeftFrom(left1);
		if (left2 != null) path.add(left2);

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

		if (roadMap.isExitPoint(newPos)) {
			vehicle.setActive(false);
			vehicle.setCurrentPosition(newPos);
			System.out.println("Vehicle " + vehicle.getId() + " exited at " + newPos);
			notifySSE(vehicle);
			mutualExclusion.release(current);
			return;
		}

		System.out.println("Vehicle " + vehicle.getId() + " moving from " + current.getPositionType()+ current + " to " + newPos.getPositionType() + newPos);

		vehicle.setCurrentPosition(newPos);
		notifySSE(vehicle);
		mutualExclusion.release(current);
	}

	public boolean isVehicleActive(){
		return vehicle.isActive();
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
}
