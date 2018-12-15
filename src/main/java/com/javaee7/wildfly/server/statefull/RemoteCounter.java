package com.javaee7.wildfly.server.statefull;

public interface RemoteCounter {
	   void increment();
	   
	    void decrement();
	 
	    int getCount();
}
