package com.ruoyi.framework.web.outbound;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.common.enums.NetworkAccessMessageEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.config.properties.NetworkClientProperties;

/**
 * Outbound HTTP with allowlist, timeouts, and response size cap.
 *
 * @author ruoyi
 */
@Service
public class NetworkOutboundService
{
    private static final Logger log = LoggerFactory.getLogger(NetworkOutboundService.class);

    private final RestTemplate networkRestTemplate;
    private final NetworkClientProperties properties;

    public NetworkOutboundService(@Qualifier("networkRestTemplate") RestTemplate networkRestTemplate,
        NetworkClientProperties properties)
    {
        this.networkRestTemplate = networkRestTemplate;
        this.properties = properties;
    }

    /** Ping configured pingUrl; returns round-trip ms. */
    public long pingConfiguredUrl()
    {
        assertEnabled();
        URI uri = parseAndValidatePingUri();
        long t0 = System.currentTimeMillis();
        try
        {
            networkRestTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        }
        catch (RestClientException e)
        {
            log.warn("network ping failed: {}", uri, e);
            throw new ServiceException(NetworkAccessMessageEnum.REQUEST_FAILED.getMessage())
                .setDetailMessage(e.getMessage());
        }
        return System.currentTimeMillis() - t0;
    }

    /** Open-Meteo raw JSON (current temp + humidity). */
    public String fetchOpenMeteoCurrent(double latitude, double longitude)
    {
        assertEnabled();
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
        {
            throw new ServiceException(NetworkAccessMessageEnum.INVALID_COORDINATES.getMessage());
        }
        String url = String.format(Locale.US,
            "https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&current=temperature_2m,relative_humidity_2m",
            latitude, longitude);
        return httpGet(url);
    }

    /** Parsed Open-Meteo current temperature and humidity. */
    public Map<String, Object> fetchOpenMeteoCurrentParsed(double latitude, double longitude)
    {
        String raw = fetchOpenMeteoCurrent(latitude, longitude);
        if (raw == null || raw.isBlank())
        {
            throw new ServiceException(NetworkAccessMessageEnum.WEATHER_PARSE_FAILED.getMessage());
        }
        JSONObject root;
        try
        {
            root = JSON.parseObject(raw);
        }
        catch (Exception e)
        {
            log.warn("open-meteo json parse error", e);
            throw new ServiceException(NetworkAccessMessageEnum.WEATHER_PARSE_FAILED.getMessage());
        }
        JSONObject current = root == null ? null : root.getJSONObject("current");
        if (current == null)
        {
            throw new ServiceException(NetworkAccessMessageEnum.WEATHER_PARSE_FAILED.getMessage());
        }
        Map<String, Object> row = new LinkedHashMap<>(8);
        row.put("observationTime", current.getString("time"));
        row.put("temperatureC", current.get("temperature_2m"));
        row.put("relativeHumidityPct", current.get("relative_humidity_2m"));
        row.put("latitude", latitude);
        row.put("longitude", longitude);
        row.put("source", "Open-Meteo");
        return row;
    }

    public String httpGet(String url)
    {
        HttpTextResponse response = httpGetText(url);
        return response.getBody();
    }

    /** GET as bytes then decode with Content-Type / XML declared charset (fixes RSS mojibake). */
    public HttpTextResponse httpGetText(String url)
    {
        assertEnabled();
        URI uri = parseHttpUri(url);
        assertHostAllowed(uri.getHost());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "RuoYi-Agri-Network/1.0");
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.ALL));
        try
        {
            ResponseEntity<byte[]> resp = networkRestTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(headers), byte[].class);
            byte[] raw = resp.getBody();
            if (raw == null || raw.length == 0)
            {
                return new HttpTextResponse("", null);
            }
            int max = Math.max(1024, properties.getMaxResponseChars());
            if (raw.length > max * 4)
            {
                throw new ServiceException(NetworkAccessMessageEnum.RESPONSE_TOO_LARGE.getMessage());
            }
            String charset = null;
            MediaType mediaType = resp.getHeaders().getContentType();
            if (mediaType != null && mediaType.getCharset() != null)
            {
                charset = mediaType.getCharset().name();
            }
            return new HttpTextResponse(
                com.ruoyi.common.utils.agri.AgriRssCharsetUtils.decodeXmlBytes(raw, charset), charset);
        }
        catch (RestClientException e)
        {
            log.warn("network http get failed: {}", uri, e);
            throw new ServiceException(NetworkAccessMessageEnum.REQUEST_FAILED.getMessage())
                .setDetailMessage(e.getMessage());
        }
    }

    public static final class HttpTextResponse
    {
        private final String body;
        private final String charset;

        public HttpTextResponse(String body, String charset)
        {
            this.body = body == null ? "" : body;
            this.charset = charset;
        }

        public String getBody()
        {
            return body;
        }

        public String getCharset()
        {
            return charset;
        }
    }

    private void assertEnabled()
    {
        if (!properties.isEnabled())
        {
            throw new ServiceException(NetworkAccessMessageEnum.FEATURE_DISABLED.getMessage());
        }
    }

    private URI parseAndValidatePingUri()
    {
        String pingUrl = properties.getPingUrl();
        if (pingUrl == null || pingUrl.isBlank())
        {
            throw new ServiceException(NetworkAccessMessageEnum.PING_URL_INVALID.getMessage());
        }
        URI uri = parseHttpUri(pingUrl.trim());
        assertHostAllowed(uri.getHost());
        return uri;
    }

    private URI parseHttpUri(String url)
    {
        URI uri;
        try
        {
            uri = URI.create(url);
        }
        catch (IllegalArgumentException e)
        {
            throw new ServiceException(NetworkAccessMessageEnum.INVALID_URL.getMessage());
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)))
        {
            throw new ServiceException(NetworkAccessMessageEnum.INVALID_URL.getMessage());
        }
        if (uri.getHost() == null || uri.getHost().isBlank())
        {
            throw new ServiceException(NetworkAccessMessageEnum.INVALID_URL.getMessage());
        }
        if (uri.getUserInfo() != null)
        {
            throw new ServiceException(NetworkAccessMessageEnum.INVALID_URL.getMessage());
        }
        return uri;
    }

    /** Host allowlist: exact or *.suffix for subdomains. */
    public void assertHostAllowed(String host)
    {
        if (host == null)
        {
            throw new ServiceException(NetworkAccessMessageEnum.HOST_NOT_ALLOWED.getMessage());
        }
        String h = host.toLowerCase(Locale.ROOT);
        for (String pattern : properties.getAllowedHosts())
        {
            if (pattern == null || pattern.isBlank())
            {
                continue;
            }
            String p = pattern.trim().toLowerCase(Locale.ROOT);
            if (p.startsWith("*."))
            {
                String suffix = p.substring(2);
                if (h.equals(suffix) || h.endsWith("." + suffix))
                {
                    return;
                }
            }
            else if (h.equals(p))
            {
                return;
            }
        }
        throw new ServiceException(NetworkAccessMessageEnum.HOST_NOT_ALLOWED.getMessage());
    }
}
