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


import com.google.inject.Inject;

import controllers.ApplicationController;
import controllers.OAuth2Controller;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;


public class Routes implements ApplicationRoutes
{
    @Inject
    private NinjaProperties ninjaProperties;


    @Override
    public void init( Router router )
    {


        router.GET().route( "/login" ).with( OAuth2Controller::login );
        router.GET().route( "/logout" ).with( OAuth2Controller::logout );
        router.GET().route( "/oauth/callback" ).with( OAuth2Controller::oauthCallback );
        router.GET().route( "/oauth/logout" ).with( OAuth2Controller::oauthLogout );
        router.GET().route( "/oauth/success" ).with( OAuth2Controller::oauthSuccess );
        router.GET().route( "/oauth/failure" ).with( OAuth2Controller::oauthFailure );

        router.GET().route( "/" ).with( ApplicationController::index );
        router.GET().route( "/app/hello_world.json" ).with( ApplicationController::helloWorldJson );

        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////    
        router.GET().route( "/assets/webjars/{fileName: .*}" ).with( AssetsController::serveWebJars );
        router.GET().route( "/assets/{fileName: .*}" ).with( AssetsController::serveStatic );

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route( "/.*" ).with( ApplicationController::index );
    }
}
