package ro.endava.akka.workshop.messages;

/**
 * Created by cvasii on 4/17/14.
 */
public enum PasswordType {

    DEFAULT("password"),
    COMMON("commonPassword");

    private String value;

    PasswordType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
