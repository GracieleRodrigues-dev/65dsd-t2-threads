package com.threads.routes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.threads.controllers.SimulationController;
import com.threads.dto.StartSimulationDTO;

@RestController
@RequestMapping("/api/simulation")
public class SimulationRoutes {

	private final SimulationController simulationController;

	@Autowired
	public SimulationRoutes(SimulationController simulationController) {
		this.simulationController = simulationController;
	}

	@PostMapping("/start")
	public ResponseEntity<String> startSimulation(@RequestBody StartSimulationDTO body) {
		simulationController.startSimulation(body.getMap(), body.getNumberOfVehicles(), body.getInsertionTimeInterval(),
				body.getExclusionMechanism());

		return ResponseEntity.ok("Simulação iniciada com sucesso.");
	}

	@PostMapping("/stop")
	public ResponseEntity<String> stopSimulation() {
		simulationController.stopSimulation();

		return ResponseEntity.ok("Simulação parada com sucesso.");
	}

	@PostMapping("/insertion/stop")
	public ResponseEntity<String> stopInsertion() {
		simulationController.stopVehicleInsertion();

		return ResponseEntity.ok("Inserção de veículos parada com sucesso.");
	}

}
