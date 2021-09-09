package com.mavenclinic.appointmentscheduler.exceptions;

import org.springframework.http.HttpStatus;

public class MemberDoesNotExistException extends AppointmentSchedulerException {

    public MemberDoesNotExistException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}
