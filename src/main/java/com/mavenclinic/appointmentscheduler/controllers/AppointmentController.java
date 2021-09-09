package com.mavenclinic.appointmentscheduler.controllers;

import com.mavenclinic.appointmentscheduler.models.Appointment;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@RestController("/appointment")
public class AppointmentController {

    private Map<Integer, List<Appointment>> memberAppointments = new HashMap<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @PostMapping("")
    public Appointment saveAppointment(Integer userId, String formattedDateAndTimeOfAppointment) {
        LocalDateTime dateAndTimeOfAppointment = 
                deserializeFormattedDateAndTimeOfAppointment(formattedDateAndTimeOfAppointment);
        Appointment newAppointment = new Appointment(dateAndTimeOfAppointment.toLocalDate(),
                dateAndTimeOfAppointment.toLocalTime());

        // todo maybe add this to a method
        List<Appointment> memberAppointments = this.memberAppointments.get(userId);
        if (memberAppointments == null) memberAppointments = new ArrayList<>();
        memberAppointments.add(newAppointment);
        this.memberAppointments.put(userId, memberAppointments);

        return newAppointment;
    }

    private LocalDateTime deserializeFormattedDateAndTimeOfAppointment(String formattedDateAndTimeOfAppointment) {
        return LocalDateTime.parse(formattedDateAndTimeOfAppointment, FORMATTER);
    }
}
