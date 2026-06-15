package com.ruoyi.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.framework.config.properties.NetworkClientProperties;

/**
 * RestTemplate bean for outbound HTTP (ruoyi.network timeouts).
 *
 * @author ruoyi
 */
@Configuration
public class NetworkClientConfig
{
    @Bean
    public RestTemplate networkRestTemplate(NetworkClientProperties properties)
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeoutMs());
        factory.setReadTimeout(properties.getReadTimeoutMs());
        return new RestTemplate(factory);
    }
}
