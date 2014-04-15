package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.app.App;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.LocalPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LocalPasswordActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(LocalPasswordActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof LocalPasswordMessage) {
            LocalPasswordMessage inMessage = (LocalPasswordMessage) message;
            try {
                // open resource file
                InputStream inputStream = getClass().getResourceAsStream(inMessage.getResourceFilePath());
                DataInputStream in = new DataInputStream(inputStream);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(in));
                String password;
                List<PasswordMessage> passwordsList = new ArrayList<>(
                        inMessage.getBulkSize());
                while ((password = br.readLine()) != null) {
                    if (passwordsList.size() == inMessage.getBulkSize()) {
                        // send a bulk of passwords for being indexed
                        BulkPasswordMessage outMessage = new BulkPasswordMessage(
                                passwordsList);
                        getSender().tell(outMessage, getSelf());
                        // LOGGER.debug("added a new bulk of local passwords");
                        App.sentChunks.incrementAndGet();
                        passwordsList = new ArrayList<>(inMessage.getBulkSize());
                    }
                    passwordsList.add(new PasswordMessage(password));
                }
                if (passwordsList.size() > 0) {
                    BulkPasswordMessage outMessage = new BulkPasswordMessage(
                            passwordsList);
                    getSender().tell(outMessage, getSelf());
                    App.sentChunks.incrementAndGet();
                }
                // LOGGER.debug("added a new bulk of local passwords");
                in.close();

                LOGGER.debug("finished to load local passwords");
            } catch (Exception e) {// Catch exception if any
                LOGGER.error("Error: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("unknown message : "
                    + message.getClass());
        }
    }

}
