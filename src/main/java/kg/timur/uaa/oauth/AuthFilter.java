package kg.timur.uaa.oauth;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.inject.Inject;

import conf.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kg.timur.uaa.SigningKeyResolver;
import kg.timur.uaa.WebSession;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import ninja.utils.NinjaProperties;


/**
 * Checks if user is authorized
 */
public class AuthFilter implements Filter
{
    private final Logger log = LoggerFactory.getLogger( AuthFilter.class );

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private NinjaProperties ninjaProperties;

    @Inject
    private UaaService uaaService;

    @Inject
    private SigningKeyResolver signingKeyResolver;


    @Override
    public Result filter( FilterChain filterChain, Context ctx )
    {
        String requestPath = ctx.getRequestPath();
        if ( !requestPath.contains( "/app/" ) )
        {
            return filterChain.next( ctx );
        }

        Session session = ctx.getSession();

        if ( session == null )
        {
            return Results.forbidden().html().template( "/views/system/403forbidden.ftl.html" );
        }


        WebSession webSession = lookupWebSession( session );

        if ( webSession == null )
        {
            return Results.forbidden().html().template( "/views/system/403forbidden.ftl.html" );
        }

        try
        {
            final Claims claims = parseJWT( webSession.getAccessToken() );

            boolean needToRefresh = claims.getIssuedAt().getTime() + TimeUnit.MINUTES
                    .toMillis( ninjaProperties.getInteger( Constants.OAUTH_ACCESS_TOKEN_REFRESH ) ) < System
                    .currentTimeMillis();

            log.info( "Issued at: {}. Current time: {}. Need to refresh: {}", claims.getIssuedAt(), new Date(),
                    needToRefresh );


            if ( needToRefresh )
            {
                // trying to refresh the access token
                final OAuth2AccessToken accessToken = uaaService.refreshAccessToken( webSession.getRefreshToken() );

                log.debug( "Access token refreshed successfully for: {}", claims.getSubject() );
                webSession.setAccessToken( accessToken.getAccessToken() );
                webSession.setRefreshToken( accessToken.getRefreshToken() );

                session.put( Constants.WEB_SESSION, objectMapper.writeValueAsString( webSession ) );
            }

            ctx.setAttribute( Constants.WEB_SESSION, webSession );
        }
        catch ( Exception e )
        {
            log.warn( "Web session exception: {}", e.getMessage() );
            return Results.redirect( ctx.getContextPath() + "/login" );
        }


        return filterChain.next( ctx );
    }


    private WebSession lookupWebSession( final Session session )
    {
        String sessionData = session.get( Constants.WEB_SESSION );

        if ( sessionData == null )
        {
            return null;
        }

        try
        {
            return objectMapper.readValue( sessionData, WebSession.class );
        }
        catch ( IOException e )
        {
            return null;
        }
    }


    private Claims parseJWT( String jwt )
    {
        return Jwts.parser().setSigningKeyResolver( signingKeyResolver ).parseClaimsJws( jwt ).getBody();
    }
}