package com.mavenclinic.appointmentscheduler;

import com.mavenclinic.appointmentscheduler.controllers.AppointmentController;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@SpringBootTest
public class AppointmentControllerTests {

    @Autowired
    private AppointmentController appointmentController;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @Test
    public void CreateMemberAppointmentReturnsMemberAppointmentDetails() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);
        Appointment appointment = appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);

        Assertions.assertEquals(appointment.getAppointmentDate(), dateAndStartTimeOfAppointment.toLocalDate());
        Assertions.assertEquals(appointment.getAppointmentStartTime(), dateAndStartTimeOfAppointment.toLocalTime());
    }

    @Test
    public void CreateMemberAppointmentReturnsAppointmentEndTimeThirtyMinutesAfterAppointmentStartTime() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);
        Appointment appointment = appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);

        LocalTime expectedAppointmentEndTime = dateAndStartTimeOfAppointment.toLocalTime().plusMinutes(30);
        Assertions.assertEquals(appointment.getAppointmentEndTime(), expectedAppointmentEndTime);
    }

    @Test
    public void CreateMemberAppointmentWithNullAppointmentStartTimeThrows400() {
        Integer userId = 1;

        try {
            appointmentController.saveAppointment(userId, null);
        } catch (HttpClientErrorException e) {
            Assertions.assertTrue(Objects.requireNonNull(e.getMessage()).contains("400"));
        }

    }

    @Test
    public void CreateMemberAppointmentWithNullUserIdThrows400() {
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);

        try {
            appointmentController.saveAppointment(null, formattedDateAndStartTimeOfAppointment);
        } catch (HttpClientErrorException e) {
            Assertions.assertTrue(Objects.requireNonNull(e.getMessage()).contains("400"));
        }

    }

    @Test
    public void CreateMemberAppointmentWhenAnAppointmentExistsOnTheSameDayThrows409() {

    }

    @Test
    public void CreateMemberAppointmentWithInvalidDateTimeFormatThrows400() {

    }

    @Test
    public void CreateMemberAppointmentWithNonExistantUserIdThrows404() {

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
