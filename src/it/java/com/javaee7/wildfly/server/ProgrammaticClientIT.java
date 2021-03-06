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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

import static com.javaee7.wildfly.server.client.utils.JndiConfiguration.createStatelessJndi;


@RunWith(Arquillian.class)
public class ProgrammaticClientIT {

	private static String APPLICATION_NAME="myApplication";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class,String.format("%s.war", APPLICATION_NAME))
            .addClasses(Counter.class,RemoteCounter.class)
            .addClasses(Greeting.class,RemoteGreeting.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
}

    @Test
    @RunAsClient
    public void should_call_stateless_ejb() throws NamingException {
	    Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        //properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        //properties.put( "remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false" );
        //properties.put( "remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false" );
        properties.put( "remote.connections", "default" );
        properties.put( "remote.connection.default.host", "localhost" );
        properties.put( "remote.connection.default.port","7979");
        final Context context = new InitialContext(properties);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi(APPLICATION_NAME));
        Assert.assertEquals("Hello", greeting.classical());
    }

    @Test
    @RunAsClient
    public void should_call_stateless_ejb_with_multiple_target() throws NamingException {
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        properties.put( "remote.connections", "one, two" );

        properties.put( "remote.connection.one.host","localhost" );
        properties.put( "remote.connection.one.port","7979" );

        properties.put( "remote.connection.two.host","localhost" );
        properties.put( "remote.connection.two.port","8080" );

        final Context context = new InitialContext(properties);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi(APPLICATION_NAME));
        Assert.assertEquals("Hello", greeting.classical());
    }

}
