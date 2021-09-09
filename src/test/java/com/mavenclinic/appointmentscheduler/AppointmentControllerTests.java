package com.mavenclinic.appointmentscheduler;

import com.mavenclinic.appointmentscheduler.controllers.AppointmentController;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class AppointmentControllerTests {

    @Autowired
    private AppointmentController appointmentController;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @Test
    public void CreateMemberAppointmentReturnsMemberAppointmentDetails() {
        Integer userId = 1;
        LocalDateTime dateAndTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndTimeOfAppointment = dateAndTimeOfAppointment.format(FORMATTER);
        Appointment appointment = appointmentController.saveAppointment(userId, formattedDateAndTimeOfAppointment);

        Assertions.assertEquals(appointment.getAppointmentDate(), dateAndTimeOfAppointment.toLocalDate());
        Assertions.assertEquals(appointment.getAppointmentStartTime(), dateAndTimeOfAppointment.toLocalTime());
    }

    @Test
    public void CreateMemberAppointmentReturnsAppointmentEndTimeThirtyMinutesAfterAppointmentStartTime() {

    }

    @Test
    public void CreateMemberAppointmentWithInvalidParametersThrows400() {

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
