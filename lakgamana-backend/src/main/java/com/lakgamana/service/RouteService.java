package com.lakgamana.service;

import com.lakgamana.entity.Route;
import com.lakgamana.entity.enums.TrainStatus;
import com.lakgamana.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional(readOnly = true)
    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + id));
    }

    @Transactional
    public Route create(Route route) {
        if (route.getRouteId() == null || route.getRouteId().isEmpty()) {
            route.setRouteId(generateRouteId());
        }
        if (route.getStatus() == null) {
            route.setStatus(TrainStatus.ACTIVE);
        }
        if (route.getSchedule() == null) {
            route.setSchedule(new Route.Schedule());
        }
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        return routeRepository.save(route);
    }

    @Transactional
    public Route update(Long id, Route routeDetails) {
        Route route = findById(id);
        route.setName(routeDetails.getName());
        route.setFromStation(routeDetails.getFromStation());
        route.setToStation(routeDetails.getToStation());
        route.setDistance(routeDetails.getDistance());
        route.setDuration(routeDetails.getDuration());
        route.setStatus(routeDetails.getStatus());
        if (routeDetails.getSchedule() != null) {
            route.setSchedule(routeDetails.getSchedule());
        }
        route.setUpdatedAt(LocalDateTime.now());
        return routeRepository.save(route);
    }

    @Transactional
    public void delete(Long id) {
        Route route = findById(id);
        routeRepository.delete(route);
    }

    private String generateRouteId() {
        long counter = routeRepository.count() + 1;
        return String.format("R%03d", counter);
    }
}


