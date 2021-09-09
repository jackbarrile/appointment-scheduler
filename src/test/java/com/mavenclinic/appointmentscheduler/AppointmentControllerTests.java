package com.mavenclinic.appointmentscheduler;

import com.mavenclinic.appointmentscheduler.controllers.AppointmentController;
import com.mavenclinic.appointmentscheduler.exceptions.AppointmentExistsException;
import com.mavenclinic.appointmentscheduler.exceptions.InvalidAppointmentParametersException;
import com.mavenclinic.appointmentscheduler.exceptions.MemberDoesNotExistException;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
public class AppointmentControllerTests {

    @Autowired
    private AppointmentController appointmentController;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");

    @BeforeEach
    public void setup() {
        Map<Integer, List<Appointment>> memberAppointmentList = new HashMap<>();
        Map<Integer, Set<LocalDate>> memberAppointmentDateList = new HashMap<>();

        LocalDateTime dateAndStartTimeOfFirstMemberAppointment = LocalDateTime.of(2021,
                Month.APRIL, 29, 19, 30, 40);
        Appointment firstMemberAppointment = new Appointment(dateAndStartTimeOfFirstMemberAppointment.toLocalDate(),
                dateAndStartTimeOfFirstMemberAppointment.toLocalTime(),
                dateAndStartTimeOfFirstMemberAppointment.toLocalTime().plusMinutes(30));

        List<Appointment> firstMemberAppointmentList = new ArrayList<>();
        firstMemberAppointmentList.add(firstMemberAppointment);
        Set<LocalDate> firstMemberAppointmentStartDates =
                new HashSet<>(List.of(dateAndStartTimeOfFirstMemberAppointment.toLocalDate()));

        memberAppointmentList.put(1, firstMemberAppointmentList);
        memberAppointmentDateList.put(1, firstMemberAppointmentStartDates);

        appointmentController.setMemberAppointmentList(memberAppointmentList);
        appointmentController.setMemberAppointmentDateList(memberAppointmentDateList);
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
            Assertions.assertEquals("Invalid request: DateTime format provided does not match expectation " +
                    "(yyyy-dd-MM HH:mm:ss)", e.getMessage());
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getErrorCode());
        }

    }

    @Test
    public void CreateMemberAppointmentWithNonExistentUserIdThrows404() {
        Integer userId = 5;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 30, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);

        try {
            appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);
            Assertions.fail();
        } catch (MemberDoesNotExistException e) {
            Assertions.assertEquals(String.format("Invalid request: User ID provided (%d) not found", userId),
                    e.getMessage());
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    public void CreateMemberAppointmentWithDateTimeNotOnTheHourOrHalfHourThrows400() {
        Integer userId = 1;
        LocalDateTime dateAndStartTimeOfAppointment = LocalDateTime.of(2021,
                Month.SEPTEMBER, 29, 19, 45, 40);
        String formattedDateAndStartTimeOfAppointment = dateAndStartTimeOfAppointment.format(FORMATTER);

        try {
            appointmentController.saveAppointment(userId, formattedDateAndStartTimeOfAppointment);
            Assertions.fail();
        } catch (InvalidAppointmentParametersException e) {
            Assertions.assertEquals(String.format("Invalid request: Appointment start time provided (%s) is not on the " +
                            "hour or half-hour", dateAndStartTimeOfAppointment.toLocalTime()),
                    e.getMessage());
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getErrorCode());
        }
    }

    @Test
    public void GetMemberAppointmentsReturnsAllMemberAppointmentDetails() {
        Integer userId = 1;

        List<Appointment> memberAppointments = appointmentController.getMemberAppointments(userId);

        LocalDateTime expectedDateAndStartTimeOfMemberAppointment = LocalDateTime.of(2021,
                Month.APRIL, 29, 19, 30, 40);
        Appointment expectedAppointment = new Appointment(expectedDateAndStartTimeOfMemberAppointment.toLocalDate(),
                expectedDateAndStartTimeOfMemberAppointment.toLocalTime(),
                expectedDateAndStartTimeOfMemberAppointment.toLocalTime().plusMinutes(30));
        Appointment actualAppointment = memberAppointments.get(0);

        Assertions.assertEquals(1, memberAppointments.size());
        Assertions.assertEquals(expectedAppointment, actualAppointment);
    }

    @Test
    public void GetMemberAppointmentWithInvalidParametersThrows400() {

    }

    @Test
    public void GetMemberAppointmentWithNonExistentUserIdThrows404() {
    }


}
