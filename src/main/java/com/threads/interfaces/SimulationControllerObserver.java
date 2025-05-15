package com.threads.interfaces;

public interface SimulationControllerObserver {
	void onSimulationUpdate(int roadMapIndex, int numberOfVehicles, int insertionTimeInterval, String exclusionMechanism,
			boolean isStarted, boolean isInsertionStarted);

}
