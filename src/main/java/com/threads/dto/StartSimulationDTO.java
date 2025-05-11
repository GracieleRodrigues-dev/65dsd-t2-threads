package com.threads.dto;

public class StartSimulationDTO {
	private int map;
	private int numberOfVehicles;
	private int insertionTimeInterval;
	private String exclusionMechanism;

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
}
