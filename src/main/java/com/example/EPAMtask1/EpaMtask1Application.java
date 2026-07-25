package com.example.EPAMtask1;

import com.example.EPAMtask1.facade.GymFacade;
import com.example.EPAMtask1.model.Trainee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

@SpringBootApplication
public class EpaMtask1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(EpaMtask1Application.class, args);

		GymFacade facade = context.getBean(GymFacade.class);
		Trainee trainee = facade.createTrainee("Daniil", "Radevich", LocalDate.of(2001,7,8), "Wroclaw");
		System.out.println(trainee);

	}

}
