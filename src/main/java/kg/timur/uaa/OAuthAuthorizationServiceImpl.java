package kg.timur.uaa;


import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.inject.Inject;

import conf.Constants;
import kg.timur.uaa.oauth.OAuthAuthorizationService;
import kg.timur.uaa.oauth.UaaProfile;
import kg.timur.uaa.oauth.UaaService;
import lombok.extern.slf4j.Slf4j;
import ninja.Context;


/**
 * lookup the OAuth profile and process it
 *
 * @author Timur Zhamakeev
 */
@Slf4j
public class OAuthAuthorizationServiceImpl implements OAuthAuthorizationService
{
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private UaaService uaaService;


    @Override
    public boolean lookupAndProcessProfile( Context context, String authorizationCode )
    {
        boolean profileValid = false;
        if ( StringUtils.isNotEmpty( authorizationCode ) )
        {
            profileValid = true;

            OAuth2AccessToken accessToken = uaaService.getAccessToken( authorizationCode );

            log.debug( "Token type: {}", accessToken.getTokenType() );
            log.debug( "Token scope: {}", accessToken.getScope() );
            log.debug( "Access token: {}", accessToken.getAccessToken() != null ? "REDACTED" : null );
            log.debug( "Refresh token: {}", accessToken.getRefreshToken() != null ? "REDACTED" : null );
            log.debug( "Token expired in: {}", accessToken.getExpiresIn() );

            final OAuth2AccessToken token = uaaService.refreshAccessToken( accessToken.getRefreshToken() );

            final UaaProfile profile = uaaService.getProfile( token.getAccessToken() );

            String json = context.getSession().get( Constants.WEB_SESSION );

            try
            {
                Session session = StringUtils.isNotEmpty( json ) ? objectMapper.readValue( json, WebSession.class ) :
                                  new WebSession( profile );

                session.setAccessToken( accessToken.getAccessToken() );
                session.setRefreshToken( accessToken.getRefreshToken() );
                context.getSession().put( Constants.WEB_SESSION, objectMapper.writeValueAsString( session ) );
            }
            catch ( IOException e )
            {
                log.error( e.getMessage(), e );
                profileValid = false;
            }
        }

        return profileValid;
    }
}
