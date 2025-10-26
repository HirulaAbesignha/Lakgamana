package com.lakgamana.repository;

import com.lakgamana.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteId(String routeId);
    boolean existsByRouteId(String routeId);
}


