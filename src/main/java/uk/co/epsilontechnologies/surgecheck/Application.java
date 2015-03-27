package uk.co.epsilontechnologies.surgecheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@EnableScheduling
public class Application {

    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }

}