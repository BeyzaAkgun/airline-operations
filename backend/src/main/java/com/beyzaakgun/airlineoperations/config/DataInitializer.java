package com.beyzaakgun.airlineoperations.config;

import com.beyzaakgun.airlineoperations.model.Flight;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import com.beyzaakgun.airlineoperations.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final FlightRepository repository;

    public DataInitializer(FlightRepository repository){
        this.repository=repository;

    }

    @Override
    public void run(String... args){
        if(repository.count()>0){
            return;
        }
        Flight flight1=new Flight("TX23","Istanbul","Antalya",FlightStatus.DELAYED,
                OffsetDateTime.parse("2026-09-25T10:00:00Z"),"A12");
        Flight flight2=new Flight("A2X7","Berlin","Izmir", FlightStatus.CANCELED,
                OffsetDateTime.parse("2026-09-25T08:30:00Z"),"B04");
        Flight flight3=new Flight("C4K6","Rome","Los Angeles",FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-09-25T14:00:00Z"),"");
        Flight flight4=new Flight("D8K2","Madrid","Paris",FlightStatus.CANCELED,
                OffsetDateTime.parse("2026-09-25T07:15:00Z"),"C08");
        Flight flight5=new Flight("F54K","Seoul","Tokyo",FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-09-25T11:45:00Z"),"D02");

        List<Flight> flights=List.of(flight1,flight2,flight3,flight4,flight5);

        repository.saveAll(flights);


    }



}
