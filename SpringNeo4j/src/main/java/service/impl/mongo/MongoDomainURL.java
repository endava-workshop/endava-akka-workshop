package service.impl.mongo;

import org.jongo.marshall.jackson.oid.Id;

public class MongoDomainURL {
    @Id
    String address;
    String name;
    long coolDownPeriod = 1000;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCoolDownPeriod() {
        return coolDownPeriod;
    }

    public void setCoolDownPeriod(long coolDownPeriod) {
        this.coolDownPeriod = coolDownPeriod;
    }
}