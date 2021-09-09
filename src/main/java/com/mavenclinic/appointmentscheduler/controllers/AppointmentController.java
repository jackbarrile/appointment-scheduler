package com.mavenclinic.appointmentscheduler.controllers;

import com.mavenclinic.appointmentscheduler.exceptions.InvalidAppointmentParametersException;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@RestController("/appointment")
@Validated
public class AppointmentController {

    private Map<Integer, List<Appointment>> memberAppointments = new HashMap<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @PostMapping("")
    public Appointment saveAppointment(Integer userId, String dateAndStartTimeOfAppointment) {
        validateParameters(userId, dateAndStartTimeOfAppointment);

        LocalDateTime deserializedDateAndStartTimeOfAppointment =
                deserializeFormattedDateAndTimeOfAppointment(dateAndStartTimeOfAppointment);
        LocalTime appointmentEndTime = deserializedDateAndStartTimeOfAppointment.toLocalTime().plusMinutes(30);
        Appointment newAppointment = new Appointment(deserializedDateAndStartTimeOfAppointment.toLocalDate(),
                deserializedDateAndStartTimeOfAppointment.toLocalTime(), appointmentEndTime);

        List<Appointment> memberAppointments = this.memberAppointments.get(userId);
        if (memberAppointments == null) memberAppointments = new ArrayList<>();
        memberAppointments.add(newAppointment);
        this.memberAppointments.put(userId, memberAppointments);

        return newAppointment;
    }

    private void validateParameters(Integer userId, String dateAndStartTimeOfAppointment) {
        if (userId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid request: userID is null");
        }

        if (dateAndStartTimeOfAppointment == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Invalid request: dateAndStartTimeOfAppointment is null");
        }

    }

    private LocalDateTime deserializeFormattedDateAndTimeOfAppointment(String formattedDateAndTimeOfAppointment) {
        return LocalDateTime.parse(formattedDateAndTimeOfAppointment, FORMATTER);
    }
}
