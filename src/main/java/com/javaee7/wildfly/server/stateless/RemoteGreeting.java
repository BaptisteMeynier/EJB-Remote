package com.javaee7.wildfly.server.stateless;

public interface RemoteGreeting {

	String classical();
	
	String personalGreeting(final String name);
	
	void ignore();
	
}
