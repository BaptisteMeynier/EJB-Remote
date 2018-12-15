# EJB3 Remote and Wildfly Client
----------------

## Server
You have to provide an interface and an implementation. The implementation has to reference with annotations the interface that will be called by the client. 

###Stateless
Implementation
```
@Stateless
@Remote(RemoteGreeting.class)
public class Greeting implements RemoteGreeting{
```
Remote
```
public interface RemoteGreeting {
```

###Stateful
Implementation
```
@Stateful
@Remote(RemoteCounter.class)
public class Counter implements RemoteCounter{
```
Remote
```
public interface RemoteCounter {
```


## Wildfly Client (for a no wildfly application)
### Maven import
```
<dependency>
	<groupId>org.wildfly</groupId>
	<artifactId>wildfly-ejb-client-bom</artifactId>
	<version>8.2.1.Final</version>
	<type>pom</type>
	<scope>test</scope>
</dependency>
```

### Configuration
You need to create a file which is called  jboss-ejb-client.properties   
The classical file looks like the bellow example:
```
endpoint.name=client-endpoint
remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
 
remote.connections=default
 
remote.connection.default.host=10.20.30.40
remote.connection.default.port = 8080
remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS=false
```

If you are in clustered configuration you can reference each node that you want to request  
```
remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
 
remote.connections=one, two
 
remote.connection.one.host=localhost
remote.connection.one.port=8080
remote.connection.one.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS=false
 
remote.connection.two.host=localhost
remote.connection.two.port=7999
remote.connection.two.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS=false
```


________________________
### link
https://docs.jboss.org/author/display/WFLY8/EJB+invocations+from+a+remote+client+using+JNDI?_sscc=t
