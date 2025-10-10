package com.lakgamana.repository;

import com.lakgamana.entity.Train;
import com.lakgamana.entity.enums.TrainType;
import com.lakgamana.entity.enums.TrainStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainId(String trainId);

    List<Train> findByStatus(TrainStatus status);

    List<Train> findByType(TrainType type);

    @Query("SELECT t FROM Train t WHERE " +
           "(:fromStation IS NULL OR LOWER(t.fromStation) LIKE LOWER(CONCAT('%', :fromStation, '%'))) AND " +
           "(:toStation IS NULL OR LOWER(t.toStation) LIKE LOWER(CONCAT('%', :toStation, '%'))) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<Train> findTrainsWithFilters(
            @Param("fromStation") String fromStation,
            @Param("toStation") String toStation,
            @Param("type") TrainType type,
            @Param("status") TrainStatus status,
            Pageable pageable
    );

    @Query("SELECT t FROM Train t WHERE " +
           "LOWER(t.fromStation) = LOWER(:fromStation) AND " +
           "LOWER(t.toStation) = LOWER(:toStation) AND " +
           "t.status = 'ACTIVE' AND " +
           "(:departureTime IS NULL OR t.departureTime >= :departureTime)")
    List<Train> findAvailableTrains(
            @Param("fromStation") String fromStation,
            @Param("toStation") String toStation,
            @Param("departureTime") LocalTime departureTime
    );

    @Query("SELECT DISTINCT t.fromStation FROM Train t WHERE t.status = 'ACTIVE' ORDER BY t.fromStation")
    List<String> findAllFromStations();

    @Query("SELECT DISTINCT t.toStation FROM Train t WHERE t.status = 'ACTIVE' ORDER BY t.toStation")
    List<String> findAllToStations();

    @Query("SELECT COUNT(t) FROM Train t WHERE t.status = 'ACTIVE'")
    long countActiveTrains();

    @Query("SELECT t FROM Train t WHERE t.status = 'ACTIVE' ORDER BY t.createdAt DESC")
    List<Train> findRecentActiveTrains(Pageable pageable);
}
