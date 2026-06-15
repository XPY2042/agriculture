package com.ruoyi.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Outbound HTTP client settings (ruoyi.network.*).
 */
@Component
@ConfigurationProperties(prefix = "ruoyi.network")
public class NetworkClientProperties
{
    private boolean enabled = true;

    private int connectTimeoutMs = 8000;

    private int readTimeoutMs = 15000;

    /** Connectivity probe URL (server-side only). */
    private String pingUrl = "https://www.baidu.com";

    /** Allowed remote hosts; use *.example.com for subdomains. */
    private List<String> allowedHosts = new ArrayList<>();

    private int maxResponseChars = 262144;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public int getConnectTimeoutMs()
    {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs)
    {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs()
    {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs)
    {
        this.readTimeoutMs = readTimeoutMs;
    }

    public String getPingUrl()
    {
        return pingUrl;
    }

    public void setPingUrl(String pingUrl)
    {
        this.pingUrl = pingUrl;
    }

    public List<String> getAllowedHosts()
    {
        return allowedHosts;
    }

    public void setAllowedHosts(List<String> allowedHosts)
    {
        this.allowedHosts = allowedHosts;
    }

    public int getMaxResponseChars()
    {
        return maxResponseChars;
    }

    public void setMaxResponseChars(int maxResponseChars)
    {
        this.maxResponseChars = maxResponseChars;
    }
}
