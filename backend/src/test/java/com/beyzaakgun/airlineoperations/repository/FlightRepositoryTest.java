package com.beyzaakgun.airlineoperations.repository;

import com.beyzaakgun.airlineoperations.model.Flight;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.dao.DataIntegrityViolationException;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class FlightRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres=new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    FlightRepository repository;

    @Autowired
    private EntityManager entityManager;


    @Test
    void shouldFindSavedFlightByFlightNumber() {
        Flight flight = new Flight(
                "TEST01",
                "Istanbul",
                "Antalya",
                FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"),
                "A12"
        );

        repository.saveAndFlush(flight);
        entityManager.clear();

        Flight foundFlight=repository.findByFlightNumber("TEST01").orElseThrow();

        assertNotNull(foundFlight.getId());
        assertEquals(foundFlight.getFlightNumber(),"TEST01");
        assertEquals(foundFlight.getOrigin(),"Istanbul");
        assertEquals(foundFlight.getDestination(),"Antalya");
        assertEquals(foundFlight.getStatus(),FlightStatus.SCHEDULED);
        assertEquals(foundFlight.getDepartureTime().toInstant(),OffsetDateTime.parse("2026-10-01T10:00:00Z").toInstant());
        assertEquals(foundFlight.getGate(),"A12");

    }

    @Test
    void shouldRejectDuplicateFlightNumber(){
        Flight firstFlight = new Flight(
                "TEST01",
                "Istanbul",
                "Antalya",
                FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"),
                "A12"
        );

        Flight duplicateFlight = new Flight(
                "TEST01",
                "Berlin",
                "Izmir",
                FlightStatus.DELAYED,
                OffsetDateTime.parse("2026-10-02T12:00:00Z"),
                "B04"
        );

        repository.saveAndFlush(firstFlight);

        assertThrows(DataIntegrityViolationException.class,
                ()->repository.saveAndFlush(duplicateFlight));

    }


}
