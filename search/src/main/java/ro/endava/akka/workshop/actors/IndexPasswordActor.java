package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will index passwords in elastic
 */
public class IndexPasswordActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexPasswordActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof BulkPasswordMessage) {
            throw new ApplicationException("Not yet implemented.", ErrorCode.NOT_IMPLEMENTED);
        } else if (message instanceof PasswordMessage) {
            PasswordMessage passwordMessage = (PasswordMessage) message;
            LOGGER.info("Index Password Actor received a bulk password message: " + passwordMessage);
            indexAsyncNonBlocking(passwordMessage);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }


    /**
     * Indexing a password using  rest calls to elastic server, with Spring 4 AsyncRestTemplate, non- blocking,
     * not waiting for the result
     *
     * @param passwordMessage
     * @throws Exception
     */
    private void indexAsyncNonBlocking(PasswordMessage passwordMessage) throws Exception {
        AsyncRestTemplate asyncRestTemplate = buildAsyncRestTemplate();
        HttpEntity httpEntity = new HttpEntity(passwordMessage);
        String url = "http://localhost:9200/passwords/password/" + passwordMessage.getPassword();
        asyncRestTemplate.put(url, httpEntity);
        LOGGER.info("[Rest call non-blocking] We just an index password request to elastic server [fire and forget]");
    }

    /**
     * Build AsyncRestTemplate with custom JodaTime serializer
     *
     * @return
     */
    private AsyncRestTemplate buildAsyncRestTemplate() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false);
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonMessageConverter);
        asyncRestTemplate.setMessageConverters(messageConverters);
        return asyncRestTemplate;
    }
}
