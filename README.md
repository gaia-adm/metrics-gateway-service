[![Circle CI](https://circleci.com/gh/gaia-adm/metrics-gateway-service.svg?style=svg)](https://circleci.com/gh/gaia-adm/metrics-gateway-service) [![Codacy Badge](https://api.codacy.com/project/badge/grade/ba9b93b4cc764eda8ef2774f6cb3b9ae)](https://www.codacy.com/app/alexei-led/metrics-gateway-service) [![](https://badge.imagelayers.io/gaiaadm/mgs:latest.svg)](https://imagelayers.io/?images=gaiaadm/mgs:latest 'Get your own badge on imagelayers.io')

# Metrics-gateway-service

How to run locally: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

To build the project behind corporate proxy run the following command:

```
docker build -t local/mgs --build-arg PROXY_HOST=web-proxy.israel.hp.com --build-arg PROXY_PORT=8080 -f Dockerfile.build .
```

## Functionality:
- Accept events sent to the system and sends to RabbitMQ for further usage.
- Supports bulk input (request body size up to 100KB)
- Supports only predefined data types s described [here] (https://github.com/gaia-adm/api-data-format)
- General data type is defined for testing purpose as described [here] (https://github.com/gaia-adm/api-data-format)
- Requires authorization token when sending events
- Monitor memory and number of threads consumed by this service (on demand)
- For testing purpose: can work without RabbitMQ with -DuseAmqp=false. In this case output is just printed to stdout

## Prerequisites:
- Authorization server (https://github.com/gaia-adm/auth-server) must be running in order to allow publishing
- RabbitMQ for publishing data

## Customization
- Based on default.properties file, can be reset with -D parameters.
- Authorization server: -DauthServer=name:port. Defalut is localhot:9001
- Output to log file (metrics-storage.log) or to RabbitMQ: -DuseAmqp=true/false. Default is false (print to log file)
- RabbitMQ parameters: host, port, user, password, routing key: -DamqpHost, -DamqpPort, -DamqpUser, -DamqpPassword, -DamqpRoutingKey. Defaults are localhost, 5672, admin, "", events-indexer

## Data format
- Input - see [gaia-adm/api-data-format](https://github.com/gaia-adm/api-data-format)
- Output:
  - Single input event can result in multiple records in database. In any case dimension tag is added for querying data easier (as a set of data fields will be different for different dimensions of the same event)

## API:
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


## Adding new event type:
  - add DTO extending BaseEvent directly or indirectly. Note EVENT_TYPE field must be unique and set in constructor
  - add deserializer
  - set deserializer annotation in DTO
  - add/update xxxToInfluxProtocol class
  - register type in BaseEvent (add subtype annotation)
  - register type in InfluxLineProtocolConverterFactory
  - add xxxEventParserTest in the relevant package
  - add event to MixedEventsListParserTest



Please refer https://github.com/gaia-adm/security-token-service in order to obtain token
