package com.threads.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.threads.models.Position;
import com.threads.models.RoadMap;
import com.threads.models.SegmentType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;


@Service
public class RoadMapService {
    private final List<RoadMap> availableMaps = new ArrayList<>();

    public RoadMapService() {
        loadMapsFromResources();
    }

    private void loadMapsFromResources() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:maps/*.txt");

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    RoadMap roadMap = loadMapFromFile(inputStream);
                    if (roadMap != null) {
                        availableMaps.add(roadMap);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar mapas", e);
        }
    }

    public RoadMap loadMapFromFile(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int rows = Integer.parseInt(reader.readLine().trim());
            int cols = Integer.parseInt(reader.readLine().trim());

            RoadMap roadMap = new RoadMap(rows, cols);

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Arquivo de malha incompleto");
                }

                String[] values = line.trim().split("\\s+");
                if (values.length != cols) {
                    throw new IOException("Número de colunas não corresponde ao informado");
                }

                for (int j = 0; j < cols; j++) {
                    int value = Integer.parseInt(values[j]);
                    roadMap.setSegment(i, j, SegmentType.fromValue(value));
                }
            }

            identifyEntryAndExitPoints(roadMap);

            return roadMap;
        }
    }

    private void identifyEntryAndExitPoints(RoadMap roadMap) {
        for (int i = 0; i < roadMap.getRows(); i++) {
            for (int j = 0; j < roadMap.getCols(); j++) {
                SegmentType type = roadMap.getSegment(i, j);

                if (type.isRoad()) {
                    boolean isBorder = i == 0 || i == roadMap.getRows() - 1 ||
                            j == 0 || j == roadMap.getCols() - 1;

                    if (isBorder) {
                        Position pos = new Position(i, j,type);

                        switch (type) {
                            case ROAD_UP:
                                if (i == roadMap.getRows() - 1) roadMap.addEntryPoint(pos);
                                else roadMap.addExitPoint(pos);
                                break;
                            case ROAD_DOWN:
                                if (i == 0) roadMap.addEntryPoint(pos);
                                else roadMap.addExitPoint(pos);
                                break;
                            case ROAD_LEFT:
                                if (j == roadMap.getCols() - 1) roadMap.addEntryPoint(pos);
                                else roadMap.addExitPoint(pos);
                                break;
                            case ROAD_RIGHT:
                                if (j == 0) roadMap.addEntryPoint(pos);
                                else roadMap.addExitPoint(pos);
                                break;
                        }
                    }
                }
            }
        }
    }

    public List<RoadMap> getAvailableMaps() {
        return new ArrayList<>(availableMaps);
    }

    public RoadMap getMapById(int id) {
        if (id >= 0 && id < availableMaps.size()) {
            return availableMaps.get(id);
        }
        return null;
    }
}