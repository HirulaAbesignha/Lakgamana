package com.lakgamana.controller;

import com.lakgamana.dto.response.ApiResponse;
import com.lakgamana.entity.Route;
import com.lakgamana.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
@Tag(name = "Routes", description = "Route management APIs")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    @Operation(summary = "Get all routes")
    public ResponseEntity<ApiResponse<List<Route>>> getAllRoutes() {
        List<Route> routes = routeService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Routes retrieved", routes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<ApiResponse<Route>> getRouteById(@PathVariable Long id) {
        Route route = routeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success("Route retrieved", route));
    }

    @PostMapping
    @Operation(summary = "Create route (Admin)")
    public ResponseEntity<ApiResponse<Route>> createRoute(@Valid @RequestBody Route route) {
        Route created = routeService.create(route);
        return ResponseEntity.ok(ApiResponse.success("Route created", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route (Admin)")
    public ResponseEntity<ApiResponse<Route>> updateRoute(@PathVariable Long id, @Valid @RequestBody Route route) {
        Route updated = routeService.update(id, route);
        return ResponseEntity.ok(ApiResponse.success("Route updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Route deleted", null));
    }
}


