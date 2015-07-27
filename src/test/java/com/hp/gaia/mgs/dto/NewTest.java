package com.hp.gaia.mgs.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by belozovs on 7/27/2015.
 */
public class NewTest {

    private final String issueChange = "{\"event\":\"issue_change\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"uid\":\"1122\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"Status\",\"from\":\"New\",\"to\":\"Open\",\"ttc\":124},{\"name\":\"Priority\",\"to\":\"2-Medium\"}],\"comments\":[{\"topic\":\"re: Problem to delpoy on AWS\",\"text\":\"larin fdsfsdf, fsdfds fsdfsfs\",\"time_since_last_post(h)\":12.5}]}";
    private final String testChange = "{\"time\":\"2015-11-10T23:00:00Z\",\"id\":{\"uid\":\"2341\"},\"event\":\"test_change\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"tags\":{\"workspace\":\"CRM\",\"user\":\"bob\"},\"fields\":[{\"name\":\"State\",\"from\":\"Maintenance\",\"to\":\"Ready\",\"ttc(d)\":11}],\"steps\":[{\"new\":10,\"modified\":3,\"deleted\":1}],\"attachments\":[{\"name\":\"readme.docx\",\"size\":\"1.3M\"}]}";
    private final String codeCommit = "{\"event\":\"code_commit\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"uid\":\"8ad3535acb2a724eb0058fa071c788d48ab6978e\"},\"tags\":{\"user\":\"alex\"},\"files\":[{\"file\":\"README.md\",\"loc\":10},{\"file\":\" src/main/java/managers/RabbitmqManager.java\",\"loc\":-14}]}";
    private final String codeTestrun = "{\"event\":\"code_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"repository\":\"git://github.com/hp/mqm-server\",\"branch\":\"master\"},\"id\":{\"package\":\"com.hp.mqm\",\"class\":\"FilterBuilder\",\"method\":\"TestLogicalOperators\"},\"tags\":{\"build_job\":\"backend_job\",\"browser\":\"firefox\",\"build_label\":\"1.7.0\"},\"result\":{\"status\":\"error\",\"error\":\"NullPointerException: ...\",\"setup_time\":35,\"tear_down_time\":20,\"run_time\":130}}";
    private final String almTestrun = "{\"event\":\"tm_testrun\",\"time\":\"2015-11-10T23:00:00Z\",\"source\":{\"server\":\"http://alm-saas.hp.com\",\"domain\":\"IT\",\"project\":\"Project A\"},\"id\":{\"instance\":\"245\",\"test_id\":\"34\"},\"tags\":{\"workspace\":\"CRM\",\"testset\":\"Regression\",\"user\":\"john\",\"type\":\"Manual\"},\"result\":{\"status\":\"Passed\",\"run_time\":380,\"steps\":[{\"name\":\"step 1\",\"status\":\"Passed\",\"runt_time\":12},{\"name\":\"step 2\",\"status\":\"Skipped\"},{\"name\":\"step 3\",\"status\":\"Passed\",\"runt_time\":368}]}}";

    @Test
    public void testIssueChange() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue("{\"events\": [" + issueChange + "]}", Events.class);

        System.out.println("Number of events: " + events.events.size());
    }

    @Test
    public void testTestChange() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue("{\"events\": [" + testChange + "]}", Events.class);

        System.out.println("Number of events: " + events.events.size());
    }

    @Test
    public void testCodeCommit() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue("{\"events\": [" + codeCommit + "]}", Events.class);

        System.out.println("Number of events: " + events.events.size());
    }

    @Test
    public void testCodeTestrun() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue("{\"events\": [" + codeTestrun + "]}", Events.class);

        System.out.println("Number of events: " + events.events.size());
    }

    @Test
    public void testAlmTestrun() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue("{\"events\": [" + almTestrun + "]}", Events.class);

        System.out.println("Number of events: " + events.events.size());
    }

    @Test
    public void testAllEvents() throws Exception {

        String allEvents = "{\"events\": [" + issueChange + ", " + testChange + "," + codeCommit + "]}";

        ObjectMapper mapper = new ObjectMapper();
        Events events = mapper.readValue(allEvents, Events.class);

        System.out.println("Number of events: " + events.events.size());

        for (BaseEvent event : events.events) {

            InfluxLineProtocolConverter converter = new InfluxLineProtocolConverterFactory().getConverter(event.getType());
            System.out.println(converter.convert(event));

        }

    }
}