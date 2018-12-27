package com.javaee7.wildfly.server;

import com.javaee7.wildfly.server.client.EJBClient;
import com.javaee7.wildfly.server.statefull.Counter;
import com.javaee7.wildfly.server.statefull.RemoteCounter;
import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * https://github.com/arquillian/arquillian-showcase/blob/master/multinode/pom.xml
 */
@RunWith(Arquillian.class)
public class WildflyToWildflyIT {

    private static String APPLICATION_NAME = "myApplication";

    @Deployment(name = "wildfly1")
    @TargetsContainer("widlfly-managed-1")
    public static WebArchive createDeployment1() {
        return ShrinkWrap.create(WebArchive.class, String.format("%s.war", APPLICATION_NAME))
                .addClasses(Counter.class,RemoteCounter.class)
                .addClasses(Greeting.class,RemoteGreeting.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Deployment(name = "wildfly2")
    @TargetsContainer("widlfly-managed-2")
    public static WebArchive createDeployment2() {
        return ShrinkWrap.create(WebArchive.class, String.format("%s.war", APPLICATION_NAME + "-2"))
                .addClasses(EJBClient.class)
                .addClasses(Counter.class,RemoteCounter.class)
                .addClasses(Greeting.class,RemoteGreeting.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @OperateOnDeployment("wildfly2")
    public void testEjbCall() throws NamingException {
        EJBClient ejbClient = new EJBClient();
        Assert.assertEquals("Hello",ejbClient.callStatelessEJB(APPLICATION_NAME));
    }


    @Test
    @OperateOnDeployment("wildfly2")
    public void testEjbCallToOtherLocalServer() throws NamingException {
        EJBClient ejbClient = new EJBClient();
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        properties.put( "remote.connections", "one");

        properties.put( "remote.connection.one.host","localhost" );
        properties.put( "remote.connection.one.port","7979" );

        Assert.assertEquals("Hello",ejbClient.callStatelessEJB(APPLICATION_NAME,properties));
    }

    @Test
    @OperateOnDeployment("wildfly2")
    public void testEjbCallToSameLocalServer() throws NamingException {
        EJBClient ejbClient = new EJBClient();
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        properties.put( "remote.connections", "two");

        properties.put( "remote.connection.two.host","localhost" );
        properties.put( "remote.connection.two.port","8080" );

        Assert.assertEquals("Hello",ejbClient.callStatelessEJB(APPLICATION_NAME,properties));
    }


    @Test
    @OperateOnDeployment("wildfly2")
    public void testEjbCallToServer() throws NamingException {
        EJBClient ejbClient = new EJBClient();
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );

        Assert.assertEquals("Hello",ejbClient.callStatelessEJB(APPLICATION_NAME,properties));
    }

}
