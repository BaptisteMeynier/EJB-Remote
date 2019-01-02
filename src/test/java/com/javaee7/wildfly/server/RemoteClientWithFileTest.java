package com.javaee7.wildfly.server;

import com.javaee7.wildfly.server.statefull.Counter;
import com.javaee7.wildfly.server.statefull.RemoteCounter;
import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.naming.NamingException;
import java.nio.file.Path;

import static com.javaee7.wildfly.server.client.utils.JndiConfiguration.lookupRemoteStateful;
import static com.javaee7.wildfly.server.client.utils.JndiConfiguration.lookupRemoteStateless;


@RunWith(Arquillian.class)
public class RemoteClientWithFileTest {

    private static String APPLICATION_NAME = "myApplication";


    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, String.format("%s.war", APPLICATION_NAME))
                .addClasses(Counter.class, RemoteCounter.class)
                .addClasses(Greeting.class, RemoteGreeting.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    @Test
    @RunAsClient
    public void should_call_stateless_ejb() throws NamingException {
        RemoteGreeting greeting = lookupRemoteStateless(APPLICATION_NAME);
        Assert.assertEquals("Hello", greeting.classical());
        Assert.assertEquals("Hello Bernard", greeting.personalGreeting("Bernard"));
        greeting.ignore();
    }

    @Test
    @RunAsClient
    public void should_call_statefull_ejb() throws NamingException {
        RemoteCounter counter = lookupRemoteStateful(APPLICATION_NAME);
        Assert.assertEquals(0, counter.getCount());
        counter.decrement();
        Assert.assertEquals(-1, counter.getCount());
        counter.increment();
        counter.increment();
        Assert.assertEquals(1, counter.getCount());
    }


}
