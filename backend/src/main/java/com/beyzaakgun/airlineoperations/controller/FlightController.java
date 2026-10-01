package com.beyzaakgun.airlineoperations.controller;

import com.beyzaakgun.airlineoperations.dto.CreateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.FlightResponse;
import com.beyzaakgun.airlineoperations.dto.UpdateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.UpdateFlightStatusRequest;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import com.beyzaakgun.airlineoperations.service.FlightService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService){
        this.flightService=flightService;
    }

    @GetMapping
    public List<FlightResponse> getAllFlights(@RequestParam(name = "status",required = false)
                                                  FlightStatus status){


        if(status!=null){
            return flightService.getFlightsByStatus(status);
        }
        return flightService.getAllFlights();

    }
    @GetMapping("/{flightNumber}")
    public FlightResponse getFlightByNumber(@PathVariable("flightNumber") String flightNumber){
        return flightService.getFlightByNumber(flightNumber);

    }
    @PostMapping()
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody CreateFlightRequest request){
        FlightResponse response=flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable("flightNumber") String flightNumber,
                                                       @Valid @RequestBody UpdateFlightRequest request){
        FlightResponse response=flightService.updateFlight(flightNumber,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{flightNumber}/status")
    public ResponseEntity<FlightResponse> updateFlightStatus(@PathVariable("flightNumber") String flightNumber,
            @Valid @RequestBody UpdateFlightStatusRequest request){

        FlightResponse response=flightService.updateFlightStatus(flightNumber,request.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable("flightNumber") String flightNumber){
        flightService.deleteFlight(flightNumber);
        return ResponseEntity.noContent().build();

    }
}
