package org.example.controller;

import org.example.model.RoadMap;
import org.example.service.RoadMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/map")
public class MapController {
    private final RoadMapService roadMapService;

    public MapController(RoadMapService roadMapService) {
        this.roadMapService = roadMapService;
    }

    @GetMapping("/models")
    public ResponseEntity<List<int[][]>> getAvailableMaps() {
        List<RoadMap> maps = roadMapService.getAvailableMaps();
        List<int[][]> response = maps.stream()
                .map(this::convertRoadMapToArray)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private int[][] convertRoadMapToArray(RoadMap roadMap) {
        int[][] grid = new int[roadMap.getRows()][roadMap.getCols()];
        for (int i = 0; i < roadMap.getRows(); i++) {
            for (int j = 0; j < roadMap.getCols(); j++) {
                grid[i][j] = roadMap.getSegment(i, j).getValue();
            }
        }
        return grid;
    }
}