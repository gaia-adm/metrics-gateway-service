package com.hp.gaia.mgs.rest.oauth2;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.rsfilter.OAuthDecision;
import org.apache.oltu.oauth2.rsfilter.OAuthRSProvider;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by belozovs on 6/1/2015.
 */
public class OAuth2RSProvider implements OAuthRSProvider {


    @Override
    public OAuthDecision validateRequest(String rsId, String token, HttpServletRequest req) throws OAuthProblemException {

        return new OAuth2Decision(token);

    }



}
