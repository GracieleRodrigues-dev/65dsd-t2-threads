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
import com.threads.models.SegmentType;
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

	@Autowired
	private SseEmitterService sseEmitterService;

	public int getMap() {
		return roadMapIndex;
	}

	public void setMap(int map) {
		this.roadMapIndex = roadMapIndex;
	}

	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}

	public void setNumberOfVehicles(int numberOfVehicles) {
		this.numberOfVehicles = numberOfVehicles;
	}

	public int getInsertionTimeInterval() {
		return insertionTimeInterval;
	}

	public void setInsertionTimeInterval(int insertionTimeInterval) {
		this.insertionTimeInterval = insertionTimeInterval;
	}

	public String getExclusionMechanism() {
		return exclusionMechanism;
	}

	public void setExclusionMechanism(String exclusionMechanism) {
		this.exclusionMechanism = exclusionMechanism;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public boolean isInsertionStarted() {
		return isInsertionStarted;
	}

	public void setInsertionStarted(boolean isInsertionStarted) {
		this.isInsertionStarted = isInsertionStarted;
	}

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
		
		System.out.println("StartSimulation");
		
		RoadMap roadMap = roadMapService.getMapById(roadMapIndex);

		List<Position> entryPoints = roadMap.getEntryPoints();
		if (entryPoints.isEmpty()) {
			throw new IllegalStateException("No entry points in the road map");
		}

		MutualExclusionTemplate strategy = "semaphore".equals(exclusionMechanism) ?
				new SemaphoreStrategy(roadMap) : new MonitorStrategy(roadMap);

		for (int i = 0; i < Math.min(numberOfVehicles, entryPoints.size()); i++) {
			Position startPos = entryPoints.get(i % entryPoints.size());
			Vehicle vehicle = new Vehicle(i+1, startPos, 100 + new Random().nextInt(400)); // Velocidades variadas

			VehicleController controller = new VehicleController(
					vehicle, roadMap, strategy, sseEmitterService);
			activeControllers.add(controller);
		}

	}

	public void stopSimulation() {
		this.roadMapIndex = 0;
		this.numberOfVehicles = 0;
		this.insertionTimeInterval = 0;
		this.exclusionMechanism = null;
		this.isStarted = false;

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
		this.isInsertionStarted = false;
		
		System.out.println("StopVehicleInsertion");
	}

}
