package com.mavenclinic.appointmentscheduler.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class AppointmentExistsException extends AppointmentSchedulerException {

    public AppointmentExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.CONFLICT);
    }
}
