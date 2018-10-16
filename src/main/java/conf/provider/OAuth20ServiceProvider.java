package conf.provider;


import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.Inject;
import com.google.inject.Provider;

import conf.Constants;
import kg.timur.uaa.oauth.UaaApi;
import ninja.utils.NinjaProperties;


public class OAuth20ServiceProvider implements Provider<OAuth20Service>
{
    @Inject
    private NinjaProperties ninjaProperties;

    @Inject
    private UaaApi uaaApi;


    @Override
    public OAuth20Service get()
    {
        return new ServiceBuilder( ninjaProperties.get( Constants.OAUTH_CLIENT_ID ) )
                .apiSecret( ninjaProperties.get( Constants.OAUTH_CLIENT_SECRET ) ).
                        callback( ninjaProperties.get( Constants.OAUTH_REDIRECT_URI ) ).
                        build( uaaApi );
    }
}
