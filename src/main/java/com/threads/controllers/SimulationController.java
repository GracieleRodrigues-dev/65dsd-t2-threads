package com.threads.controllers;

import java.util.ArrayList;
import java.util.List;

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
		
		RoadMap roadMap = roadMapService.getMapById(1);

		
		Vehicle vehicle1 = new Vehicle(1, new Position(0,0, SegmentType.ROAD_UP), 10);
		
		MutualExclusionTemplate semaphoreStrategy = new SemaphoreStrategy(roadMap);
		
		VehicleController vehicleController1 = new VehicleController(vehicle1, roadMap,semaphoreStrategy, sseEmitterService);

	}

	public void stopSimulation() {
		this.roadMapIndex = 0;
		this.numberOfVehicles = 0;
		this.insertionTimeInterval = 0;
		this.exclusionMechanism = null;
		this.isStarted = false;
		
		System.out.println("StopSimulation");
	}

	public void stopVehicleInsertion() {
		this.isInsertionStarted = false;
		
		System.out.println("StopVehicleInsertion");
	}

}
