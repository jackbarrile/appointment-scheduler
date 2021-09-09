package com.mavenclinic.appointmentscheduler.controllers;

import com.mavenclinic.appointmentscheduler.exceptions.AppointmentExistsException;
import com.mavenclinic.appointmentscheduler.exceptions.AppointmentSchedulerException;
import com.mavenclinic.appointmentscheduler.exceptions.InvalidAppointmentParametersException;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Setter
@RestController("/appointment")
@Validated
public class AppointmentController {

    private Map<Integer, List<Appointment>> memberAppointmentList = new HashMap<>();
    private Map<Integer, Set<LocalDate>> memberAppointmentDateList = new HashMap<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @PostMapping("")
    public Appointment saveAppointment(Integer userId, String dateAndStartTimeOfAppointment) {
        validateParameters(userId, dateAndStartTimeOfAppointment);

        LocalDateTime deserializedDateAndStartTimeOfAppointment =
                deserializeFormattedDateAndTimeOfAppointment(dateAndStartTimeOfAppointment);
        LocalTime appointmentEndTime = deserializedDateAndStartTimeOfAppointment.toLocalTime().plusMinutes(30);
        Appointment newAppointment = new Appointment(deserializedDateAndStartTimeOfAppointment.toLocalDate(),
                deserializedDateAndStartTimeOfAppointment.toLocalTime(), appointmentEndTime);

        validateAppointment(userId, newAppointment);
        saveMemberAppointment(userId, newAppointment);

        return newAppointment;
    }

    private LocalDateTime deserializeFormattedDateAndTimeOfAppointment(String formattedDateAndTimeOfAppointment) {
        try {
            return LocalDateTime.parse(formattedDateAndTimeOfAppointment, FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new InvalidAppointmentParametersException(ex.getMessage());
        }

    }

    private void validateParameters(Integer userId, String dateAndStartTimeOfAppointment) {
        if (userId == null) {
            throw new InvalidAppointmentParametersException("Invalid request: userID is null");
        }

        if (dateAndStartTimeOfAppointment == null) {
            throw new InvalidAppointmentParametersException("Invalid request: dateAndStartTimeOfAppointment is null");
        }
    }

    private void validateAppointment(Integer userId, Appointment newAppointment) {
        Set<LocalDate> memberAppointmentDates = memberAppointmentDateList.get(userId);

        if (!(memberAppointmentDates == null) && !memberAppointmentDates.isEmpty()) {
            if (memberAppointmentDates.contains(newAppointment.getAppointmentDate())) {
                throw new AppointmentExistsException(String.format("Invalid request: " +
                        "the provided Member already has an appointment on the provided date %s",
                        newAppointment.getAppointmentDate()));
            }
        }

    }

    private void saveMemberAppointment(Integer userId, Appointment newAppointment) {
        List<Appointment> memberAppointments = memberAppointmentList.get(userId);
        Set<LocalDate> memberAppointmentDates = memberAppointmentDateList.get(userId);

        if (memberAppointments == null) {
            memberAppointments = new ArrayList<>();
            memberAppointmentDates = new HashSet<>();
        }

        memberAppointments.add(newAppointment);
        memberAppointmentDates.add(newAppointment.getAppointmentDate());

        memberAppointmentList.put(userId, memberAppointments);
        memberAppointmentDateList.put(userId, memberAppointmentDates);
    }

    @ExceptionHandler({AppointmentSchedulerException.class})
    public ResponseEntity handleInvalidAppointmentParameters(AppointmentSchedulerException ex) {
        return new ResponseEntity(ex.getMessage(), ex.getErrorCode());
    }
}
