package com.javaee7.wildfly.server;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.javaee7.wildfly.server.statefull.Counter;
import com.javaee7.wildfly.server.statefull.RemoteCounter;
import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;


@RunWith(Arquillian.class)
public class RemoteClientTest {

	private static String APPLICATION_NAME="myApplication";

    @Deployment(name = "wildfly1")
    @TargetsContainer("widlfly-managed-1")
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class,String.format("%s.war", APPLICATION_NAME))
            .addClasses(Counter.class,RemoteCounter.class)
            .addClasses(Greeting.class,RemoteGreeting.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
}
	@Test
    @RunAsClient
	public void should_call_stateless_ejb() throws NamingException {
		RemoteGreeting greeting = lookupRemoteStateless();
		Assert.assertEquals("Hello", greeting.classical());
		Assert.assertEquals("Hello Bernard", greeting.personalGreeting("Bernard"));
		greeting.ignore();
	}
	
	@Test
    @RunAsClient
	public void should_call_statefull_ejb() throws NamingException {
		RemoteCounter counter = lookupRemoteStateful();
		Assert.assertEquals(0,counter.getCount());
		counter.decrement();
		Assert.assertEquals(-1, counter.getCount());
		counter.increment();
		counter.increment();
		Assert.assertEquals(1, counter.getCount());
	}

    @Test
    @RunAsClient
    public void should_call_stateless_ejb_without_file() throws NamingException {
	    Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        //properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        //properties.put( "remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false" );
        //properties.put( "remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false" );
        properties.put( "remote.connections", "default" );
        properties.put( "remote.connection.default.host", "localhost" );
        properties.put( "remote.connection.default.port","7979");
        final Context context = new InitialContext(properties);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi());
        Assert.assertEquals("Hello", greeting.classical());
    }

    @Test
    @RunAsClient
    public void should_call_stateless_ejb_without_file_with_multiple_target() throws NamingException {
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        properties.put( "remote.connections", "one, two" );

        properties.put( "remote.connection.one.host","localhost" );
        properties.put( "remote.connection.one.port","7979" );

        properties.put( "remote.connection.two.host","localhost" );
        properties.put( "remote.connection.two.port","8080" );

        final Context context = new InitialContext(properties);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi());
        Assert.assertEquals("Hello", greeting.classical());
    }


    /**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteGreeting lookupRemoteStateless() throws NamingException {
        final Hashtable<String,String> jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);

        return (RemoteGreeting) context.lookup(createStatelessJndi());
    }

    private static String createStatelessJndi(){
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = APPLICATION_NAME;
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = Greeting.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = RemoteGreeting.class.getName();
        // let's do the lookup
        final String jndi = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName;
        return jndi;
    }

    /**
     * Looks up and returns the proxy to remote stateful counter bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCounter lookupRemoteStateful() throws NamingException {
        final Hashtable<String,String> jndiProperties = new Hashtable<String, String>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = APPLICATION_NAME;
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = Counter.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = RemoteCounter.class.getName();
        // let's do the lookup (notice the ?stateful string as the last part of the jndi name for stateful bean lookup)
        String jndi = "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName + "?stateful";
        return (RemoteCounter) context.lookup(jndi);
    }


	
}
