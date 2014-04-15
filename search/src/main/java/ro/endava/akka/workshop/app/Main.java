package ro.endava.akka.workshop.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.endava.akka.workshop.actors.ESAdminActor;
import ro.endava.akka.workshop.actors.IndexDispatcherActor;
import ro.endava.akka.workshop.messages.AdminMessage;
import ro.endava.akka.workshop.messages.LocalPasswordMessage;
import scala.Option;
import scala.concurrent.Future;
import scala.util.Try;

public class Main {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(Main.class);

    public static void main(String[] args) {
        ActorSystem akkaSystem = ActorSystem.create("searchAkkaSystem");

        final Props properties = Props.create(ESAdminActor.class);
        ActorRef esAdminActor = akkaSystem.actorOf(properties);

        Future<Object> ask = Patterns.ask(esAdminActor, new AdminMessage(), 1000);
        Option<Try<Object>> value = ask.value();
        final Props props = Props.create(IndexDispatcherActor.class);
        ActorRef indexDispatcherActor = akkaSystem.actorOf(props);

        LocalPasswordMessage message = new LocalPasswordMessage(
                "/common_passwords.txt", 10000);

        indexDispatcherActor.tell(message, ActorRef.noSender());

    }

}
