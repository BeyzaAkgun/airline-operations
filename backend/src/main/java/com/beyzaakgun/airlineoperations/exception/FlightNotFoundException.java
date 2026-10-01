package com.beyzaakgun.airlineoperations.exception;

public class FlightNotFoundException extends RuntimeException{

    public FlightNotFoundException(String flightNumber){
        super("Flight "+ flightNumber + " was not found.");

    }
}
