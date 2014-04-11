package com.en_workshop.webcrawlerakka.akka.requests.other.statistics;

import com.en_workshop.webcrawlerakka.akka.requests.MessageResponse;
import com.en_workshop.webcrawlerakka.entities.DomainLinkStatistics;

import java.util.Map;

/**
 * The response for the statistics request.
 *
 * @author <a href="mailto:roxana.paduraru@endava.com">Roxana PADURARU</a>
 * @since 4/10/14
 */
public class ShowStatisticsResponse extends MessageResponse {

    private final Map<String, DomainLinkStatistics> statistics;
    private final String message;

    public ShowStatisticsResponse(final ShowStatisticsRequest showStatisticsRequest, final Map<String, DomainLinkStatistics> statistics, final String message) {
        super(System.currentTimeMillis(), showStatisticsRequest);

        this.statistics = statistics;
        this.message = message;
    }

    public Map<String, DomainLinkStatistics> getStatistics() {
        return statistics;
    }

    public String getMessage() {
        return message;
    }
}
