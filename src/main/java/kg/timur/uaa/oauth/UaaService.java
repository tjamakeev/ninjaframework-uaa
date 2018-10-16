package kg.timur.uaa.oauth;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.Inject;

import conf.Constants;
import lombok.extern.slf4j.Slf4j;
import ninja.utils.NinjaProperties;


@Slf4j
public class UaaService
{
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private OAuth20Service auth20Service;

    @Inject
    private NinjaProperties ninjaProperties;


    public UaaProfile getProfile( String accessToken )
    {
        try
        {
            final OAuthRequest request =
                    new OAuthRequest( Verb.GET, ninjaProperties.get( Constants.OAUTH_SERVER_URL ) + "/userinfo" );

            auth20Service.signRequest( accessToken, request );

            final Response response = auth20Service.execute( request );

            if ( response.isSuccessful() )
            {
                return objectMapper.readValue( response.getBody(), UaaProfile.class );
            }
            else
            {
                throw new OAuthException( response.getBody() );
            }
        }
        catch ( IOException | InterruptedException | ExecutionException e )
        {
            throw new OAuthException( "Could not retrieve user profile." );
        }
    }


    public OAuth2AccessToken refreshAccessToken( final String accessToken )
    {
        try
        {
            return auth20Service.refreshAccessToken( accessToken );
        }
        catch ( IOException | InterruptedException | ExecutionException e )
        {
            throw new OAuthException( "Could not refresh OAuth access token." );
        }
    }


    public OAuth2AccessToken getAccessToken( final String authorizationCode )
    {
        try
        {
            return auth20Service.getAccessToken( authorizationCode );
        }
        catch ( IOException | InterruptedException | ExecutionException e )
        {
            throw new OAuthException( "Could not OAuth get access token." );
        }
    }
}
