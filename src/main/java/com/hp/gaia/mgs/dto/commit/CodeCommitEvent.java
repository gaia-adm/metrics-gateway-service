package com.hp.gaia.mgs.dto.commit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hp.gaia.mgs.dto.BaseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by belozovs on 7/24/2015.
 */
@JsonDeserialize(using = CodeCommitDeserializer.class)
public class CodeCommitEvent extends BaseEvent {

    public final static String EVENT_TYPE = "code_commit";
    List<Map<String,Object>> changedFilesList;

    public CodeCommitEvent() {
        this.setType(EVENT_TYPE);
        this.changedFilesList = new ArrayList<>();
    }

    public List<Map<String, Object>> getChangedFilesList() {
        return changedFilesList;
    }

    public void setChangedFilesList(List<Map<String, Object>> changedFilesList) {
        this.changedFilesList = changedFilesList;
    }

    public void addChangedFile(Map<String, Object> changeFileData){
        changedFilesList.add(changeFileData);
    }


}
