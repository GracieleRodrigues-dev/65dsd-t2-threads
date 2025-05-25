package com.threads.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.threads.strategy.MonitorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.threads.interfaces.SimulationControllerObserver;
import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.Vehicle;
import com.threads.services.RoadMapService;
import com.threads.services.SseEmitterService;
import com.threads.strategy.MutualExclusionTemplate;
import com.threads.strategy.SemaphoreStrategy;

@Component
public class SimulationController {
	private List<SimulationControllerObserver> observers = new ArrayList<>();

	private int roadMapIndex;
	private int numberOfVehicles;
	private int insertionTimeInterval;
	private String exclusionMechanism;
	private boolean isStarted;
	private boolean isInsertionStarted;
	private final RoadMapService roadMapService = new RoadMapService();
	private List<VehicleController> activeControllers = new ArrayList<>();
	private Thread vehicleControlThread;


	@Autowired
	private SseEmitterService sseEmitterService;

	public void addObserver(SimulationControllerObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(SimulationControllerObserver observer) {
		observers.remove(observer);
	}

	public void notifyObserver() {
		for (SimulationControllerObserver observer : observers) {
			observer.onSimulationUpdate(roadMapIndex, numberOfVehicles, insertionTimeInterval, exclusionMechanism, isStarted,
					isInsertionStarted);
		}
	}

	public void startSimulation(int roadMapIndex, int numberOfVehicles, int insertionTimeInterval, String exclusionMechanism) {
		this.roadMapIndex = roadMapIndex;
		this.numberOfVehicles = numberOfVehicles;
		this.insertionTimeInterval = insertionTimeInterval;
		this.exclusionMechanism = exclusionMechanism;
		this.isStarted = true;
		this.isInsertionStarted = true;

		System.out.println("StartSimulation - Map: " + roadMapIndex +
				", Vehicles: " + numberOfVehicles +
				", Interval: " + insertionTimeInterval +
				", Strategy: " + exclusionMechanism);

		RoadMap roadMap = roadMapService.getMapById(roadMapIndex);
		List<Position> entryPoints = roadMap.getEntryPoints();

		if (entryPoints.isEmpty()) {
			throw new IllegalStateException("No entry points in the road map");
		}

		MutualExclusionTemplate strategy = "semaphore".equals(exclusionMechanism) ?
				new SemaphoreStrategy(roadMap) : new MonitorStrategy(roadMap);

		vehicleControlThread = new Thread(() -> {
			Random random = new Random();
			while (isStarted && !Thread.currentThread().isInterrupted()) {
				if (isInsertionStarted && activeControllers.size() < numberOfVehicles) {
				try {
					synchronized (activeControllers) {
						int beforeRemove = activeControllers.size();
						activeControllers.removeIf(controller -> !controller.isVehicleActive());
						int afterRemove = activeControllers.size();

						if (beforeRemove != afterRemove) {
							System.out.println("Removed " + (beforeRemove - afterRemove) + " inactive vehicles");
						}

						while (activeControllers.size() < numberOfVehicles) {
							Position startPos = entryPoints.get(random.nextInt(entryPoints.size()));
							int speed = 300 + random.nextInt(200);

							Vehicle vehicle = new Vehicle(
									generateNextId(),
									startPos,
									speed);

							VehicleController controller = new VehicleController(
									vehicle, roadMap, strategy, sseEmitterService);

							activeControllers.add(controller);
							System.out.printf("Vehicle %d inserted at %s (Speed: %dms)%n",
									vehicle.getId(), startPos, speed);

							Thread.sleep(50);
						}
					}
					Thread.sleep(insertionTimeInterval);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println("Insertion thread interrupted normally");
					break;
				}
			}else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			System.out.println("Vehicle control thread finished");
		}, "VehicleControllerThread");

		vehicleControlThread.start();
	}

	private int generateNextId() {
		return activeControllers.stream()
				.mapToInt(c -> c.getVehicle().getId())
				.max()
				.orElse(0) + 1;
	}
	public void stopSimulation() {
		this.roadMapIndex = 0;
		this.numberOfVehicles = 0;
		this.insertionTimeInterval = 0;
		this.exclusionMechanism = null;
		this.isStarted = false;
		this.isInsertionStarted = false;

		if (vehicleControlThread != null && vehicleControlThread.isAlive()) {
			vehicleControlThread.interrupt();
			try {
				vehicleControlThread.join(1000);
			} catch (InterruptedException e) {
				System.err.println("Error while waiting for thread to finish: " + e.getMessage());
			}
		}

		activeControllers.forEach(controller -> {
			try {
				controller.interrupt();
			} catch (Exception e) {
				System.err.println("Error stopping vehicle: " + e.getMessage());
			}
		});
		activeControllers.clear();

		System.out.println("StopSimulation");
	}

	public void stopVehicleInsertion() {
		synchronized (activeControllers) {
			this.isInsertionStarted = false;
		}

		if (vehicleControlThread != null && vehicleControlThread.isAlive()) {
			vehicleControlThread.interrupt();
			try {
				vehicleControlThread.join(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Error while waiting for insertion thread to finish: " + e.getMessage());
			}
		}
		System.out.println("Vehicle insertion stopped. Active vehicles: " + activeControllers.size());
		notifyObserver();
	}

}
