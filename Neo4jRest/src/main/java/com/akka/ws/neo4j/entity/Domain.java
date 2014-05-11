package com.akka.ws.neo4j.entity;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.akka.ws.neo4j.enums.DomainStatus;

/**
 * @author Radu Ciumag
 */
public class Domain {

    private final String name;
    private final long coolDownPeriod;
    private final long crawledAt;
    private final DomainStatus domainStatus;

    public Domain(final String name, final long coolDownPeriod, final long crawledAt, final DomainStatus domainStatus) {
        this.name = name;
        this.coolDownPeriod = coolDownPeriod;
        this.crawledAt = crawledAt;
        this.domainStatus = domainStatus;
    }

    public Domain(final String name, final long coolDownPeriod, final long crawledAt) {
        this(name, coolDownPeriod, crawledAt, DomainStatus.FOUND);
    }

    public String getName() {
        return name;
    }

    public long getCrawledAt() {
        return crawledAt;
    }

    public long getCoolDownPeriod() {
        return coolDownPeriod;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }

    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		byte[] bytes = (this.name + "0000000000000000").getBytes();
		try {
			return MessageDigest.getInstance("MD5").digest(bytes, 0, bytes.length);
		} catch (Exception e) {
			return super.hashCode();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Domain) {
			Domain domain = (Domain) obj;
			return this.name.equals(domain.getName());
		}
		return false;
	}
    
    @Override
    public String toString() {
        return "Domain{" +
                "name='" + name + '\'' +
                ", coolDownPeriod=" + coolDownPeriod +
                ", crawledAt=" + crawledAt +
                ", domainStatus=" + domainStatus +
                '}';
    }
}
