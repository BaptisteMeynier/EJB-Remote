package com.javaee7.wildfly.server.client;

import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;

import javax.inject.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

@Singleton
public class EJBClient {

    public String callStatelessEJB(final String applicationName, Properties remoteDeclaration) throws NamingException {
        final Context context = new InitialContext(remoteDeclaration);
        RemoteGreeting greeting = (RemoteGreeting) context.lookup(createStatelessJndi(applicationName));
        return greeting.classical();
    }

    public String callStatelessEJB(final String applicationName) throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
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
