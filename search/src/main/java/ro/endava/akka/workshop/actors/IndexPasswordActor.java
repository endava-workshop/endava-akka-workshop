package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import ro.endava.akka.workshop.es.actions.ESBulkAction;
import ro.endava.akka.workshop.es.actions.ESBulky;
import ro.endava.akka.workshop.es.actions.ESIndexAction;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will index passwords in ES
 */
public class IndexPasswordActor extends UntypedActor {

    private ESRestClient client;
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof BulkPasswordMessage) {
            bulkIndexPasswords((BulkPasswordMessage) message);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }


    /**
     * Bulk index the passwords in ES
     *
     * @param message
     * @throws IOException
     */
    private void bulkIndexPasswords(BulkPasswordMessage message) throws IOException {
        Collection<ESBulky> bulkies = new ArrayList<>();
        for (PasswordMessage passwordMessage : message.getPasswords()) {
            ESIndexAction indexAction = new ESIndexAction.Builder().index("passwords").type(message.getPasswordType().toString()).
                    id(passwordMessage.getPassword()).body(passwordMessage).build();
            bulkies.add(indexAction);
        }

        ESBulkAction bulkAction = new ESBulkAction.Builder().bulkies(bulkies).build();
        client.executeAsyncBlocking(bulkAction);
    }


	@Override
	public void preStart() throws Exception {
		super.preStart();
        ESRestClientFactory factory = new ESRestClientFactory();
        client = factory.getClient(ESRestClientFactory.Type.ASYNC, false);
	}
    
    
}
