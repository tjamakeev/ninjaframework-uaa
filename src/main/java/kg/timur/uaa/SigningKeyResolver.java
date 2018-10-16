package kg.timur.uaa;


import java.io.File;
import java.io.FileReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SigningKeyResolver extends SigningKeyResolverAdapter
{
    private String keyDir;
    private Map<String, Key> cache = new ConcurrentHashMap<>();


    public SigningKeyResolver( final String keyDir )
    {
        this.keyDir = keyDir;
    }


    @Override
    public Key resolveSigningKey( final JwsHeader jwsHeader, final Claims claims )
    {
        //inspect the header or claims, lookup and return the signing key
        String keyId = jwsHeader.getKeyId();

        try
        {
            return lookupVerificationKey( keyId );
        }
        catch ( Exception e )
        {
            log.warn( "Could not lookup key: {}, {}", keyId, e.getMessage() );
            return null;
        }
    }


    private Key lookupVerificationKey( final String keyId ) throws Exception
    {

        final Key result = cache.get( keyId );

        if ( result != null )
        {
            return result;
        }

        String filename = keyDir + File.separator + keyId + ".pub.asc";
        log.debug( "Expect public key file: {}", filename );

        final PemReader reader = new PemReader( new FileReader( filename ) );

        final PemObject pemObject = reader.readPemObject();

        X509EncodedKeySpec spec = new X509EncodedKeySpec( pemObject.getContent() );

        KeyFactory kf = KeyFactory.getInstance( "RSA" );

        PublicKey publicKey = kf.generatePublic( spec );

        cache.put( keyId, publicKey );

        return publicKey;
    }
}

