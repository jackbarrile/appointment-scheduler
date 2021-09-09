package com.mavenclinic.appointmentscheduler;

import com.mavenclinic.appointmentscheduler.controllers.AppointmentController;
import com.mavenclinic.appointmentscheduler.models.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class AppointmentSchedulerApplication {

	@Autowired
	private AppointmentController appointmentController;

	public static void main(String[] args) {
		SpringApplication.run(AppointmentSchedulerApplication.class, args);
	}

	@PostConstruct
	public void setupIntegrationTestData() {
		Map<Integer, List<Appointment>> memberAppointmentList = new HashMap<>();
		Map<Integer, Set<LocalDate>> memberAppointmentDateList = new HashMap<>();

		LocalDateTime dateAndStartTimeOfFirstMemberAppointment = LocalDateTime.now();
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

}
