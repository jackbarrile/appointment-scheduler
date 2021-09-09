package com.mavenclinic.appointmentscheduler;

import com.mavenclinic.appointmentscheduler.controllers.AppointmentController;
import com.mavenclinic.appointmentscheduler.exceptions.AppointmentExistsException;
import com.mavenclinic.appointmentscheduler.exceptions.InvalidAppointmentParametersException;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@SpringBootTest
public class AppointmentControllerTests {

    @Autowired
    private AppointmentController appointmentController;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @BeforeEach
    public void setup() {
        appointmentController.setMemberAppointmentList(new HashMap<>());
        appointmentController.setMemberAppointmentDateList(new HashMap<>());
    }

    @Test
    public void CreateMemberAppointmentReturnsMemberAppointmentDetails() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);
        Appointment appointment = appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);

        Assertions.assertEquals(dateAndStartTimeOfAppointment.toLocalDate(), appointment.getAppointmentDate());
        Assertions.assertEquals(dateAndStartTimeOfAppointment.toLocalTime(), appointment.getAppointmentStartTime());
    }

    @Test
    public void CreateMemberAppointmentReturnsAppointmentEndTimeThirtyMinutesAfterAppointmentStartTime() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);
        Appointment appointment = appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);

        LocalTime expectedAppointmentEndTime = dateAndStartTimeOfAppointment.toLocalTime().plusMinutes(30);
        Assertions.assertEquals(expectedAppointmentEndTime, appointment.getAppointmentEndTime());
    }

    @Test
    public void CreateMemberAppointmentWhenAnAppointmentExistsOnTheSameDayThrows409() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfFirstAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfFirstAppointment = dateAndStartTimeOfFirstAppointment.format(FORMATTER);

        appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfFirstAppointment);

        LocalDateTime dateAndStartTimeOfSecondAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 17, 30, 25);
        String formattedDateAndStartTimeOfSecondAppointment = dateAndStartTimeOfSecondAppointment.format(FORMATTER);

        try {
            appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfSecondAppointment);
            Assertions.fail();
        } catch (AppointmentExistsException e) {
            Assertions.assertEquals(String.format("Invalid request: " +
                            "the provided Member already has an appointment on the provided date %s",
                    dateAndStartTimeOfSecondAppointment.toLocalDate()), e.getMessage());
            Assertions.assertEquals(HttpStatus.CONFLICT, e.getErrorCode());
        }

    }

    @Test
    public void CreateMemberAppointmentWithInvalidDateTimeFormatThrows400() {
        DateTimeFormatter badFormat = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm");

        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(badFormat);

        try {
            appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);
            Assertions.fail();
        } catch (InvalidAppointmentParametersException e) {
            Assertions.assertEquals("Text '2021-29-09 19:30' could not be parsed at index 16", e.getMessage());
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    public void CreateMemberAppointmentWithNonExistentUserIdThrows404() {

    }

    @Test
    public void CreateMemberAppointmentWithDateTimeNotOnTheHourOrHalfHourThrows400() {

    }

    @Test
    public void GetMemberAppointmentsReturnsAllMemberAppointmentDetails() {

    }

    @Test
    public void GetMemberAppointmentWithInvalidParametersThrows400() {

    }

    @Test
    public void GetMemberAppointmentWithNonExistentUserIdThrows404() {
    }


}
