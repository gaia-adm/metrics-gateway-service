package com.hp.gaia.mgs.services;

import com.hp.gaia.mgs.amqp.AmqpManager;
import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.change.IssueChangeToInfluxLineProtocol;
import com.rabbitmq.client.Channel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by belozovs on 7/19/2015.
 */
public class MetricsCollectorServiceTest {

    AmqpManager amqpManager = mock(AmqpManager.class);
    Channel channel = mock(Channel.class);
    private MetricsCollectorService mcs;


    @Before
    public void setUp() throws Exception {
        mcs = new MetricsCollectorService();

        mcs.amqpManager = amqpManager;

    }

    @Test
    public void testEscapedString() throws Exception {
        assertEquals("my\\ do\\\"uble\\ quoted\\ string\\ part\\ 1\\,\\ part\\ 2", new IssueChangeToInfluxLineProtocol().getEscapedString("my do\"uble quoted string part 1, part 2"));
    }

    @Test
    public void testQuoteString() throws Exception {
        assertEquals("\"str\"", new IssueChangeToInfluxLineProtocol().getQuotedValue("str"));
    }

    @Test
    public void testPublishEvent() throws Exception {

        List<IssueChangeEvent> eventList = new ArrayList<>();
        eventList.add(createIssueChangeEvent("aaa"));
        eventList.add(createIssueChangeEvent("bbb"));

        when(amqpManager.getChannel()).thenReturn(channel);
        //void method do nothing by default - keep it here just for the reference
        // doNothing().when(channel).basicPublish(anyString(), anyString(), anyObject(), anyObject());

        mcs.publishEvent(eventList, "1");

    }

    private IssueChangeEvent createIssueChangeEvent(String prefix) {

        IssueChangeEvent ice = spy(IssueChangeEvent.class);
/*
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(prefix+"_numeric", 25);
        valuesMap.put(prefix + "_string", "25");
        when(ice.getValues()).thenReturn(valuesMap);
*/
        ice.setTime(new Date());
        ice.setType(IssueChangeEvent.EVENT_TYPE);
        ice.setId(Collections.singletonMap("uuid", prefix));
        ice.setSource(Collections.singletonMap("project", prefix));
        ice.setTags(Collections.singletonMap("server", prefix));

        return ice;
    }

}