package com.hp.gaia.mgs.rest.oauth2;

import org.apache.oltu.oauth2.rsfilter.OAuthClient;

/**
 * Created by belozovs on 6/1/2015.
 */
public class OAuth2Client implements OAuthClient {

    String tenantId;

    public OAuth2Client(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getClientId() {
        return tenantId;
    }
}
