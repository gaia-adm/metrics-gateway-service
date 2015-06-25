package com.hp.gaia.mgs.spring;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by belozovs on 6/25/2015.
 */
public class MultiTenantOAuth2Authentication extends OAuth2Authentication {

    private Map<String, Object> tenantDetails = new HashMap<>();

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
     * authentication may be null.
     *
     * @param storedRequest      The authorization request (must not be null).
     * @param userAuthentication The user authentication (possibly null).
     */
    public MultiTenantOAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication) {
        super(storedRequest, userAuthentication);
    }

    public Map<String, Object> getTenantDetails() {
        return tenantDetails;
    }
}
