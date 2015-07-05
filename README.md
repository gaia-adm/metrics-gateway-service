# metrics-gateway-service
Temporary metrics-gateway-service based on jax-rs and jersey with async I/O
This implementation is temporary, can be moved to another platform if we discover it is not robust and scalable enough
This implementation uses spring-security based authz server. Oltu build-in version is available in with_oltu branch

How to run locally: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

Functionality:
- Accept metrics publishing and store it into logs/metrics-storage.log. Only metric name stored to the file.
- Publish API requires authorization token
- Monitor memory and number of threads consumed by this service

Prerequisites:
- Authorization server (https://github.com/gaia-adm/auth-server) must be running in order to allow publishing
- (Optional) RabbitMQ

Customization (-D parameters) - based on default.properties file. For example,
- Authorization server: -DauthServer=name:port. Defalut is localhot:9001
- Output to log file (metrics-storage.log) or to RabbitMQ: -DuseAmqp=true/false. Default is false (print to log file)
- RabbitMQ parameters: host, port, user, password, routing key: -DamqpHost, -DamqpPort, -DamqpUser, -DamqpPassword, -DamqpRoutingKey. Defaults are localhost, 5672, admin, "", events-indexer

API:
- Publish metrics 
    - URL: /mgs/rest/v1/gateway/publish3
    - Method: POST
    - Headers: Authorization: Bearer <oauth2 token>
    - Response code: 201
    - Body: my_metric,host=server02,region=us-east value=0.97

- Start/Stop monitoring memory and thread count every 1 second
    - URL: /mgs/rest/monitor/action/{action}
    - Method: GET
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 200
    - Parameters:
      - action: String; on - start monitoring, off - stop monitoring
    
Please refer https://github.com/gaia-adm/security-token-service in order to obtain token