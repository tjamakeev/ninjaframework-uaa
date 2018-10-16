package kg.timur.uaa.oauth;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import conf.Constants;
import kg.timur.uaa.WebSession;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;


/**
 * Checks the OAuth authorization token
 */
public class RefreshFilter implements Filter
{
    private final Logger log = LoggerFactory.getLogger( RefreshFilter.class );

    @Inject
    private ObjectMapper mapper;


    @Override
    public Result filter( FilterChain filterChain, Context ctx )
    {
        log.info( "Refresh filter..." );

        String requestPath = ctx.getRequestPath();
        if ( !requestPath.startsWith( "/app/" ) )
        {
            return filterChain.next( ctx );
        }

        Session session = ctx.getSession();

        if ( session == null )
        {
            return Results.forbidden().html().template( "/views/system/403forbidden.ftl.html" );
        }

        String sessionData = session.get( Constants.WEB_SESSION );

        if ( sessionData == null )
        {
            return Results.forbidden().html().template( "/views/system/403forbidden.ftl.html" );
        }

        try
        {
            WebSession webSession = mapper.readValue( sessionData, WebSession.class );

            ctx.setAttribute( Constants.WEB_SESSION, webSession );
        }
        catch ( IOException ex )
        {
            log.warn( "Invalid web session", ex );
        }

        return filterChain.next( ctx );
    }
}