package ro.endava.akka.workshop.util;

import ro.endava.akka.workshop.es.responses.ESAnalyzeResponse;
import ro.endava.akka.workshop.es.responses.ESToken;
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
            PasswordMessage passwordMessage = new PasswordMessage(token.getToken());
            passwordMessages.add(passwordMessage);
        }
        return new BulkPasswordMessage(passwordMessages);
    }
}
