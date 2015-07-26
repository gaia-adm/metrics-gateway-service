package com.hp.gaia.mgs.dto;

import com.hp.gaia.mgs.dto.change.IssueChangeEvent;
import com.hp.gaia.mgs.dto.change.IssueChangeToInfluxLineProtocol;
import com.hp.gaia.mgs.dto.commit.CodeCommitEvent;
import com.hp.gaia.mgs.dto.commit.CodeCommitToInfluxLineProtocol;
import com.hp.gaia.mgs.dto.testrun.*;

/**
 * Created by belozovs on 7/23/2015.
 * Create correct event converter and run it
 */
public class InfluxLineProtocolConverterFactory {

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
        if (type.equals(CodeCommitEvent.EVENT_TYPE)){
            return new CodeCommitToInfluxLineProtocol();
        }
        throw new RuntimeException("Event type not supported: " + type);
    }

}
