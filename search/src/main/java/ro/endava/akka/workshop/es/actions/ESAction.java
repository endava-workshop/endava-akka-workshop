package ro.endava.akka.workshop.es.actions;

/**
 * Created by cosmin on 4/6/14.
 */
public interface ESAction {

    String getIndex();

    String getType();

    String getId();

    String getUrl();

    String getMethod();

    Object getBody();
}
