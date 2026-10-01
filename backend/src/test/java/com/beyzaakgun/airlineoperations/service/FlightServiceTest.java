package com.beyzaakgun.airlineoperations.service;


import com.beyzaakgun.airlineoperations.dto.CreateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.FlightResponse;
import com.beyzaakgun.airlineoperations.dto.UpdateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.UpdateFlightStatusRequest;
import com.beyzaakgun.airlineoperations.exception.DuplicateFlightException;
import com.beyzaakgun.airlineoperations.exception.FlightNotFoundException;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import com.beyzaakgun.airlineoperations.repository.FlightRepository;
import com.beyzaakgun.airlineoperations.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.mock;
import static  org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;



public class FlightServiceTest {
    private FlightRepository repository;
    private FlightService service;

    @BeforeEach
    void setUp(){
        repository=mock(FlightRepository.class);
        service=new FlightService(repository);
    }

    @Test
    void shouldThrownWhenFlightIsNotFound(){
        when(repository.findByFlightNumber("UNKNOWN")).thenReturn(Optional.empty());

        FlightNotFoundException exception=assertThrows(FlightNotFoundException.class,
                ()->service.getFlightByNumber("UNKNOWN"));

        assertEquals(
                "Flight UNKNOWN was not found.",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrownWhenFlightAlreadyExists(){
        CreateFlightRequest req=new CreateFlightRequest();
        req.setFlightNumber(" tx23 ");
        when(repository.existsByFlightNumber("TX23")).thenReturn(true);

        DuplicateFlightException ex=assertThrows((DuplicateFlightException.class),
                ()->service.createFlight(req));

        assertEquals("Flight TX23 already exists.",
                ex.getMessage());

        verify(repository, never()).save(any(Flight.class));
    }

    @Test
    void shouldCreateFlightSuccsessfully(){
        CreateFlightRequest req=new CreateFlightRequest();
        req.setFlightNumber(" test01");
        req.setOrigin(" Istanbul ");
        req.setDestination("Malatya");
        req.setStatus(FlightStatus.SCHEDULED);
        req.setDepartureTime(OffsetDateTime.now());
        req.setGate("B1");

        when(repository.existsByFlightNumber("TEST01")).thenReturn(false);

        when(repository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse res=service.createFlight(req);

        assertEquals("TEST01",res.getFlightNumber());
        assertEquals("Istanbul",res.getOrigin());
        assertEquals(req.getDestination(),res.getDestination());
        assertEquals(req.getStatus(),res.getStatus());
        assertEquals(req.getDepartureTime(),res.getDepartureTime());
        assertEquals(req.getGate(),res.getGate());


        verify(repository).save(any(Flight.class));


    }

    @Test
    void shouldUpdateFlightSuccsessfully(){
        Flight flight=new Flight("TX23","Istanbul","Antalya",FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-05-01T10:00:00Z"),"A12");

        when(repository.findByFlightNumber("TX23"))
                .thenReturn(Optional.of(flight));

        UpdateFlightRequest req=new UpdateFlightRequest();
        req.setOrigin("Malatya");
        req.setDestination("Izmır");
        req.setDepartureTime(OffsetDateTime.parse("2026-06-01T10:00:00Z"));
        req.setStatus(FlightStatus.DELAYED);
        req.setGate("B10");

        when(repository.save(any(Flight.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse response=service.updateFlight(" tx23",req);

        assertEquals("TX23",response.getFlightNumber());
        assertEquals("Malatya",response.getOrigin());
        assertEquals("Izmır",response.getDestination());
        assertEquals(OffsetDateTime.parse("2026-06-01T10:00:00Z"),response.getDepartureTime());
        assertEquals(FlightStatus.DELAYED,response.getStatus());
        assertEquals("B10",response.getGate());

        verify(repository).save(any(Flight.class));



    }

    @Test
    void shouldDeleteFlightSuccsessfully() {
        Flight flight = new Flight("TX23", "Istanbul", "Antalya", FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-05-01T10:00:00Z"), "A12");

        when(repository.findByFlightNumber("TX23"))
                .thenReturn(Optional.of(flight));

        service.deleteFlight(" tx23");

        verify(repository).delete(flight);
    }

    @Test
    void shouldThrowWhenDeletingUnknownFlight(){
        when(repository.findByFlightNumber("UNKNOWN")).thenReturn(Optional.empty());

        FlightNotFoundException ex=assertThrows(FlightNotFoundException.class,
                ()->service.deleteFlight("UNKNOWN"));

        assertEquals("Flight UNKNOWN was not found.",ex.getMessage());

        verify(repository,never()).delete(any(Flight.class));

        }


    @Test
    void shouldUpdateFlightStatusSuccessfully(){
       Flight flight=new Flight("TX23", "Istanbul", "Antalya", FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-05-01T10:00:00Z"), "A12");

        when(repository.findByFlightNumber("TX23")).thenReturn(Optional.of(flight));

        when(repository.save(any(Flight.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FlightResponse response=service.updateFlightStatus("TX23",FlightStatus.DELAYED);

        assertEquals("TX23",response.getFlightNumber());
        assertEquals("Istanbul",response.getOrigin());
        assertEquals("Antalya",response.getDestination());
        assertEquals(OffsetDateTime.parse("2026-05-01T10:00:00Z"),response.getDepartureTime());
        assertEquals(FlightStatus.DELAYED,response.getStatus());
        assertEquals("A12",response.getGate());

        verify(repository).save(flight);







    }






    }



