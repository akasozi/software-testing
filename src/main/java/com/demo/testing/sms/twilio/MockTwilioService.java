package com.demo.testing.sms.twilio;

import com.demo.testing.sms.SmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "twilio.enabled",
        havingValue ="false"
)
public class MockTwilioService implements SmsService {

    @Override
    public void send(String to, String message) {
        System.out.println("***** Sending " + to + " and message " + message);
    }
}
