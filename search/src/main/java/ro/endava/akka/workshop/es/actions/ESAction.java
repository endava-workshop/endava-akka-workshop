package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 * Interface for an action done on ES server
 */
public interface ESAction {

    String getIndex();

    String getType();

    String getId();

    String getUrl();

    String getMethod();

    Object getBody();
}
