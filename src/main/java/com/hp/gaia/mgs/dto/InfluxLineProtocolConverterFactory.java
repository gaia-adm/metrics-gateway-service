package com.hp.gaia.mgs.dto;

import com.hp.gaia.mgs.dto.issuechange.IssueChangeEvent;
import com.hp.gaia.mgs.dto.issuechange.IssueChangeToInfluxLineProtocol;
import com.hp.gaia.mgs.dto.testrun.*;

/**
 * Created by belozovs on 7/23/2015.
 * Create correct event converter and run it
 */
public class InfluxLineProtocolConverterFactory <T extends AbstractBaseEvent>{

    public String createEventConverterAndRun(T event){
        if(event.getType().equals(IssueChangeEvent.EVENT_TYPE)){
            return new IssueChangeToInfluxLineProtocol().convert((IssueChangeEvent) event);
        }
        if(event.getType().equals(CodeTestRunEvent.EVENT_TYPE)){
            return new TestRunToInfluxLineProtocol().convert((CodeTestRunEvent) event);
        }
        if(event.getType().equals(AlmTestRunEvent.EVENT_TYPE)){
            return new TestRunToInfluxLineProtocol().convert((AlmTestRunEvent) event);
        }
        throw new RuntimeException("Event type not supported: " + event.getType());
    }

    public InfluxLineProtocolConverter getConverter(String type){
        if(type.equals(IssueChangeEvent.EVENT_TYPE)){
            return new IssueChangeToInfluxLineProtocol();
        }
        if(type.equals(CodeTestRunEvent.EVENT_TYPE)){
            return new TestRunToInfluxLineProtocol();
        }
        if (type.equals(AlmTestRunEvent.EVENT_TYPE)){
            return new TestRunToInfluxLineProtocol();
        }
        throw new RuntimeException("Event type not supported: " + type);
    }

}
