package com.beyzaakgun.airlineoperations.dto;

import com.beyzaakgun.airlineoperations.model.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class CreateFlightRequest {
    @NotBlank(message = "Flight number is required")
    @Size(min = 2,max = 10,message = "Flight number must contain 2 to 10 characters.")
    private String flightNumber;
    @NotBlank(message = "Origin is required")
    @Size(max = 100,message = "Origin must not exceed 100 characters.")
    private String origin;
    @NotBlank(message = "Destination is required")
    @Size(max = 100,message = "Destination must not exceed 100 characters.")
    private String destination;
    @NotNull(message = "Flight status is required.")
    private FlightStatus status;
    @NotNull(message = "Departure time is required")
    private OffsetDateTime departureTime;
    @Size(max = 10,message = "Gate must not exceed 10 characters.")
    private String gate;

    public CreateFlightRequest(){

    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(OffsetDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }



}
