ARMI - A-synchronous RMI for Java
=================================

The interface is dead simple.

**Client Side***

    client = ARMIClient.create(ServerInterface.class);
    client.connect(host, port, new CompletionCallback<Void>() {
			@Override
			public void operationCompleted(Void retVal) {
				client.getServerMethods().someServerMethod(param1, param2, ...);
			}
	});

**Server Side**

    server = ARMIServer.create(ServerImpl, ClientInterface.class);
    server.listen(host, port);

Servers can call their clients:

    server.getClientMethods().someClientMethod(params...);

Clients can be called back when server methods return:

    ServerInterface serverMethods = client.getServerMethods();
    serverMethods.someMethod(params..., new CompletionCallback<Type>() {
        @Override
        public void operationCompleted(Type retVal) {
            ...
        }
    });
    
Clients can register themselves or callbacks with a server to be called:

    client = ARMIClient.create(ServerInterface.class, CallbackInterface.class);
    client.registerClient(new ClientInterfaceImpl());
    client.registerClient(new ClientInterfaceImpl());
    ...
    
A crappy console chat is provided in the examples directory as well as a client/server loop that times how long it takes 
to fire a 20K requests and get 20K callbacks.  Currently getting 20K per second on a core-i5.