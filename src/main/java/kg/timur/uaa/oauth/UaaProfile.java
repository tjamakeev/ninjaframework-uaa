package kg.timur.uaa.oauth;


import kg.timur.uaa.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class UaaProfile implements UserProfile
{
    private String userId;
    private String userName;
    private String name;
    private String givenName;
    private String familyName;
    private String email;
    private Boolean emailVerified;
    private Long previousLogonTime;
    private String sub;
}
