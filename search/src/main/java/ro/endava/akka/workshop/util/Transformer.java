package ro.endava.akka.workshop.util;

import org.joda.time.DateTime;
import ro.endava.akka.workshop.dto.ESAnalyzeResponse;
import ro.endava.akka.workshop.dto.ESToken;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cosmin on 3/13/14.
 * Transformer utils class
 */
public class Transformer {

    public static BulkPasswordMessage tokensToPasswords(ESAnalyzeResponse analyzeResponse) {
        List<PasswordMessage> passwordMessages = new ArrayList<>();
        for (ESToken token : analyzeResponse.getTokens()) {
            PasswordMessage passwordMessage = new PasswordMessage(token.getToken(), DateTime.now());
            passwordMessages.add(passwordMessage);
        }
        return new BulkPasswordMessage(passwordMessages);
    }

    public static PasswordMessage tokenToPassword(ESToken esToken) {
        return new PasswordMessage(esToken.getToken(), DateTime.now());
    }
}
