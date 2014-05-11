package ro.endava.akka.workshop.app;

import ro.endava.akka.workshop.actors.IndexDispatcherActor;
import ro.endava.akka.workshop.actors.SearchRouterActor;
import ro.endava.akka.workshop.messages.LocalPasswordMessage;
import ro.endava.akka.workshop.messages.PasswordType;
import ro.endava.akka.workshop.messages.SearchPasswordMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

    public static void main(String[] args) throws Exception {
        ActorSystem akkaSystem = ActorSystem.create("SearchAkkaSystem");

        final Props indexProps = Props.create(IndexDispatcherActor.class);
        ActorRef indexDispatcherActor = akkaSystem.actorOf(indexProps);

       // indexDispatcherActor.tell(new LocalPasswordMessage(
         //       "/common_passwords.txt", 10000), ActorRef.noSender());

        final Props searchProps = Props.create(SearchRouterActor.class);
        ActorRef searchRouterActor = akkaSystem.actorOf(searchProps);

        searchRouterActor.tell(new SearchPasswordMessage(0L, 10L), ActorRef.noSender());


        //will run forever
    }

}
