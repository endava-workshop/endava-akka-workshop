package ro.endava.akka.workshop.es.actions;

/**
 * Created by cvasii on 4/7/14.
 * Interface to mark that an {@link ro.endava.akka.workshop.es.actions.ESAction} can be used in a bulk request
 */
public interface ESBulky extends ESAction {

    String getESActionName();
}
