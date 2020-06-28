package com.demo.testing.sms;

public class SmsSend {

    private final boolean isMessageSent;

    public SmsSend(boolean isMessageSent) {
        this.isMessageSent = isMessageSent;
    }

    public boolean isMessageSent() {
        return isMessageSent;
    }

    @Override
    public String toString() {
        return "SmsSend{" +
                "isMessageSent=" + isMessageSent +
                '}';
    }
}
