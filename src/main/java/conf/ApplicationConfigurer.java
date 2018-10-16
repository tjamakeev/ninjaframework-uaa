package conf;


import java.security.Security;
import java.text.SimpleDateFormat;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import ninja.lifecycle.Start;


@Slf4j
@Singleton
public class ApplicationConfigurer
{
    @Inject
    private ObjectMapper objectMapper;


    @Start( order = 90 )
    public void configureBouncyCastle()
    {
        Security.addProvider( new BouncyCastleProvider() );
        log.info( "BouncyCastle provider added." );
    }


    @Start( order = 90 )
    public void configureObjectMapper()
    {
        log.info( "Configuring object mapper..." );
        // adding Joda Time parsing and rendering support to Jackson
        //        objectMapper.registerModule( new JodaModule() );
        objectMapper.enable( SerializationFeature.INDENT_OUTPUT );
        objectMapper.disable( MapperFeature.DEFAULT_VIEW_INCLUSION );
        objectMapper.setPropertyNamingStrategy( PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES );
        objectMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        // include non-nulls?
        //objectMapper.getSerializationConfig().withSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        objectMapper.configure( JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true );
        objectMapper.configure( JsonParser.Feature.ALLOW_SINGLE_QUOTES, true );
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
        objectMapper.setDateFormat( dateFormat );
    }
}
