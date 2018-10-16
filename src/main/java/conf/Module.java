/**
 * Copyright (C) 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */


package conf;


import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import conf.provider.OAuth20ServiceProvider;
import conf.provider.SigningKeyResolverProvider;
import kg.timur.uaa.OAuthAuthorizationServiceImpl;
import kg.timur.uaa.SigningKeyResolver;
import kg.timur.uaa.oauth.OAuthAuthorizationService;


@Singleton
public class Module extends AbstractModule
{
    protected void configure()
    {
        // bind your injections here!
        bind( ApplicationConfigurer.class );
        bind( OAuth20Service.class ).toProvider( OAuth20ServiceProvider.class );
        bind( OAuthAuthorizationService.class ).to( OAuthAuthorizationServiceImpl.class );
        bind( SigningKeyResolver.class ).toProvider( SigningKeyResolverProvider.class ).asEagerSingleton();
    }
}
