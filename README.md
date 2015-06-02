# metrics-gateway-service
Temporary metrics-gateway-service based on jax-rs and jersey with async I/O
This implementation is temporary, can be moved to another platform if we discover it is not robust and scalable enough.

TBD: docker file and service configuration file

How to run: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

Functionality:
- Accept metrics publishing and store it into logs/metrics-storage.log. Only tenant id and metric name stored to the file.
- Set OAuth2 token for the provided tenant. Multiple tokens can be added.
- Get OAuth2 token(s) for the provided tenant.
- Monitor memory and number of threads consumed by this service

API:
- Publish metrics 
    - URL: /mgs/rest/v1/gateway/publish
    - Method: POST
    - Headers: Content-Type: application/json, Accept: application/json, Authorization: Bearer <oauth2 token>
    - Response code: 201
    - Body: [{"metric":"metric-type(test,build,defect,scm)","category":"automatic-test,commit,fork","name":"test-name,job-name,defect-number,sha-of-commit","source":"ci-server,qc-name/project,scm-repository","timestamp":1432191000,"tags":["any.string.value.for.further.usage","any.string.value.for.further.usage"],"measurements":[{"name":"aut.build","value":968},{"name":"duration","value":350}],"events":[{"name":"status","value":"failed"},{"name":"runBy","value":"admin"}]}]
    
- Create oauth2 token for tenant
    - URL: /mgs/rest/auth/set/{tenantId}
    - Method: POST
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 201
    - Parameters:
      - tenantId: String
      
  - Get oauth2 tokens for tenant
    - URL: /mgs/rest/auth/get/{tenantId}
    - Method: GET
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 200
    - Parameters:
      - tenantId: String
      
  - Start/Stop monitoring memory and thread count every 1 second
    - URL: /mgs/rest/monitor/action/{action}
    - Method: GET
    - Headers: Content-Type: application/json, Accept: application/json
    - Response code: 200
    - Parameters:
      - action: String; on - start monitoring, off - stop monitoring
    
