package com.hp.gaia.mgs.rest.oauth2;

import org.apache.oltu.oauth2.rsfilter.OAuthClient;
import org.apache.oltu.oauth2.rsfilter.OAuthDecision;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.Principal;

/**
 * Created by belozovs on 6/1/2015.
 */
public class OAuth2Decision implements OAuthDecision {

    String token;
    String tenantId;

    public OAuth2Decision(String token) {
        this.token = token;
    }

    @Override
    public boolean isAuthorized() {
        if(token == null || token.isEmpty()){
            System.out.println("Token is emtpy or invalid");
            return false;
        } else {
            if(TokensDB.tokensPerTenant.get(token) == null){
                System.out.println("Token is empty or invalid");
                return false;
            } else {
                this.tenantId = TokensDB.tokensPerTenant.get(token);
            }
        }

        return true;
    }

    @Override
    public Principal getPrincipal() {
        throw new NotImplementedException();
    }

    @Override
    public OAuthClient getOAuthClient() {
        if(tenantId != null){
            return new OAuth2Client(String.valueOf(tenantId));
        } else {
            return null;
        }
    }
}
