package com.javaee7.wildfly.server.rs;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("ejb")
public class EjbRemoteApplication extends Application {


    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }
}
