package com.mavenclinic.appointmentscheduler.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class Appointment {

    private LocalDate appointmentDate;
    private LocalTime appointmentStartTime;

}
