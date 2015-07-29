package com.hp.gaia.mgs.spring;

import com.hp.gaia.mgs.rest.context.TenantContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

/**
 * Created by belozovs on 6/25/2015.
 * 
 */
public class MultiTenantAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public MultiTenantOAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication auth =  super.extractAuthentication(map);

        MultiTenantOAuth2Authentication mauth = new MultiTenantOAuth2Authentication(auth.getOAuth2Request(), auth.getUserAuthentication());

        mauth.getTenantDetails().put("tenantId", map.get("tenantId"));

        if(map.get("tenantId")!=null){
            TenantContextHolder.getInstance().setTenantIdLocal(String.valueOf(map.get("tenantId")));
        } else {
            TenantContextHolder.getInstance().setTenantIdLocal(null);
        }

        return mauth;
    }
}
