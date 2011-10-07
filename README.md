ARMI - Asynchronous RMI for Java
=================================

In its very early stages (dev started 10/2/11).  Lacking error handling and full connection life cycle handling.

The interface is dead simple.

**Client Side**

```java
    client = ARMIClient.create(ServerInterface.class);
    client.connect(host, port, new CompletionCallback<Void>() {
			@Override
			public void operationCompleted(Void retVal) {
			    ServerInterface remoteServ = client.getServerMethods();
			    remoteServ.doSomething(param1, param2, ...);
			    remoteServ.doSomethingElse();
			}
	});
```

**Server Side**

```java
    server = ARMIServer.create(ServerImpl, ClientInterface.class);
    server.listen(host, port);
```

Servers can call their clients:

```java
    server.getClientMethods().someClientMethod(params...);
```

Clients can be called back when server methods return by providing a callback as the last parameter:

```java
    ServerInterface serverMethods = client.getServerMethods();
    serverMethods.someMethod(params..., new CompletionCallback<Type>() {
        @Override
        public void operationCompleted(Type retVal) {
            ...
        }
    });
```
    
Clients can register themselves or callbacks with a server to be called:

```java
    client = ARMIClient.create(ServerInterface.class, CallbackInterface.class);
    client.registerClient(new ClientInterfaceImpl());
    client.registerClient(new ClientInterfaceImpl());
    ...
```
    
A crappy console chat is provided in the examples directory as well as a client/server loop that times how long it takes 
to fire a 20K requests and get 20K callbacks.  Currently getting 20K per second on a core-i5.