package com.javaee7.wildfly.server.statefull;

import javax.ejb.Remote;
import javax.ejb.Stateful;


@Stateful
@Remote(RemoteCounter.class)
public class Counter implements RemoteCounter{
    private int count = 0;
    
    public void increment() {
        this.count++;
    }
 
    public void decrement() {
        this.count--;
    }
 
    public int getCount() {
        return this.count;
    }
}
