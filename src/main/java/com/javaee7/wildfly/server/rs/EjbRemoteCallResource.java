package com.javaee7.wildfly.server.rs;


import com.javaee7.wildfly.server.client.EJBClient;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("call")
public class EjbRemoteCallResource {

    @Inject
    private EJBClient ejbClient;

    @GET
    public Response call(@QueryParam("applicationName") String applicationName) throws NamingException {
        return Response.ok(ejbClient.callStatelessEJB(applicationName)).build();
    }
}
