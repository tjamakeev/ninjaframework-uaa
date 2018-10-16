package kg.timur.uaa.oauth;


import ninja.Context;


/**
 * the service is used to lookup the provided oauth profile in a profile store. The logic determines if the profile is
 * valid and processes the profile date (e.g. store it in session, ...)
 *
 * @author henrik
 */
public interface OAuthAuthorizationService
{

    /**
     * lookup the profile in a store and handle further processing
     *
     * @param context Ninja Context object
     * @param accessCode an OAuth authorization code
     *
     * @return true or false if the lookup was successful
     */
    boolean lookupAndProcessProfile( Context context, String accessCode );
}
