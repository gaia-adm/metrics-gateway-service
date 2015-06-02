package com.hp.gaia.mgs.security;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * Created by belozovs on 6/1/2015.
 */
public class TokenTest {

    public static void main(String[] args) throws OAuthSystemException {

        OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        System.out.println(oauthIssuerImpl.accessToken());
        System.out.println(oauthIssuerImpl.authorizationCode());
        System.out.println(oauthIssuerImpl.refreshToken());



    }

}
