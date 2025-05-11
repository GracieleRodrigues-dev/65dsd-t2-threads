package com.threads.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.threads.interfaces.SimulationControllerObserver;

@Component
public class SimulationController {
	private List<SimulationControllerObserver> observers = new ArrayList<>();

	private int map;
	private int numberOfVehicles;
	private int insertionTimeInterval;
	private String exclusionMechanism;
	private boolean isStarted;
	private boolean isInsertionStarted;

	public int getMap() {
		return map;
	}

	public void setMap(int map) {
		this.map = map;
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
			observer.onSimulationUpdate(map, numberOfVehicles, insertionTimeInterval, exclusionMechanism, isStarted,
					isInsertionStarted);
		}
	}

	public void startSimulation(int map, int numberOfVehicles, int insertionTimeInterval, String exclusionMechanism) {
		this.map = map;
		this.numberOfVehicles = numberOfVehicles;
		this.insertionTimeInterval = insertionTimeInterval;
		this.exclusionMechanism = exclusionMechanism;
		this.isStarted = true;
		this.isInsertionStarted = true;
		
		System.out.println("StartSimulation");
	}

	public void stopSimulation() {
		this.map = 0;
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
