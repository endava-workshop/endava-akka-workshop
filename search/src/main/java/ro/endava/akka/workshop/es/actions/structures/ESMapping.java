package ro.endava.akka.workshop.es.actions.structures;

/**
 * Created by cvasii on 4/14/14.
 */
public class ESMapping {

    private String attrName;
    private String attrType;

    public ESMapping(String attrName, String attrType) {
        this.attrName = attrName;
        this.attrType = attrType;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrType() {
        return attrType;
    }
}
