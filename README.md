# metrics-gateway-service
Temporary metrics-gateway-service based on jax-rs and jersey with async I/O
This implementation is temporary, can be moved to another platform if we discover it is not robust and scalable enough
This implementation uses spring-security based authz server. Oltu build-in version is available in with_oltu branch

TBD: docker file and service configuration file

How to run: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

Functionality:
- Accept metrics publishing and store it into logs/metrics-storage.log. Only metric name stored to the file.
- Publish API requires authorization token
- Monitor memory and number of threads consumed by this service

Prerequisites:
- Authourization server (https://github.com/gaia-adm/auth-server) must be running in order to allow publishing
- Default authorization server is set to localhot:9001 in default.properties file. This value can be overwritten with -DauthServer parameter

API:
- Publish metrics 
    - URL: /mgs/rest/v1/gateway/publish
    - Method: POST
    - Headers: Content-Type: application/json, Accept: application/json, Authorization: Bearer <oauth2 token>
    - Response code: 201
    - Body: 
    ``` json
    [{
      "metric":"metric-type(test,build,defect,scm)",
      "category":"automatic-test,commit,fork",
      "name":"test-name,job-name,defect-number,sha-of-commit",
      "source":"ci-server,qc-name/project,scm-repository",
      "timestamp":1432191000,
      "tags":["any.string.value.for.further.usage","any.string.value.for.further.usage"],
      "measurements":[{"name":"aut.build","value":968},{"name":"duration","value":350}],
      "events":[{"name":"status","value":"failed"},{"name":"runBy","value":"admin"}]
    }]
    ```

  - Start/Stop monitoring memory and thread count every 1 second
    - URL: /mgs/rest/monitor/action/{action}
    - Method: GET
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 200
    - Parameters:
      - action: String; on - start monitoring, off - stop monitoring
    
Please refer https://github.com/gaia-adm/auth-server in order to obtain token