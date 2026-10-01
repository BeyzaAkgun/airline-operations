package com.beyzaakgun.airlineoperations.service;

import com.beyzaakgun.airlineoperations.dto.CreateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.FlightResponse;
import com.beyzaakgun.airlineoperations.dto.UpdateFlightRequest;
import com.beyzaakgun.airlineoperations.exception.DuplicateFlightException;
import com.beyzaakgun.airlineoperations.exception.FlightNotFoundException;
import com.beyzaakgun.airlineoperations.model.Flight;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import com.beyzaakgun.airlineoperations.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository repo;

    public FlightService(FlightRepository repo){
        this.repo=repo;
    }

    private FlightResponse toResponse(Flight flight){
        FlightResponse flightResponse=new FlightResponse(flight.getFlightNumber(),
                flight.getOrigin(),flight.getDestination(),flight.getStatus(),
                flight.getDepartureTime(), flight.getGate());

        return flightResponse;


    }

    public List<FlightResponse> getAllFlights(){
        List<Flight> flights=repo.findAll();
        List<FlightResponse> flightResponses= flights.stream()
               .map(flight->toResponse(flight))
               .toList();

        return flightResponses;

    }

    public FlightResponse getFlightByNumber(String flightNumber){
        String flightNum=flightNumber.trim().toUpperCase(Locale.ROOT);
        Flight flight=repo.findByFlightNumber(flightNum).orElseThrow(()->new FlightNotFoundException(flightNum));
        FlightResponse flightResponse=toResponse(flight);
        return flightResponse;

    }

    public List<FlightResponse> getFlightsByStatus(FlightStatus status){
        List<Flight> flights=repo.findByStatus(status);
        List<FlightResponse> flightResponses=flights.stream()
                .map(flight->toResponse(flight))
                .toList();

        return  flightResponses;
    }

    @Transactional
    public FlightResponse createFlight(CreateFlightRequest request){
        String flightNum=request.getFlightNumber().trim().toUpperCase(Locale.ROOT);
        if(repo.existsByFlightNumber(flightNum)){
            throw new DuplicateFlightException(flightNum);
        }
        String origin=request.getOrigin().trim();
        String destination= request.getDestination().trim();
        FlightStatus status=request.getStatus();
        OffsetDateTime departureTime=request.getDepartureTime();
        String gate=request.getGate()==null ? "" : request.getGate().trim();
        Flight flight=new Flight(flightNum,origin,destination,status,departureTime,gate);
        Flight savedFlight=repo.save(flight);
        return toResponse(savedFlight);


    }

    @Transactional
    public FlightResponse updateFlight(String flightNumber,UpdateFlightRequest request){
        String flightNum=flightNumber.trim().toUpperCase(Locale.ROOT);
        Flight flight=repo.findByFlightNumber(flightNum).orElseThrow(()->new FlightNotFoundException(flightNum));
        flight.setOrigin(request.getOrigin().trim());
        flight.setDestination(request.getDestination().trim());
        flight.setStatus(request.getStatus());
        flight.setDepartureTime(request.getDepartureTime());
        flight.setGate(request.getGate()==null ? "" : request.getGate().trim());

        Flight savedFlight=repo.save(flight);
        return toResponse(savedFlight);

    }

    @Transactional
    public FlightResponse updateFlightStatus(String flightNumber,FlightStatus status){
        String flightNum=flightNumber.trim().toUpperCase(Locale.ROOT);
        Flight flight=repo.findByFlightNumber(flightNum).orElseThrow(()->new FlightNotFoundException(flightNum));

        flight.setStatus(status);

        Flight savedFlight=repo.save(flight);
        return toResponse(savedFlight);
    }


    @Transactional
    public void deleteFlight(String flightNumber){
        String flightNum=flightNumber.trim().toUpperCase(Locale.ROOT);
        Flight flight=repo.findByFlightNumber(flightNum).orElseThrow(()->new FlightNotFoundException(flightNum));

        repo.delete(flight);
    }






}
