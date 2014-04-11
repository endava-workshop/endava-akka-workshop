package com.en_workshop.webcrawlerakka.akka.requests.other.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageRequest;

/**
 * The request for obtaining statistics.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class ShowStatisticsRequest extends MessageRequest {

    public ShowStatisticsRequest() {
        super(System.currentTimeMillis());
    }

}
