package com.example.mailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class MailSenderApplication {

    @Autowired
    private MailVersenderService senderService;

    public static void main(String[] args) {
        SpringApplication.run(MailSenderApplication.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void sendEmail() {

        senderService.sendEmail("nofear992302@gmail.com",
                "Sep rockt",
                "Wir werden es schaffen!");
    }

}
