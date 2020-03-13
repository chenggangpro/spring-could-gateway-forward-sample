# spring-could-gateway-forward-sample

## Project Purpose 
This Sample is aim to get websocket address from another server and dynamic forward websocket connection. 

### Project Describe

|Application Name|Purpose|
|:--:|:---|
|websocket-service-1|First websocket service |
|websocket-service-2|Second websocket service |
|address-lookup-service|To get dynamic connection info|
|forward-gateway|To dynamic forward websocket connection|

### Run Step

* 1. Run websocket-service-1
* 2. Run websocket-service-2
* 3. Run address-lookup-service
* 4. Run forward-gateway
* 5. Connect websocket with `ws://127.0.0.0:9011/websocket/type1`,Then Connect Successful
* 6. Send some message,you'll receive an echo response
* 7. Change `type1` to `type2` ,you'll connect to the websocket-service-2