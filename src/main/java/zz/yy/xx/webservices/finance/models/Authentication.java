package zz.yy.xx.webservices.finance.models;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Component
public class Authentication {

    private RestOperations restTemplate;
    @Value("${auth.webservice.url}")
    private String authWebServiceUrl;
    @Value("${auth.webservice.format}")
    private String format;
    //not sure how to handle getting this to log using AOP (see notes in SessionInterceptor)
    private static final Logger logger = Logger.getLogger(Authentication.class);

    @Autowired
    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ParameterHolder getDataForService(String apiKey, String consumer) throws RestClientException, UnsupportedEncodingException {

        //actual request issued to auth looks as follows as Spring automatically decodes apiKey before usage within app
        //and then automatically encodes the WS URL when calling restTemplate
        logger.debug("WS URL (actual request) = " + authWebServiceUrl + "apiKey=" + URLEncoder.encode(apiKey, "UTF8") + "&consumer=" + consumer + "&format=" + format);

        String authWsUrl = authWebServiceUrl + "apiKey=" + apiKey + "&consumer=" + consumer + "&format=" + format;
        logger.trace("WS URL (decoded request) = " + authWsUrl);

        ParameterHolder parameterHolder = restTemplate.getForObject(authWsUrl, ParameterHolder.class);

        return parameterHolder;
    }
}

