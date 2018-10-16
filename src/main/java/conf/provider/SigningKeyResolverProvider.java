package conf.provider;


import com.google.inject.Inject;
import com.google.inject.Provider;

import conf.Constants;
import kg.timur.uaa.SigningKeyResolver;
import ninja.utils.NinjaProperties;


public class SigningKeyResolverProvider implements Provider<SigningKeyResolver>
{
    @Inject
    private NinjaProperties ninjaProperties;


    @Override
    public SigningKeyResolver get()
    {
        return new SigningKeyResolver( ninjaProperties.get( Constants.APPLICATION_KEYS_FOLDER ) );
    }
}
