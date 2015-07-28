CircleCI build status: [![Circle CI](https://circleci.com/gh/gaia-adm/metrics-gateway-service.svg?style=svg)](https://circleci.com/gh/gaia-adm/metrics-gateway-service)
# metrics-gateway-service

How to run locally: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

Functionality:
- Accept events sent to the system, convert them to InfluxDB line protocol format and sends to RabbitMQ for further usage.
- Supports bulk input (request body size up to 100KB)
- Supports only predefined data types s described [here] (https://github.com/gaia-adm/api-data-format)
- General data type is defined for testing purpose as described [here] (https://github.com/gaia-adm/api-data-format)
- Requires authorization token when sending events
- Monitor memory and number of threads consumed by this service (on demand)
- For testing purpose: can work without RabbitMQ with -DuseAmqp=false. In this case output is just printed to stdout

Prerequisites:
- Authorization server (https://github.com/gaia-adm/auth-server) must be running in order to allow publishing
- (Optional) RabbitMQ

Customization (-D parameters) - based on default.properties file. For example,
- Authorization server: -DauthServer=name:port. Defalut is localhot:9001
- Output to log file (metrics-storage.log) or to RabbitMQ: -DuseAmqp=true/false. Default is false (print to log file)
- RabbitMQ parameters: host, port, user, password, routing key: -DamqpHost, -DamqpPort, -DamqpUser, -DamqpPassword, -DamqpRoutingKey. Defaults are localhost, 5672, admin, "", events-indexer

API:
- Publish metrics 
    - URL: /mgs/rest/v1/gateway/event
    - Method: POST
    - Headers:
        - Content-Type: application/json
        - Accept: Bearer application/json
        - Authorization: Bearer <oauth2 token>
    - Response code: 201
    - **JSON Array** of events as described [here] (https://github.com/gaia-adm/api-data-format)

- Start/Stop monitoring memory and thread count every 1 second
    - URL: /mgs/rest/monitor/action/{action}
    - Method: GET
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 200
    - Parameters:
      - action: String; on - start monitoring, off - stop monitoring

    NOTES:
    - only predefined types supported
    - no "common part" - each event includes everything
    - timestamp uniqueness: microseconds keep instanceId, nanoseconds keep running number unique per instance
    - separate DB record created for each map in list of maps (fields, steps, comments, attachments, etc.). For each record datatype tag is added to easier distinguishing between data of fields, comments, etc.

    To add new event:
    - add DTO extending BaseEvent directly or indirectly. Note EVENT_TYPE field must be set in constructor, not later via setter
    - add deserializer
    - set deserializer annotation in DTO
    - add/update xxxToInfluxProtocol class
    - register type in BaseEvent (add subtype annotation)
    - register type in InfluxLineProtocolConverterFactory



Please refer https://github.com/gaia-adm/security-token-service in order to obtain token
