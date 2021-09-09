package com.mavenclinic.appointmentscheduler.exceptions;

public class InvalidAppointmentParametersException extends Exception {
    public InvalidAppointmentParametersException(String errorMessage) {
        super(errorMessage);
    }
}
