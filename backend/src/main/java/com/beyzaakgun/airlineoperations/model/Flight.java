package com.beyzaakgun.airlineoperations.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true,updatable = false)
    private String flightNumber;
    @Column(nullable = false)
    private String origin;
    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status;
    @Column(nullable = false)
    private OffsetDateTime departureTime;
    private String gate;

    public Flight(){

    }
    public Flight(String flightNumber, String origin, String destination, FlightStatus
            status, OffsetDateTime departureTime, String gate){
        this.flightNumber=flightNumber;
        this.origin=origin;
        this.destination=destination;
        this.status=status;
        this.departureTime=departureTime;
        this.gate=gate;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepartureTime(OffsetDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }


    public String getGate() {
        return gate;
    }

    public Long getId() {
        return id;
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

    public FlightStatus getStatus() {
        return status;
    }

    public OffsetDateTime getDepartureTime() {
        return departureTime;
    }


}
