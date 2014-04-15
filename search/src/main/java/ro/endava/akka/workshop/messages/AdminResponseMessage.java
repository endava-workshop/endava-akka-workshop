package ro.endava.akka.workshop.messages;

import java.io.Serializable;

/**
 * Created by cvasii on 4/15/14.
 */
public class AdminResponseMessage implements Serializable {

    private Boolean isOk;

    public AdminResponseMessage(Boolean isOk) {
        this.isOk = isOk;
    }

    public Boolean getIsOk() {
        return isOk;
    }
}
