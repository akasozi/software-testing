package com.demo.testing.sms.twilio;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("twilio")
public class TwilioSettings {

    private String sid;
    private String authtoken;
    private String from;

    public TwilioSettings(String sid, String authtoken, String from) {
        this.sid = sid;
        this.authtoken = authtoken;
        this.from = from;
    }

    public TwilioSettings() {

    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "TwilioSettings{" +
                "sid='" + sid + '\'' +
                ", authtoken='" + authtoken + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
