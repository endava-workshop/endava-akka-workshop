package com.en_workshop.webcrawlerakka.akka.actors.other;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.en_workshop.webcrawlerakka.WebCrawlerConstants;
import com.en_workshop.webcrawlerakka.akka.actors.BaseActor;
import com.en_workshop.webcrawlerakka.akka.actors.MasterActor;
import com.en_workshop.webcrawlerakka.akka.requests.StartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStartMasterResponse;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStopMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.master.ControlStopMasterResponse;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.statistics.ShowStatisticsResponse;
import com.en_workshop.webcrawlerakka.enums.ControlActionStatus;

/**
 * All actors control actor
 *
 * @author Radu Ciumag
 */
public class ControlActor extends BaseActor {
    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof ControlStartMasterRequest) {
            /* Is MasterActor created? */
            try {
                final ActorRef masterActor = findLocalActor(null);

                if (null != masterActor) {
                    getSender().tell(new ControlStartMasterResponse((ControlStartMasterRequest) message, ControlActionStatus.FAILED, "MasterActor already exists"),
                            getSelf());
                    return;
                }
            } catch (Exception exc) {
                // Exception ignored
            }

            /* Start MasterActor */
            final ActorRef masterActor = getContext().system().actorOf(Props.create(MasterActor.class, getSelf()), WebCrawlerConstants.MASTER_ACTOR_NAME);
            masterActor.tell(new StartMasterRequest(), ActorRef.noSender());

            getSender().tell(new ControlStartMasterResponse((ControlStartMasterRequest) message, ControlActionStatus.OK, "MasterActor started"), getSelf());
        } else if (message instanceof ControlStopMasterRequest) {
            /* Is MasterActor created? */
            try {
                final ActorRef masterActor = findLocalActor(null);

                if (null != masterActor) {
                    /* Stop MasterActor */
                    context().stop(masterActor);

                    getSender().tell(new ControlStopMasterResponse((ControlStopMasterRequest) message, ControlActionStatus.OK, "MasterActor asked to stop"),
                            getSelf());
                }
            } catch (Exception exc) {
                getSender().tell(new ControlStopMasterResponse((ControlStopMasterRequest) message, ControlActionStatus.FAILED, "MasterActor not found"),
                        getSelf());
            }
        } else if (message instanceof ShowStatisticsRequest) {
            try {
                final ActorRef masterActor = findLocalActor(null);

                if (null != masterActor) {
                    masterActor.tell(message, getSelf());
                }
            } catch (Exception exc) {
                LOG.error("Master Actor not created or just died or is just restarting.");
            }
        } else if (message instanceof ShowStatisticsResponse) {
            final ShowStatisticsResponse showStatisticsResponse = (ShowStatisticsResponse) message;
            LOG.error("ShowStatisticsResponse with result: " + showStatisticsResponse.getStatistics() + ".\n Message: " + showStatisticsResponse.getMessage());

        } else {
            LOG.error("Unknown message: " + message);
            unhandled(message);
        }
    }
}