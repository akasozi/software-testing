package com.demo.testing.sms.twilio;

import com.stripe.exception.StripeException;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Currency;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class TwilioServiceTest {

    @Mock
    private TwilioApi twilioApi;
    @Mock
    private TwilioSettings twilioSettings;

    private TwilioService underTest;

    @BeforeEach
    void setUp() {
         MockitoAnnotations.initMocks(this);
         underTest = new TwilioService(twilioSettings, twilioApi);
    }

    @Test
    void itShouldNotSendSMSWhenExceptionIsThrown() {
        // Given from, to and message
        String from = "+15005550006";
        String to = "+254728107303";
        String message = "Hi There!";
        // ...twilio sid
        given(twilioSettings.getSid())
                .willReturn("ACd7f6afa14c0904716d7396db3e9a8f7c");
        // ... twilio authtoken
        given(twilioSettings.getAuthtoken())
                .willReturn("a80cc5a70512f10c0785bf76ae7bd53");
        // ... twilio from

        // Throw exception when stripe api is called
        TwilioException twilioException = mock(TwilioException.class);
        doThrow(twilioException).when(twilioApi).creator(any(), any(), any());
        // When
        // Then
        assertThatThrownBy(() -> underTest.send(to, message))
                .isInstanceOf(IllegalStateException.class)
                .hasRootCause(twilioException)
                .hasMessageContaining("Cannot send Twilio sms");
    }

    @Test
    void itShouldSendSMS() {
        // Given from, to and message
        String from = "+15005550006";
        String to = "+254728107303";
        String message = "Hi There!";
        // ...twilio sid
        given(twilioSettings.getSid())
                .willReturn("ACd7f6afa14c0904716d7396db3e9a8f7c");
        // ... twilio authtoken
        given(twilioSettings.getAuthtoken())
                .willReturn("a80cc5a70512f10c0785bf76ae7bd53");
        // ... twilio from
        given(twilioSettings.getFrom())
                .willReturn(from);

        // ... When
        underTest.send(to, message);
        // ... Then
        ArgumentCaptor<String> fromArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);

        then(twilioApi).should().creator(toArgumentCaptor.capture(),
                                         fromArgumentCaptor.capture(),
                                         messageArgumentCaptor.capture());

        String toArgumentCaptorValue = toArgumentCaptor.getValue();
        String fromArgumentCaptorValue = fromArgumentCaptor.getValue();
        String messageArgumentCaptorValue = messageArgumentCaptor.getValue();

        assertThat(toArgumentCaptorValue).isEqualTo(to);
        assertThat(fromArgumentCaptorValue).isEqualTo(from);
        assertThat(messageArgumentCaptorValue).isEqualTo(message);

    }


}