package com.mavenclinic.appointmentscheduler.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppointmentSchedulerException extends RuntimeException {

    private final HttpStatus errorCode;

    public AppointmentSchedulerException(String errorMessage, HttpStatus errorCode) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
