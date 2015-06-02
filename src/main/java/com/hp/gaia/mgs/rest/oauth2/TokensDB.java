package com.hp.gaia.mgs.rest.oauth2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 6/1/2015.
 */
public class TokensDB {

    public static Map<String, String> tokensPerTenant = new HashMap<>();

    public String getTokenByTenant(String token){
        return tokensPerTenant.get(token);
    }

    public void addTokenToTenant(String token, String tenant){
        tokensPerTenant.put(token, tenant);
    }

}
