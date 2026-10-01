package com.beyzaakgun.airlineoperations.dto;

import com.beyzaakgun.airlineoperations.model.FlightStatus;

import java.time.OffsetDateTime;

public class FlightResponse {
    private String flightNumber;
    private String origin;
    private String destination;
    private FlightStatus status;
    private OffsetDateTime departureTime;
    private String gate;

    public FlightResponse(String flightNumber,String origin,String destination,FlightStatus status,
                          OffsetDateTime departureTime, String gate){
        this.flightNumber=flightNumber;
        this.origin=origin;
        this.destination=destination;
        this.status=status;
        this.departureTime=departureTime;
        this.gate=gate;

    }



    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }


    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }

    public String getGate() {
        return gate;
    }


    public FlightStatus getStatus() {
        return status;
    }

}
