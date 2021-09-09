package com.mavenclinic.appointmentscheduler.controllers;

import com.mavenclinic.appointmentscheduler.exceptions.AppointmentExistsException;
import com.mavenclinic.appointmentscheduler.exceptions.AppointmentSchedulerException;
import com.mavenclinic.appointmentscheduler.exceptions.InvalidAppointmentParametersException;
import com.mavenclinic.appointmentscheduler.exceptions.MemberDoesNotExistException;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
            throw new InvalidAppointmentParametersException("Invalid request: DateTime format provided does " +
                    "not match expectation (yyyy-dd-MM HH:mm:ss)");
        }

    }

    private void validateAppointment(Integer userId, Appointment newAppointment) {
        Set<LocalDate> memberAppointmentDates = memberAppointmentDateList.get(userId);

        if (memberAppointmentDates == null) {
            throw new MemberDoesNotExistException(String.format("Invalid request: User ID provided (%d) not found",
                    userId));
        }

        if (!memberAppointmentDates.isEmpty()) {
            if (memberAppointmentDates.contains(newAppointment.getAppointmentDate())) {
                throw new AppointmentExistsException(String.format("Invalid request: " +
                        "the provided Member already has an appointment on the provided date %s",
                        newAppointment.getAppointmentDate()));
            }
        }

        int appointmentStartTimeMinute = newAppointment.getAppointmentStartTime().getMinute();

        if (appointmentStartTimeMinute != 0 && appointmentStartTimeMinute != 30) {
            throw new InvalidAppointmentParametersException(String.format("Invalid request: Appointment start time " +
                    "provided (%s) is not on the hour or half-hour", newAppointment.getAppointmentStartTime()));
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
