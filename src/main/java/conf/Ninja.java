package conf;


import com.github.scribejava.core.exceptions.OAuthException;
import com.google.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;


@Slf4j
public class Ninja extends NinjaDefault
{
    @Inject
    private NinjaProperties properties;


    @Override
    public void onRouteRequest( final Context.Impl context )
    {
        log.debug( "{} {}: {}", context.getMethod(), context.getRequestUri(), context.getRemoteAddr() );
        super.onRouteRequest( context );
    }


    /**
     * At final point all exceptions, handled by Ninja, reach this method.
     */
    @Override
    public void renderErrorResultAndCatchAndLogExceptions( Result result, Context context )
    {
        super.renderErrorResultAndCatchAndLogExceptions( result, context );
    }


    private Result handleOAuthException( final Context context, final OAuthException e )
    {
        context.getFlashScope().error( e.getMessage() );
        return Results.redirect( properties.get( "oauth.failure.url" ) );
    }


    @Override
    public Result onException( final Context context, final Exception e, final Result underlyingResult )
    {
        if ( e instanceof OAuthException )
        {
            return handleOAuthException( context, ( OAuthException ) e );
        }

        return super.onException( context, e, underlyingResult );
    }
}
