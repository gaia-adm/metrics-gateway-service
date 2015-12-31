[![](https://badge.imagelayers.io/gaiaadm/mgs:latest.svg)](https://imagelayers.io/?images=gaiaadm/mgs:latest 'Get your own badge on imagelayers.io')

**CircleCI:** [![Circle CI](https://circleci.com/gh/gaia-adm/metrics-gateway-service.svg?style=svg)](https://circleci.com/gh/gaia-adm/metrics-gateway-service)

[![Codacy Badge](https://api.codacy.com/project/badge/grade/ba9b93b4cc764eda8ef2774f6cb3b9ae)](https://www.codacy.com/app/alexei-led/metrics-gateway-service)

# Metrics-gateway-service

How to run locally: build a war and put it on Jetty (tested with Jetty 9.2) or run mnn jetty:run

## Functionality:
- Accept events sent to the system, convert them to InfluxDB line protocol format and sends to RabbitMQ for further usage.
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
  - InfluxDB 0.9 line protocol
  - Single input event can result in multiple records in database. In any case dimension tag is added for querying data easier (as a set of data fields will be different for different dimensions of the same event)
  - NOTE: all tag and field names are prefixed with underscore in order to prevent using InfluxDB reserved keywords. For example, server becomes _server and from becomes _from. Event type, which is a measurement in InfluxDB, is not prefixed. Timestamp goes to "time" field (name generated by InfluxDB and also not prefixed).
- Examples:
  - Input (issue_change):
     ```
     {"event":"issue_change","time":"2015-07-27T23:00:00Z","source":{"server":"http://alm-saas.hp.com","domain":"IT","project":"Project A"},"id":{"uid":"1122"},"tags":{"workspace":"CRM","user":"bob"},"fields":[{"name":"Status","from":"New","to":"Open","ttc":124},{"name":"Priority","to":"2-Medium"}],"comments":[{"topic":"re: Problem to delpoy on AWS","text":"larin fdsfsdf, fsdfds fsdfsfs","time_since_last_post(h)":12.5}]}
     ```
  - Output (issue_change x 3):
     ```
     issue_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=field _uid="1122",_ttc=124,_name="Status",_from="New",_to="Open" 1438038000000000000
     issue_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=field _uid="1122",_name="Priority",_to="2-Medium" 1438038000000000001
     issue_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=comment _uid="1122",_topic="re: Problem to delpoy on AWS",_text="larin fdsfsdf,_ fsdfds fsdfsfs",_time_since_last_post(h)=12.5 1438038000000000002
     ```
  - Input (test_change):
     ```
     {"time":"2015-07-27T23:00:00Z","id":{"uid":"2341"},"event":"test_change","source":{"server":"http://alm-saas.hp.com","domain":"IT","project":"Project A"},"tags":{"workspace":"CRM","user":"bob"},"fields":[{"name":"State","from":"Maintenance","to":"Ready","ttc(d)":11}],"steps":[{"new":10,"modified":3,"deleted":1}],"attachments":[{"name":"readme.docx","size":"1.3M"}]}
     ```
  - Output (test_change x 3):
     ```
     test_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=field _uid="2341",_name="State",_ttc(d)="11",_from="Maintenance",_to="Ready" 1438038000000000003
     test_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=attachment _uid="2341",_size="1.3M",_name="readme.docx" 1438038000000000004
     test_change,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=step _uid="2341",_new=10,_deleted=1,_modified=3 1438038000000000005
     ```
  - Input (code_commit):
     ```
     {"event":"code_commit","time":"2015-11-10T23:00:00Z","source":{"repository":"git://github.com/hp/mqm-server","branch":"master"},"id":{"uid":"8ad3535acb2a724eb0058fa071c788d48ab6978e"},"tags":{"user":"alex"},"files":[{"file":"README.md","loc":10},{"file":" src/main/java/managers/RabbitmqManager.java","loc":-14}]}
     ```
  - Output (code_commit x 2):
     ```
     code_commit,_repository=git://github.com/hp/mqm-server,_branch=master,_dimension=file _uid="8ad3535acb2a724eb0058fa071c788d48ab6978e",_loc=10,_file="README.md" 1438038000000000006
     code_commit,_repository=git://github.com/hp/mqm-server,_branch=master,_dimension=file _uid="8ad3535acb2a724eb0058fa071c788d48ab6978e",_loc=-14,_file="src/main/java/managers/RabbitmqManager.java" 1438038000000000007
     ```
  - Input (code_testrun):
     ```
     {"event":"code_testrun","time":"2015-07-27T23:00:00Z","source":{"repository":"git://github.com/hp/mqm-server","branch":"master"},"id":{"package":"com.hp.mqm","class":"FilterBuilder","method":"TestLogicalOperators"},"tags":{"build_job":"backend_job","browser":"firefox","build_label":"1.7.0"},"result":{"status":"error","error":"NullPointerException: ...","setup_time":35,"tear_down_time":20,"run_time":130}}
     ```
  - Output (code_testrun):
     ```
     code_testrun,_repository=git://github.com/hp/mqm-server,_branch=master,_dimension=result _package="com.hp.mqm",_method="TestLogicalOperators",_class="FilterBuilder",_setup_time=35,_erorString="NullPointerException: ...",_runtTime=130,_status="error",_tear_down_time=20 1438038000000000008
     ```
  - Input (tm_testrun):
     ```
     {"event":"tm_testrun","time":"2015-07-27T23:00:00Z","source":{"server":"http://alm-saas.hp.com","domain":"IT","project":"Project A"},"id":{"instance":"245","test_id":"34"},"tags":{"workspace":"CRM","testset":"Regression","user":"john","type":"Manual"},"result":{"status":"Passed","run_time":380,"steps":[{"name":"step 1","status":"Passed","runt_time":12},{"name":"step 2","status":"Skipped"},{"name":"step 3","status":"Passed","runt_time":368}]}}
     ```
  - Output (tm_testrun x 4):
     ```
     tm_testrun,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=result _instance="245",_test_id="34",_runtTime=380,_status="Passed" 1438038000000000009
     tm_testrun,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=test-step-result _instance="245",_test_id="34",_name="step 1",_status="Passed",_runt_time=12 1438038000000000010
     tm_testrun,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=test-step-result _instance="245",_test_id="34",_name="step 2",_status="Skipped" 1438038000000000011
     tm_testrun,_server=http://alm-saas.hp.com,_domain=IT,_project=Project\ A,_dimension=test-step-result _instance="245",_test_id="34",_name="step 3",_status="Passed",_runt_time=368 1438038000000000012
     ```
  - Input (general):
     ```
     {"event":"general","time":"2015-07-27T23:00:00Z","source":{"origin":"notyourbusiness"},"id":{"uid":"12345"},"tags":{"tag1":"foo","tag2":"boo"},"data":{"field1":"value1","field2":"value2","field3":3}}
     ```
  - Output (general always x1):
     ```
     general,_origin=notyourbusiness,_dimension=general _uid="12345",_field1="value1",_field3=3,_field2="value2" 1438038000000000013
     ```


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
