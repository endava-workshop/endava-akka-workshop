package com.en_workshop.webcrawlerakka;

/**
 * @author Radu Ciumag
 */
public class WebCrawlerConstants {

    /* Actors names / paths */
    public static final String SYSTEM_NAME = "webCrawlerSystem";
    public static final String MASTER_ACTOR_NAME = "masterActor";

    public static final String CONTROL_ACTOR_NAME = "controlActor";
    public static final String STATUS_ACTOR_NAME = "statusActor";

    public static final String DOMAIN_MASTER_ACTOR_NAME = "domainMasterActor";
    public static final String PROCESSING_MASTER_ACTOR_NAME = "processingMasterActor";
    public static final String PERSISTENCE_MASTER_ACTOR_NAME = "persistenceMasterActor";
    public static final String DOWNLOAD_URL_ACTOR_NAME = "downloadUrlActor";

    public static final String DOMAIN_ACTOR_PART_NAME = "domainActor_";

    public static final String ACTOR_SYSTEM_NETWORK_ADDRESS = "";

    public static final String STATISTICS_ACTOR_NAME = "statisticsActor";

    /* HTTP related constants */
    public static final String HTTP_CUSTOM_HEADER_RESPONSE_CODE = "CRAWL-ResponseCode";

    public static final String HTTP_RESPONSE_CODE_NONE = "0";
    public static final String HTTP_RESPONSE_CODE_OK = "200";

    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    public static final String[] ACCEPTED_MIME_TYPES = new String[]{"text/html"}; // http://en.wikipedia.org/wiki/Internet_media_type
    public static final String[] ACCEPTED_PROTOCOLS = new String[]{"http", "https"};

    /* Other */
    public static final int DOMAINS_CRAWL_MAX_COUNT = 5; // Maximum number of domains to crawl at one time
    public static final long DOMAINS_REFRESH_PERIOD = 1 * 60 * 1000; // 10 minutes
    public static final long DOMAIN_DEFAULT_COOLDOWN = 20 * 1000; // 60 seconds
    public static final int CONNECTION_EXCEPTION_TRIALS = 3; //the no of trials for a domain that throws IOException and HttpHostConnectException
    public static final long CONNECTION_EXCEPTION_RETRY = 1 * 60 * 1000; // 10 minutes

    public static final long EMPTY_NEXT_LINK_TRIALS = 10;

}
