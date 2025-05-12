package com.threads.routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.threads.models.RoadMap;
import com.threads.services.RoadMapService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/map")
public class MapRoutes {
    private final RoadMapService roadMapService;

    public MapRoutes(RoadMapService roadMapService) {
        this.roadMapService = roadMapService;
    }

    @GetMapping("/maps")
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