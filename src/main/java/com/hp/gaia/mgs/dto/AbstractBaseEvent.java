package com.hp.gaia.mgs.dto;

import java.util.Map;

/**
 * Created by belozovs on 7/19/2015.
 */
public abstract class AbstractBaseEvent extends BaseEvent{

    public abstract Map<String, Object> getValues();

}
