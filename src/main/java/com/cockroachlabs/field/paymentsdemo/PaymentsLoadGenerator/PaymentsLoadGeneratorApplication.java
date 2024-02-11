package com.cockroachlabs.field.paymentsdemo.PaymentsLoadGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class PaymentsLoadGeneratorApplication {


	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(PaymentsLoadGeneratorApplication.class, args);

        System.out.println("Running Payments Load Generation App");
        System.out.println();

		StopWatch s = new StopWatch();
		s.start();

		LoadGenerationService loadGenerationService = context.getBean(LoadGenerationService.class);
		loadGenerationService.Run();

		s.stop();
		System.out.println("Finished in " + s.getTotalTimeSeconds() + " seconds");

        System.out.println("Exiting Load Generation App");

		System.exit(0);
	}

}
