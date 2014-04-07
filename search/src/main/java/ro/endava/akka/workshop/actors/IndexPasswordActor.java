package ro.endava.akka.workshop.actors;

import ro.endava.akka.workshop.es.actions.*;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.core.Bulk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.client.ESRestClientFactory;
import ro.endava.akka.workshop.es.client.ESRestClientSettings;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.BulkPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordMessage;
import ro.endava.akka.workshop.util.CustomErrorHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will index passwords in elastic
 */
public class IndexPasswordActor extends UntypedActor {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexPasswordActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof BulkPasswordMessage) {
            bulkIndexPasswords((BulkPasswordMessage) message);
        } else if (message instanceof PasswordMessage) {
            PasswordMessage passwordMessage = (PasswordMessage) message;
            LOGGER.info("Index Password Actor received a bulk password message: " + passwordMessage);
            indexAsyncNonBlocking(passwordMessage);
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }


    private void bulkIndexPasswords(BulkPasswordMessage message) throws IOException {



        AsyncRestTemplate asyncRestTemplate = buildAsyncRestTemplate();
        asyncRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        StringBuilder body = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false);
        for (PasswordMessage password : message.getPasswords()) {
//            String pass = objectMapper.writeValueAsString(password);
//            body.append("{\"index\": {\"_index\": \"passwords\", \"_type\": \"password\"}}" + "\n" + pass + "\n");
        }

        JsonObject obj1 = new JsonObject();
        obj1.addProperty("_index", "passwords");
        obj1.addProperty("_type", "password");
        JsonArray jsonElements = new JsonArray();
        jsonElements.add(obj1);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("index", obj1);

        JsonObject obj2 = new JsonObject();
        obj2.addProperty("password", "passwords");
        obj2.addProperty("indexedDate", "2014-04-02T16:41:45.963Z");

        JsonObject obj3 = new JsonObject();


        String bulkBody = jsonObject.toString() + "\n" + obj2.toString() + "\n";

        String pass = objectMapper.writeValueAsString(message.getPasswords().get(0));
        body.append("{\"index\": {\"_index\": \"passwords\", \"_type\": \"password\"}}" + "\n" + pass + "\n");
        String body1 = objectMapper.writeValueAsString(body);
        HttpEntity<String> httpEntity = new HttpEntity(bulkBody);
        String url = "http://localhost:9200/_bulk";
        URL url1 = new URL(url);
        String b = "{\"index\":{\"_index\":\"passwords\",\"_type\":\"password\"}}\n{\"password\":\"Buna\",\"indexedDate\":\"2014-04-02T16:41:45.963Z\"}\n";
//        ListenableFuture<?> futureResponse = asyncRestTemplate.put(url, new HttpEntity<String>(b));
//        try {
//            Object o = futureResponse.get();
//            LOGGER.info("[Tokenizer with callback] Tokens successfully retrieved : " + o);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        futureResponse.addCallback(new ListenableFutureCallback<Object>() {
//            @Override
//            public void onSuccess(Object response) {
//                LOGGER.info("[Tokenizer with callback] Tokens successfully retrieved : " + response);
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                LOGGER.info("[Tokenizer with callback] Error indexing: " + throwable.getMessage());
//            }
//        });
        ListenableFuture<ResponseEntity<Object>> future = asyncRestTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
        asyncRestTemplate.setErrorHandler(new CustomErrorHandler());
        try {
            ResponseEntity<Object> objectResponseEntity = future.get();
            LOGGER.info("[Tokenizer with callback] Tokens successfully retrieved : " + objectResponseEntity);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
