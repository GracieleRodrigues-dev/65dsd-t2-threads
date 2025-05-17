package com.threads.controllers;

import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.Vehicle;
import com.threads.services.SseEmitterService;
import com.threads.strategy.MutualExclusionTemplate;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

public class VehicleController extends Thread {
	private final Vehicle vehicle;
	private final RoadMap roadMap;
	private final MutualExclusionTemplate mutualExclusion;
	private final Random random = new Random();
	private List<Position> crossingPath;
	private final SseEmitterService sseEmitterService;

	public VehicleController(Vehicle vehicle, RoadMap roadMap, MutualExclusionTemplate mutualExclusion,
	        SseEmitterService sseEmitterService
	) {
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
				Position nextPosition = roadMap.getNextVehiclePosition(vehicle.getCurrentPosition());

				if (nextPosition.getPositionType().isCross()) {
					// Lógica de cruzamento
				} else {
					mutualExclusion.tryAcquire(nextPosition);

					vehicle.setCurrentPosition(nextPosition);
					System.out.println("Nova posição: " + nextPosition);
					notifySSE(vehicle);

					mutualExclusion.release(nextPosition);
				}

				// PROVISÓRIO (DEVE SER COMPLETAMENTE REVISTO)
				if (roadMap.getExitPoints().contains(nextPosition)) {
					vehicle.setActive(false);
				}

				Thread.sleep(random.nextInt(500));

			} catch (Exception e) {
				vehicle.setActive(false);
				System.err.println("Erro na thread do veículo: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
