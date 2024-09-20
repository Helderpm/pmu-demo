package com.pmu2.exec;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the ExecApplication Spring Boot application.
 * The main method runs the Spring Boot application using {@link SpringApplication#run(Class, String...)} method.
 */
@SpringBootApplication
public class ExecApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExecApplication.class, args);
    }

}
