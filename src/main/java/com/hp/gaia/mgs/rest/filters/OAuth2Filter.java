package com.hp.gaia.mgs.rest.filters;

import com.hp.gaia.mgs.rest.oauth2.OAuth2RSProvider;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.apache.oltu.oauth2.rsfilter.OAuthClient;
import org.apache.oltu.oauth2.rsfilter.OAuthDecision;
import org.apache.oltu.oauth2.rsfilter.OAuthRSProvider;
import org.apache.oltu.oauth2.rsfilter.OAuthUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

/**
 * Created by belozovs on 6/1/2015.
 */
public class OAuth2Filter implements Filter {

    public static final String OAUTH_RS_PROVIDER_CLASS = "oauth.rs.provider-class";

    public static final String RS_REALM = "oauth.rs.realm";
    public static final String RS_REALM_DEFAULT = "OAuth Protected Service";

    public static final String RS_TOKENS = "oauth.rs.tokens";
    public static final ParameterStyle RS_TOKENS_DEFAULT = ParameterStyle.HEADER;

    private static final String TOKEN_DELIMITER = ",";

    private String realm;

    private OAuthRSProvider provider;



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

/*        provider = OAuthUtils.initiateServletContext(filterConfig.getServletContext(), OAUTH_RS_PROVIDER_CLASS,
                OAuthRSProvider.class);*/

        provider = new OAuth2RSProvider();
        realm = filterConfig.getServletContext().getInitParameter(RS_REALM);
        if (OAuthUtils.isEmpty(realm)) {
            realm = RS_REALM_DEFAULT;
        }

/*        String parameterStylesString = filterConfig.getServletContext().getInitParameter(RS_TOKENS);
        if (OAuthUtils.isEmpty(parameterStylesString)) {
            parameterStyles = new ParameterStyle[] {RS_TOKENS_DEFAULT};
        } else {
            String[] parameters = parameterStylesString.split(TOKEN_DELIMITER);
            if (parameters != null && parameters.length > 0) {
                parameterStyles = new ParameterStyle[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    ParameterStyle tempParameterStyle = ParameterStyle.valueOf(parameters[i]);
                    if (tempParameterStyle != null) {
                        parameterStyles[i] = tempParameterStyle;
                    } else {
                        throw new ServletException("Incorrect ParameterStyle: " + parameters[i]);
                    }
                }
            }
        }*/

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        try {

            // Make an OAuth Request out of this servlet request
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(req,ParameterStyle.HEADER);

            // Get the access token
            String accessToken = oauthRequest.getAccessToken();

            final OAuthDecision decision = provider.validateRequest(realm, accessToken, req);

            if(decision.isAuthorized()) {
                request.setAttribute(OAuth.OAUTH_CLIENT_ID, decision.getOAuthClient().getClientId());
                chain.doFilter(request, response);
                return;
            } else {
                throw OAuthProblemException.error("Not authorized");
            }

        } catch (OAuthSystemException e1) {
            throw new ServletException(e1);
        } catch (OAuthProblemException e) {
            respondWithError(res, e);
            return;
        }

    }


    @Override
    public void destroy() {

    }

    private void respondWithError(HttpServletResponse resp, OAuthProblemException error)
            throws IOException, ServletException {

        OAuthResponse oauthResponse = null;

        try {
            if (OAuthUtils.isEmpty(error.getError())) {
                oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm(realm)
                        .buildHeaderMessage();

            } else {

                int responseCode = 401;
                if (error.getError().equals(OAuthError.CodeResponse.INVALID_REQUEST)) {
                    responseCode = 400;
                } else if (error.getError().equals(OAuthError.ResourceResponse.INSUFFICIENT_SCOPE)) {
                    responseCode = 403;
                }

                oauthResponse = OAuthRSResponse
                        .errorResponse(responseCode)
                        .setRealm(realm)
                        .setError(error.getError())
                        .setErrorDescription(error.getDescription())
                        .setErrorUri(error.getUri())
                        .buildHeaderMessage();
            }
            resp.addHeader(OAuth.HeaderType.WWW_AUTHENTICATE,
                    oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
            resp.sendError(oauthResponse.getResponseStatus());
        } catch (OAuthSystemException e) {
            throw new ServletException(e);
        }
    }


}
