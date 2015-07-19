package com.hp.gaia.mgs.dto;

/**
 * Created by belozovs on 7/19/2015.
 */
public class EventsConverter {

    public static <T extends BaseEvent> T convert(Object from, Class<? extends BaseEvent> to){

        if(from == null){
            throw new RuntimeException("from object is null");
        }

        if(to.isAssignableFrom(from.getClass())){
            return (T) to.cast(from);
        } else {
            throw new RuntimeException("Cannot convert " + from.getClass().getName() + "to " + to.getName());
        }

    }

}
