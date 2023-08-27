package com.kyk.FoodWorld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class FoodWorldApplication {

	public static void main(String[] args) {
//		SpringApplication.run(FoodWorldApplication.class, args);

		// application.properties에서 설정한 값을 pid commend로 사용하기 위해
		SpringApplication application = new SpringApplication(Application.class);
		application.addListeners(new ApplicationPidFileWriter());
		application.run(args);
	}

}
