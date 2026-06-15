package com.ruoyi.web.controller.agri;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.config.properties.NetworkClientProperties;
import com.ruoyi.framework.web.outbound.NetworkOutboundService;

/** Agri: outbound network (ping, allowlisted fetch, Open-Meteo weather). */
@RestController
@RequestMapping("/agri/network")
public class AgriNetworkController extends BaseController
{
    @Autowired
    private NetworkOutboundService networkOutboundService;

    @Autowired
    private NetworkClientProperties networkClientProperties;

    @PreAuthorize("@ss.hasPermi('agri:monitor:view') or @ss.hasPermi('agri:remote:view')")
    @GetMapping("/status")
    public AjaxResult status()
    {
        Map<String, Object> data = new HashMap<>(6);
        data.put("enabled", networkClientProperties.isEnabled());
        data.put("pingUrl", networkClientProperties.getPingUrl());
        data.put("allowedHosts", networkClientProperties.getAllowedHosts());
        data.put("connectTimeoutMs", networkClientProperties.getConnectTimeoutMs());
        data.put("readTimeoutMs", networkClientProperties.getReadTimeoutMs());
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view') or @ss.hasPermi('agri:remote:view')")
    @GetMapping("/ping")
    public AjaxResult ping()
    {
        long costMs = networkOutboundService.pingConfiguredUrl();
        Map<String, Object> data = new HashMap<>(2);
        data.put("costMs", costMs);
        data.put("ok", Boolean.TRUE);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view') or @ss.hasPermi('agri:remote:operate')")
    @GetMapping("/fetch")
    public AjaxResult fetch(@RequestParam("url") String url)
    {
        return success(networkOutboundService.httpGet(url));
    }

    /** Open-Meteo: observationTime, temperatureC, relativeHumidityPct, ... */
    @PreAuthorize("@ss.hasPermi('agri:monitor:view') or @ss.hasPermi('agri:remote:view')")
    @GetMapping("/weather")
    public AjaxResult weather(@RequestParam(value = "latitude", defaultValue = "39.9042") double latitude,
        @RequestParam(value = "longitude", defaultValue = "116.4074") double longitude)
    {
        return success(networkOutboundService.fetchOpenMeteoCurrentParsed(latitude, longitude));
    }
}
