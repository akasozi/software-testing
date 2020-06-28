package com.demo.testing.sms.twilio;

import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioApi {

     public Message creator(String to, String from, String message) throws TwilioException {
         return Message.creator(new PhoneNumber(to),new PhoneNumber(from), message).create();
     }
}
