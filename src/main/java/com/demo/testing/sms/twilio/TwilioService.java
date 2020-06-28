package com.demo.testing.sms.twilio;

import com.demo.testing.sms.SmsService;
import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "twilio.enabled",
        havingValue ="true"
)
public class TwilioService implements SmsService {

    private final TwilioSettings twilioSettings;
    private final TwilioApi twilioApi;

    @Autowired
    public TwilioService(TwilioSettings twilioSettings, TwilioApi twilioApi) {
        this.twilioSettings = twilioSettings;
        this.twilioApi = twilioApi;
    }

    @Override
    public void send(String to,  String messageText) {
        // Twilio.init("ACd7f6afa14c0904716d7396db3e9a8f7c", "1a80cc5a70512f10c0785bf76ae7bd53");
        Twilio.init(twilioSettings.getSid(), twilioSettings.getAuthtoken());
        try {
            Message message  = twilioApi.creator(to, twilioSettings.getFrom(), messageText);
        //  Message message = Message.creator(new PhoneNumber(toMSISDN),new PhoneNumber(fromMSISDN),messageText).create();

        } catch (TwilioException ex) {
            throw new IllegalStateException("Cannot send Twilio sms", ex);
        }
    }
}
