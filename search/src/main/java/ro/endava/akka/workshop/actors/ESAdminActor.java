package ro.endava.akka.workshop.actors;

import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.es.actions.ESCreateIndexAction;
import ro.endava.akka.workshop.es.actions.ESIsIndexAction;
import ro.endava.akka.workshop.es.actions.ESPutMappingAction;
import ro.endava.akka.workshop.es.actions.structures.ESAnalyzer;
import ro.endava.akka.workshop.es.actions.structures.ESFilter;
import ro.endava.akka.workshop.es.client.ESRestClient;
import ro.endava.akka.workshop.es.responses.ESResponse;
import ro.endava.akka.workshop.exceptions.ApplicationException;
import ro.endava.akka.workshop.exceptions.ErrorCode;
import ro.endava.akka.workshop.messages.AdminMessage;
import ro.endava.akka.workshop.messages.AdminResponseMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cosmin on 3/10/14.
 * Actor that will create indexes
 */
public class ESAdminActor extends UntypedActor {
    private final static Logger LOGGER = LoggerFactory.getLogger(ESAdminActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof AdminMessage) {
            initiate();
        } else {
            throw new ApplicationException("Message not supported.", ErrorCode.UNKNOW_MESSAGE_TYPE);
        }
    }

    /**
     * Creating the indexes we need and their mapping, settings in ES
     */
    private void initiate() {
        Boolean isOk = true;

        ESRestClient client = ESRestClient.getInstance();

        isOk = isOk && buildPasswordsIndex(client);
        isOk = isOk && buildArticlesIndex(client);
        isOk = isOk && buildAnalysisIndex(client);

        if (isOk) {
            getSender().tell(new AdminResponseMessage(true), getSelf());
        } else {
            getSender().tell(new AdminResponseMessage(false), getSelf());
        }
    }

    private Boolean buildPasswordsIndex(ESRestClient client) {
        Boolean isOk = true;

        //check if the passwords index exists
        final ESIsIndexAction passExists = new ESIsIndexAction.Builder().index("passwords").build();
        final ESResponse passExistsResp = client.executeAsyncBlocking(passExists);

        //If not create it
        if (!passExistsResp.isOk()) {
            final ESCreateIndexAction createPass = new ESCreateIndexAction.Builder().index("passwords").build();
            final ESResponse createPassResp = client.executeAsyncBlocking(createPass);

            final ESPutMappingAction mappPass = new ESPutMappingAction.Builder().index("passwords").type("password").
                    attribute("password", "string").attribute("indexedDate", "date").build();
            final ESResponse mappPassResp = client.executeAsyncBlocking(mappPass);

            //final ESPutMappingAction mappCommonPass = new ESPutMappingAction.Builder().index("passwords").type("commonPassword").
            //        attribute("password", "string").build();
            //final ESResponse mappCommonPassResp = client.executeAsyncBlocking(mappCommonPass);

            if (createPassResp.isOk() && mappPassResp.isOk() /*&& mappCommonPassResp.isOk()*/) {
                LOGGER.info("The index 'passwords' has been created");
            } else {
                LOGGER.info("The index 'passwords' has not been created. Some errors occurred: "
                        + createPassResp.getJsonString() + ", " + mappPassResp.getJsonString()
                        /*+ ", " + mappCommonPassResp.getJsonString()*/);
                isOk = false;
            }
        } else {
            LOGGER.info("The index 'passwords' is already created");
        }

        return isOk;
    }

    private Boolean buildArticlesIndex(ESRestClient client) {
        Boolean isOk = true;

        //check if the articles index exists
        ESIsIndexAction articlesExists = new ESIsIndexAction.Builder().index("articles").build();
        ESResponse articlesExistsResp = client.executeAsyncBlocking(articlesExists);

        //If not create it
        if (!articlesExistsResp.isOk()) {
            final ESCreateIndexAction createArticles = new ESCreateIndexAction.Builder().index("articles").build();
            final ESResponse createArticlesResp = client.executeAsyncBlocking(createArticles);

            final ESPutMappingAction mappArticles = new ESPutMappingAction.Builder().index("articles").type("article").
                    attribute("domain", "string").attribute("content", "string").build();
            final ESResponse mappArticlesResp = client.executeAsyncBlocking(mappArticles);

            if (createArticlesResp.isOk() && mappArticlesResp.isOk()) {
                LOGGER.info("The index 'articles' has been created");
            } else {
                LOGGER.info("The index 'articles' has not been created. Some errors occurred: "
                        + createArticlesResp.getJsonString() + ", " + mappArticlesResp.getJsonString());
                isOk = false;
            }
        } else {
            LOGGER.info("The index 'articles' is already created");
        }

        return isOk;
    }

    private Boolean buildAnalysisIndex(ESRestClient client) {
        Boolean isOk = true;
        //check if the analysis index exists
        ESIsIndexAction analysisExists = new ESIsIndexAction.Builder().index("analysis").build();
        ESResponse analysisExistsResp = client.executeAsyncBlocking(analysisExists);
        //If not create it
        if (!analysisExistsResp.isOk()) {
            Map<String, Object> props = new HashMap<>();
            props.put("type", "custom");
            props.put("tokenizer", "standard");
            List<String> filter = new ArrayList<>();
            filter.add("stop_words");
            props.put("filter", filter);
            ESAnalyzer esAnalyzer = new ESAnalyzer("myanalyzer", props);

            Map<String, Object> filterProps = new HashMap<>();
            filterProps.put("type", "stop");
            filterProps.put("ignore_case", true);
            List<String> stopWords = new ArrayList<>();

            InputStream inputStream = getClass().getResourceAsStream("/common_stop_words.txt");
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in));
            String password;
            try {
                while ((password = br.readLine()) != null) {
                    stopWords.add(password);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            filterProps.put("stopwords", stopWords);
            ESFilter esFilter = new ESFilter("stop_words", filterProps);

            ESCreateIndexAction createAnalysis = new ESCreateIndexAction.Builder().index("analysis").analyzer(esAnalyzer).filter(esFilter).build();
            ESResponse createAnalysisResp = client.executeAsyncBlocking(createAnalysis);
            if (createAnalysisResp.isOk()) {
                LOGGER.info("The index 'analysis' has been created");
            } else {
                LOGGER.info("The index 'analysis' has not been created. Some errors occurred: "
                        + createAnalysisResp.getJsonString());
                isOk = false;
            }

        } else {
            LOGGER.info("The index 'analysis' is already created");
        }
        return isOk;
    }
}
