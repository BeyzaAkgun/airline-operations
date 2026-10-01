package com.beyzaakgun.airlineoperations.dto;

import com.beyzaakgun.airlineoperations.model.FlightStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateFlightStatusRequest {
    @NotNull(message = "Flight status is required")
    private FlightStatus status;

    public UpdateFlightStatusRequest(){

    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}
