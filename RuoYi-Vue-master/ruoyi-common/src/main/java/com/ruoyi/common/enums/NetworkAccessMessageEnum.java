package com.ruoyi.common.enums;

/**
 * Outbound network / weather error messages (ASCII to avoid source encoding issues).
 */
public enum NetworkAccessMessageEnum
{
    FEATURE_DISABLED("Network client disabled; set ruoyi.network.enabled=true in application.yml"),
    HOST_NOT_ALLOWED("Host is not in ruoyi.network.allowed-hosts allowlist"),
    INVALID_URL("Invalid URL: only http/https, no userinfo in URL"),
    RESPONSE_TOO_LARGE("Response body exceeds max length"),
    PING_URL_INVALID("Invalid ruoyi.network.ping-url"),
    REQUEST_FAILED("Outbound HTTP request failed"),
    INVALID_COORDINATES("Invalid coordinates: lat [-90,90], lon [-180,180]"),
    WEATHER_PARSE_FAILED("Failed to parse public weather API response");

    private final String message;

    NetworkAccessMessageEnum(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
