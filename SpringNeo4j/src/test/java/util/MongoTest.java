package util;

import com.mongodb.DB;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Ionuț Păduraru
 */
public class MongoTest {

	private static MongoResource mongoResource;
	private static DB db;

    public Jongo jongo;

    @BeforeClass
	public static void startMongo() throws Exception {
		mongoResource = new MongoResource();
		db = mongoResource.getDb("mongo_test");
    }

    @Before
    public void setUp() throws Exception {
        jongo = new Jongo(db);
    }

    public static MongoResource getMongoResource() {
		return mongoResource;
	}

	public static DB getDb() {
		return db;
	}

}
