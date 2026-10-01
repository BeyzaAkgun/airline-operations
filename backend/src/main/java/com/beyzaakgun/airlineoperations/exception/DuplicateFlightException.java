package com.beyzaakgun.airlineoperations.exception;

public class DuplicateFlightException extends RuntimeException{

    public DuplicateFlightException(String flightNumber){

        super("Flight " + flightNumber + " already exists.");

    }
}
