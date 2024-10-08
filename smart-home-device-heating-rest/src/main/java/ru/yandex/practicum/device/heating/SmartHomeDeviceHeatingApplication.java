package ru.yandex.practicum.device.heating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartHomeDeviceHeatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHomeDeviceHeatingApplication.class, args);
    }
}
