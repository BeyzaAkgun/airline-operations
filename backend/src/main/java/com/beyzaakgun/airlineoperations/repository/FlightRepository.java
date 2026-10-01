package com.beyzaakgun.airlineoperations.repository;

import com.beyzaakgun.airlineoperations.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.beyzaakgun.airlineoperations.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight,Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByStatus(FlightStatus status);
}
