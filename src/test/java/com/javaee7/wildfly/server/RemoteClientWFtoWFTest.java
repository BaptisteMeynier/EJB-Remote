package com.javaee7.wildfly.server;

import com.javaee7.wildfly.server.client.EJBClient;
import com.javaee7.wildfly.server.statefull.Counter;
import com.javaee7.wildfly.server.statefull.RemoteCounter;
import com.javaee7.wildfly.server.stateless.Greeting;
import com.javaee7.wildfly.server.stateless.RemoteGreeting;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * https://github.com/arquillian/arquillian-showcase/blob/master/multinode/pom.xml
 */
@RunWith(Arquillian.class)
public class RemoteClientWFtoWFTest {

    private static String APPLICATION_NAME = "myApplication";

    @Inject
    @TargetsContainer("widlfly-managed-1")
    private EJBClient ejbClient;

    @Deployment(name = "wildfly1")
    @TargetsContainer("widlfly-managed-1")
    public static WebArchive createDeployment1() {
        return ShrinkWrap.create(WebArchive.class, String.format("%s.war", APPLICATION_NAME + "_1"))
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Deployment(name = "wildfly2")
    @TargetsContainer("widlfly-managed-2")
    public static WebArchive createDeployment2() {
        return ShrinkWrap.create(WebArchive.class, String.format("%s.war", APPLICATION_NAME + "_2"))
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testEjbCall() throws NamingException {
        ejbClient.callStatelessEJB(APPLICATION_NAME);
    }
   /* @Test
    @RunAsClient
    public void testRunningInDep1(@ArquillianResource URL baseURL) throws URISyntaxException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(baseURL.toURI());
        webTarget.queryParam("applicationName", APPLICATION_NAME + "_2").request().get();
    }*/


}
