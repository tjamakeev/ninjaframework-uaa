package controllers;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import conf.Constants;
import kg.timur.uaa.oauth.OAuthAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;


@Slf4j
@Singleton
public class OAuth2Controller
{
    public static final String ERROR_PARAM = "error";
    public static final String CODE_PARAM = "code";
    public static final String STATE_PARAM = "state";
    private static final String OAUTH_STATE = "oauth-state";

    @Inject
    private OAuth20Service auth20Service;

    @Inject
    private OAuthAuthorizationService authorizationService;

    private NinjaProperties ninjaProperties;

    private Result oauthFailedRedirect;


    @Inject
    public OAuth2Controller( NinjaProperties ninjaProperties )
    {
        this.ninjaProperties = ninjaProperties;
        this.oauthFailedRedirect = Results.redirect( ninjaProperties.get( Constants.OAUTH_FAILURE_URL ) );
    }


    public Result login( Context context )
    {
        String state = UUID.randomUUID().toString().substring( 0, 8 );

        saveOauthState( context, state );

        final Map<String, String> params = new HashMap<>();
        //        params.put( "token_format", "opaque" );
        params.put( "scope", ninjaProperties.get( Constants.OAUTH_DEFAULT_SCOPE ) );
        params.put( "state", state );

        String authorizationUrl = auth20Service.getAuthorizationUrl( params );

        log.debug( authorizationUrl );

        return Results.redirect( authorizationUrl );
    }


    public Result logout( Context context ) throws UnsupportedEncodingException
    {
        context.getSession().remove( Constants.WEB_SESSION );

        String redirectUrl = URLEncoder.encode( Constants.OAUTH_LOGOUT_REDIRECT_URL, "UTF-8" );
        String logoutUrl =
                ninjaProperties.get( Constants.OAUTH_LOGOUT_URL ) + ( redirectUrl != null ? "?redirect=" + redirectUrl :
                                                                      "" );
        return Results.redirectTemporary( logoutUrl );
    }


    public Result oauthCallback( Context context )
    {
        final String error = context.getParameter( ERROR_PARAM );

        if ( error != null )
        {
            return handleError( context );
        }

        final String code = context.getParameter( CODE_PARAM );

        if ( code != null )
        {
            return handleAuthCode( context );
        }

        return oauthFailedRedirect;
    }


    public Result oauthSuccess()
    {
        return Results.html();
    }


    public Result oauthLogout()
    {
        return Results.html();
    }


    public Result oauthFailure()
    {
        return Results.html();
    }


    private Result handleAuthCode( final Context context )
    {
        String oauthState = getOauthState( context );

        if ( oauthState == null || !oauthState.equals( context.getParameter( STATE_PARAM ) ) )
        {
            context.getFlashScope().error( "Invalid OAuth2 state parameter" );
            return this.oauthFailedRedirect;
        }

        removeOauthState( context );

        String accessCode = context.getParameter( "code" );

        boolean validProfile = authorizationService.lookupAndProcessProfile( context, accessCode );

        if ( !validProfile )
        {
            log.error( "Could authenticate the user" );
            context.getFlashScope().error( "Could not lookup and process the profile! " );
            return Results.redirect( ninjaProperties.get( Constants.OAUTH_FAILURE_URL ) );
        }

        return Results.redirect( ninjaProperties.get( Constants.OAUTH_SUCCESS_URL ) );
    }


    private Result handleError( final Context context )
    {
        context.getFlashScope().error( context.getParameter( "error_description" ) );
        return oauthFailedRedirect;
    }


    protected void saveOauthState( Context context, String state )
    {
        context.getSession().put( OAUTH_STATE, state );
    }


    protected void removeOauthState( Context context )
    {
        context.getSession().remove( OAUTH_STATE );
    }


    protected String getOauthState( Context context )
    {
        return context.getSession().get( OAUTH_STATE );
    }
}
