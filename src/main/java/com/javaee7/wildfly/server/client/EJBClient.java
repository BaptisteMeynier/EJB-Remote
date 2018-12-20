package com.javaee7.wildfly.server.client;

import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;

import javax.inject.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Singleton
public class EJBClient {

    public String callStatelessEJB(final String applicationName) throws NamingException {
        Properties properties = new Properties();
        properties.put( Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming" );
        properties.put( "org.jboss.ejb.client.scoped.context", "true" );
        properties.put( "remote.connections", "one, two" );

        properties.put( "remote.connection.one.host","localhost" );
        properties.put( "remote.connection.one.port","8080" );

        properties.put( "remote.connection.two.host","localhost" );
        properties.put( "remote.connection.two.port","7979" );

        final Context context = new InitialContext(properties);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi(applicationName));
        return greeting.classical();
    }


    private static String createStatelessJndi(final String applicationName){
        final String appName = "";
        final String moduleName = applicationName;
        final String distinctName = "";
        final String beanName = Greeting.class.getSimpleName();
        final String viewClassName = RemoteGreeting.class.getName();
        return "ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName;
    }

}
