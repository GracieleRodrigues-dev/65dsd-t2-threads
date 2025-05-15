package com.threads.dto;

public class StartSimulationDTO {
	private int roadMapIndex;
	private int numberOfVehicles;
	private int insertionTimeInterval;
	private String exclusionMechanism;

	public int getRoadMapIndex() {
		return roadMapIndex;
	}

	public void setRoadMapIndex(int roadMapIndex) {
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
}
