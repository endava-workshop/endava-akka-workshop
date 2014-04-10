package com.en_workshop.webcrawlerakka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlResponse;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlStartMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.control.ControlStopMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.status.StatusMasterRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.status.StatusRequest;
import com.en_workshop.webcrawlerakka.akka.requests.other.status.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Radu Ciumag
 */
public class WebCrawlerConsole {
    private static final Logger LOG = LoggerFactory.getLogger(WebCrawlerConsole.class);

    private static final WebCrawlerConsole INSTANCE = new WebCrawlerConsole();

    private static final Timeout TIMEOUT = new Timeout(Duration.create(5, TimeUnit.SECONDS));

    private static final String COMMAND_STS_MASTER = "statusMaster";
    private static final String COMMAND_CTRL_START_MASTER = "startMaster";
    private static final String COMMAND_CTRL_STOP_MASTER = "stopMaster";
    private static final String COMMAND_EXIT = "exit";

    private static final Map<String, String> COMMANDS = new HashMap<String, String>() {
        {
            put(COMMAND_STS_MASTER, "Query the status of MasterActor");

            put(COMMAND_CTRL_START_MASTER, "Start MasterActor and all sub-actors");
            put(COMMAND_CTRL_STOP_MASTER, "Stop MasterActor and all sub-actors");

            put(COMMAND_EXIT, "Stop the console and exit the actor system and the application");
        }
    };

    private ActorSystem actorSystem;
    private ActorRef controlActor;
    private ActorRef statusActor;

    /**
     * Micro console supported commands:
     * {@see COMMANDS}
     */
    public static void microConsole(final ActorSystem actorSystem, final ActorRef controlActor, final ActorRef statusActor) {
        INSTANCE.actorSystem = actorSystem;
        INSTANCE.controlActor = controlActor;
        INSTANCE.statusActor = statusActor;

        //TODO Validate parameters

        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = null;
            while (!COMMAND_EXIT.equals(input)) {
                System.out.print("crawler> ");
                input = reader.readLine();

                /* Pre-process the input */
                if (null == input || 0 == input.trim().length()) {
                    continue;
                }
                input = input.trim();

                /* Display the commands list */
                if ("?".equals(input)) {
                    INSTANCE.displaySupportedCommands();
                }

                /* Process the input */
                if (COMMAND_STS_MASTER.equals(input)) {
                    INSTANCE.commandStatusMaster();
                } else if (COMMAND_CTRL_START_MASTER.equals(input)) {
                    INSTANCE.commandStartMaster();
                } else if (COMMAND_CTRL_STOP_MASTER.equals(input)) {
                    INSTANCE.commandStopMaster();
                } else if (!COMMAND_EXIT.equals(input)) {
                    LOG.debug("Command " + input + " is not known. Type ? for all known commands.");
                }
            }
        } catch (IOException exc) {
            LOG.error(exc.getMessage(), exc);
        } finally {
            /* Stop the actor system */
            INSTANCE.actorSystem.shutdown();
        }
    }

    /**
     * Display the list of supported commands
     */
    private void displaySupportedCommands() {
        for (String commandId : COMMANDS.keySet()) {
            LOG.info(commandId + " - " + COMMANDS.get(commandId));
        }
    }

    /**
     * COMMAND_STS_MASTER
     */
    private void commandStatusMaster() {
        doAsyncStatusCommand(new StatusMasterRequest(), COMMAND_STS_MASTER);
    }

    /**
     * COMMAND_CTRL_START_MASTER
     */
    private void commandStartMaster() {
        doAsyncControlCommand(new ControlStartMasterRequest(), COMMAND_CTRL_START_MASTER);
    }

    /**
     * COMMAND_CTRL_STOP_MASTER
     */
    private void commandStopMaster() {
        doAsyncControlCommand(new ControlStopMasterRequest(), COMMAND_CTRL_STOP_MASTER);
    }

    /**
     * Execute a control command
     *
     * @param message       The control message
     * @param commandString The command string
     */
    private void doAsyncControlCommand(final ControlRequest message, final String commandString) {
        final Future<Object> result = Patterns.ask(controlActor, message, TIMEOUT);
        result.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(final Object object) throws Throwable {
                if (object instanceof ControlResponse) {
                    final ControlResponse controlResponse = (ControlResponse) object;
                    LOG.info("\"" + commandString + "\" succeeded with result: " + controlResponse.getStatus() + ". Message: " + controlResponse.getMessage());
                } else {
                    LOG.info("\"" + commandString + "\" succeeded with result: " + object);
                }
            }
        }, actorSystem.dispatcher());
        result.onFailure(new OnFailure() {
            @Override
            public void onFailure(final Throwable throwable) throws Throwable {
                LOG.error("\"" + commandString + "\" failed with exception: " + throwable.getMessage(), throwable);
            }
        }, actorSystem.dispatcher());
    }

    /**
     * Execute a status command
     *
     * @param message       The status request
     * @param commandString The command string
     */
    private void doAsyncStatusCommand(final StatusRequest message, final String commandString) {
        final Future<Object> result = Patterns.ask(statusActor, message, TIMEOUT);
        result.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(final Object object) throws Throwable {
                if (object instanceof StatusResponse) {
                    final StatusResponse statusResponse = (StatusResponse) object;
                    LOG.info("\"" + commandString + "\" succeeded with result: " + statusResponse.getStatus() + ". Message: " + statusResponse.getMessage());
                } else {
                    LOG.info("\"" + commandString + "\" succeeded with result: " + object);
                }
            }
        }, actorSystem.dispatcher());
        result.onFailure(new OnFailure() {
            @Override
            public void onFailure(final Throwable throwable) throws Throwable {
                LOG.error("\"" + commandString + "\" failed with exception: " + throwable.getMessage(), throwable);
            }
        }, actorSystem.dispatcher());
    }
}
