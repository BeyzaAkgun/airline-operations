package com.beyzaakgun.airlineoperations.controller;

import com.beyzaakgun.airlineoperations.dto.CreateFlightRequest;
import com.beyzaakgun.airlineoperations.dto.FlightResponse;
import com.beyzaakgun.airlineoperations.exception.DuplicateFlightException;
import com.beyzaakgun.airlineoperations.model.FlightStatus;
import com.beyzaakgun.airlineoperations.repository.FlightRepository;
import com.beyzaakgun.airlineoperations.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.beyzaakgun.airlineoperations.exception.FlightNotFoundException;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightService service;

    @Test
    void shouldReturnBadRequestWhenOriginIsBlank() throws Exception {
        String requestBody = """
        {
          "flightNumber": "TEST01",
          "origin": "   ",
          "destination": "Antalya",
          "status": "SCHEDULED",
          "departureTime": "2026-10-01T10:00:00Z",
          "gate": "A12"
        }
        """;

        mockMvc.perform(
                        post("/api/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.origin").isNotEmpty());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnNotFoundWhenFlightDoesNotExist() throws Exception{
        when(service.getFlightByNumber("UNKNOWN"))
                .thenThrow(new FlightNotFoundException("UNKNOWN"));

        mockMvc.perform(get("/api/flights/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Flight UNKNOWN was not found."))
                .andExpect(jsonPath("$.path").value("/api/flights/UNKNOWN"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());


                verify(service).getFlightByNumber("UNKNOWN");


    }

    @Test
    void shouldReturnConflictWhenFlightAlreadyExists() throws Exception{
        String requestBody= """
                {
                          "flightNumber": "TX23",
                          "origin": "Istanbul",
                          "destination": "Antalya",
                          "status": "SCHEDULED",
                          "departureTime": "2026-10-01T10:00:00Z",
                          "gate": "A12"
                }
                """;

        when(service.createFlight(any(CreateFlightRequest.class)))
                .thenThrow(new DuplicateFlightException("TX23"));

        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)

        ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "Flight TX23 already exists."))
                .andExpect(jsonPath("$.path").value("/api/flights"))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());

        verify(service).createFlight(any(CreateFlightRequest.class));

    }

    @Test
    void shouldReturnCreatedWhenFlightIsValid() throws Exception{
        String requestBody = """
        {
          "flightNumber": "TEST01",
          "origin": "Istanbul",
          "destination": "Antalya",
          "status": "SCHEDULED",
          "departureTime": "2026-10-01T10:00:00Z",
          "gate": "A12"
        }
        """;

        FlightResponse res=new FlightResponse("TEST01",
                "Istanbul","Antalya", FlightStatus.SCHEDULED,
                OffsetDateTime.parse("2026-10-01T10:00:00Z"),"A12");

        when(service.createFlight(any(CreateFlightRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("TEST01"))
                .andExpect(jsonPath("$.origin").value("Istanbul"))
                .andExpect(jsonPath("$.destination").value("Antalya"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.departureTime").value("2026-10-01T10:00:00Z"))
                .andExpect(jsonPath("$.gate").value("A12"));

        verify(service).createFlight(any(CreateFlightRequest.class));

    }

    @Test
    void shouldReturnNoContentWhenFlightIsDeleted() throws Exception{
        mockMvc.perform(delete("/api/flights/TX23"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).deleteFlight("TX23");
    }







}
