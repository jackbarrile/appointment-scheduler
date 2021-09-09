package com.mavenclinic.appointmentscheduler.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class InvalidAppointmentParametersException extends AppointmentSchedulerException {

    public InvalidAppointmentParametersException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
