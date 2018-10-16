package kg.timur.uaa;


import kg.timur.uaa.oauth.UaaProfile;


public class WebSession implements Session
{
    private UaaProfile userProfile;

    private String accessToken;

    private String refreshToken;


    WebSession()
    {
    }


    public WebSession( final UaaProfile userProfile )
    {
        this.userProfile = userProfile;
    }


    @Override
    public UserProfile getUserProfile()
    {
        return this.userProfile;
    }


    @Override
    public String getAccessToken()
    {
        return this.accessToken;
    }


    @Override
    public String getRefreshToken()
    {
        return this.refreshToken;
    }


    @Override
    public void setAccessToken( final String accessToken )
    {
        this.accessToken = accessToken;
    }


    @Override
    public void setRefreshToken( final String refreshToken )
    {
        this.refreshToken = refreshToken;
    }
}
