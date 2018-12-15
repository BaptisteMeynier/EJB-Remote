package com.javaee7.wildfly.server.stateless;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(RemoteGreeting.class)
public class Greeting implements RemoteGreeting{

	public String classical() {
		return "Hello";
	}

	public String personalGreeting(final String name) {
		return String.format("Hello %s", name);
	}

	public void ignore() {
		// TODO Auto-generated method stub
	}

	
}
