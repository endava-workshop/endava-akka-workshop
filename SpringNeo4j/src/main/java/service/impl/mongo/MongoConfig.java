package service.impl.mongo;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

/**
 * Created by ionut on 27.04.2014.
 */
@Component
public class MongoConfig {

    @Autowired
    @Qualifier("mongoHost")
    public String mongoHost;
    @Autowired
    @Qualifier("mongoPort")
    public Integer mongoPort;
    @Autowired
    @Qualifier("mongoDb")
    public String mongoDb;
    private MongoClient mongoClient;
    private Jongo jongo;

    @PostConstruct
    public void init() {
        try {
            mongoClient = createClient();
            jongo = new Jongo(mongoClient.getDB(mongoDb));
        } catch (UnknownHostException e) {
            System.out.println("Mongo initialization failed: " + e.getMessage());
        }
    }

    private MongoClient createClient() throws UnknownHostException {
        MongoClient mongo = new MongoClient(mongoHost, mongoPort);
        mongo.setWriteConcern(WriteConcern.FSYNCED);
        return mongo;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public Jongo getJongo() {
        return jongo;
    }
}
