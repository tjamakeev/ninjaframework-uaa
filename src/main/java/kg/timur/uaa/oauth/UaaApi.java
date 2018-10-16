package kg.timur.uaa.oauth;


import com.github.scribejava.core.builder.api.DefaultApi20;
import com.google.inject.Inject;

import conf.Constants;
import ninja.utils.NinjaProperties;


public class UaaApi extends DefaultApi20
{
    private final String accessTokenEndpoint;
    private final String authorizationEndpoint;


    @Inject
    public UaaApi( NinjaProperties ninjaProperties )
    {
        this.accessTokenEndpoint = ninjaProperties.get( Constants.OAUTH_TOKEN_ENDPOINT );
        this.authorizationEndpoint = ninjaProperties.get( Constants.OAUTH_AUTHORIZATION_ENDPOINT );
    }


    @Override
    public String getAccessTokenEndpoint()
    {
        return this.accessTokenEndpoint;
    }


    @Override
    protected String getAuthorizationBaseUrl()
    {
        return this.authorizationEndpoint;
    }
}