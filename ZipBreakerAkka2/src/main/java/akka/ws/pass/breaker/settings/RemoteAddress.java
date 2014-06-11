package akka.ws.pass.breaker.settings;

public class RemoteAddress {

	private String alias;
	private String ip;
	private int port;
	private String protocol;
	private String actorSystemName;
	private int workersPerProcess;
	
	private RemoteAddress() {};

	public String getAlias() {
		return alias;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getActorSystemName() {
		return actorSystemName;
	}
	
	public int getWorkersPerProcess() {
		return workersPerProcess;
	}

	static RemoteAddressBuilder newBuilder() {
		return new RemoteAddressBuilder();
	}
	
	static class RemoteAddressBuilder {
		private RemoteAddress remoteAddress;
		public RemoteAddressBuilder() {
			remoteAddress = new RemoteAddress();
		};
		public RemoteAddressBuilder withAlias(String alias) {
			remoteAddress.alias = alias;
			return this;
		}
		public RemoteAddressBuilder withIP(String ip) {
			remoteAddress.ip = ip;
			return this;
		}
		public RemoteAddressBuilder withPort(int port) {
			remoteAddress.port = port;
			return this;
		}
		public RemoteAddressBuilder withProtocol(String protocol) {
			remoteAddress.protocol = protocol;
			return this;
		}
		public RemoteAddressBuilder withActorSystemName(String actorSystemName) {
			remoteAddress.actorSystemName = actorSystemName;
			return this;
		}
		public RemoteAddressBuilder withWorkersPerProcess(int workersPerProcess) {
			remoteAddress.workersPerProcess = workersPerProcess;
			return this;
		}
		public RemoteAddress build() {
			return remoteAddress;
		}
	}
	
}
