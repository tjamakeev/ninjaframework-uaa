package kg.timur.uaa;


public interface Session
{
    UserProfile getUserProfile();

    String getAccessToken();

    String getRefreshToken();

    void setAccessToken( String accessToken );

    void setRefreshToken( String refreshToken );
}
