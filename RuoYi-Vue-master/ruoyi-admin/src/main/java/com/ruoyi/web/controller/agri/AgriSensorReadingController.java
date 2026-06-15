package com.ruoyi.web.controller.agri;
import com.ruoyi.common.utils.poi.ExcelUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.common.utils.poi.ExcelUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.framework.web.outbound.NetworkOutboundService;
import com.ruoyi.system.service.IAgriSensorNodeService;
import com.ruoyi.system.service.IAgriSensorReadingService;

/**
 * ???????????????????????
 */
@RestController
@RequestMapping("/agri/reading")
public class AgriSensorReadingController extends BaseController
{
    @Autowired
    private IAgriSensorReadingService agriSensorReadingService;

    @Autowired
    private IAgriSensorNodeService agriSensorNodeService;

    @Autowired
    private NetworkOutboundService networkOutboundService;

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/list")
    public TableDataInfo list(AgriSensorReading reading)
    {
        startPage();
        List<AgriSensorReading> list = agriSensorReadingService.selectAgriSensorReadingList(reading);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/latest/{nodeId}")
    public AjaxResult latest(@PathVariable Long nodeId)
    {
        return success(agriSensorReadingService.selectLatestByNodeId(nodeId));
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/trend/{nodeId}")
    public AjaxResult trend(@PathVariable Long nodeId,
        @RequestParam(value = "hours", defaultValue = "24") int hours)
    {
        if (hours < 1 || hours > 168)
        {
            hours = 24;
        }
        return success(agriSensorReadingService.selectTrendByNodeId(nodeId, hours));
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @GetMapping("/advice/{nodeId}")
    public AjaxResult advice(@PathVariable Long nodeId,
        @RequestParam(value = "hours", defaultValue = "24") int hours,
        @RequestParam(value = "usePublicWeather", defaultValue = "true") boolean usePublicWeather,
        @RequestParam(value = "refLat", required = false) Double refLat,
        @RequestParam(value = "refLon", required = false) Double refLon)
    {
        if (hours < 1 || hours > 168)
        {
            hours = 24;
        }
        BigDecimal outdoorTemp = null;
        Integer outdoorRh = null;
        String outdoorTime = null;
        if (usePublicWeather)
        {
            try
            {
                double lat = refLat != null ? refLat : 39.9042;
                double lon = refLon != null ? refLon : 116.4074;
                Map<String, Object> w = networkOutboundService.fetchOpenMeteoCurrentParsed(lat, lon);
                Object t = w.get("temperatureC");
                if (t instanceof Number)
                {
                    outdoorTemp = BigDecimal.valueOf(((Number)t).doubleValue()).setScale(1, RoundingMode.HALF_UP);
                }
                Object h = w.get("relativeHumidityPct");
                if (h instanceof Number)
                {
                    outdoorRh = ((Number)h).intValue();
                }
                Object time = w.get("observationTime");
                if (time != null)
                {
                    outdoorTime = time.toString();
                }
            }
            catch (Exception ignored)
            {
                // 公网气象失败时仍返回基于传感器的建议
            }
        }
        return success(agriSensorReadingService.analyzeGrowthAdvice(nodeId, hours, outdoorTemp, outdoorRh, outdoorTime));
    }

    /**
     * ???????????????????????????????????????MQ?????
     */
    @PreAuthorize("@ss.hasPermi('agri:monitor:ingest')")
    @Log(title = "????????????", businessType = BusinessType.INSERT)
    @PostMapping("/ingest")
    public AjaxResult ingest(@RequestBody AgriSensorReading reading)
    {
        if (reading.getNodeId() == null)
        {
            return error("???ID???????");
        }
        AgriSensorNode node = agriSensorNodeService.selectAgriSensorNodeById(reading.getNodeId());
        if (node == null)
        {
            return error("?????????????");
        }
        if (!"0".equals(node.getStatus()))
        {
            return error("???????????????");
        }
        if (reading.getReadingTime() == null)
        {
            reading.setReadingTime(new Date());
        }
        return toAjax(agriSensorReadingService.insertAgriSensorReading(reading));
    }

    /**
     * ??????????????????????????
     */
    @PreAuthorize("@ss.hasPermi('agri:monitor:ingest') or @ss.hasPermi('agri:remote:operate')")
    @Log(title = "???????????", businessType = BusinessType.INSERT)
    @PostMapping("/simulate/{nodeId}")
    public AjaxResult simulate(@PathVariable Long nodeId)
    {
        AgriSensorNode node = agriSensorNodeService.selectAgriSensorNodeById(nodeId);
        if (node == null)
        {
            return error("?????????????");
        }
        if (!"0".equals(node.getStatus()))
        {
            return error("????????");
        }
        ThreadLocalRandom r = ThreadLocalRandom.current();
        AgriSensorReading reading = new AgriSensorReading();
        reading.setNodeId(nodeId);
        reading.setSoilMoisturePct(bd(r.nextInt(200, 751), 10));
        reading.setAirTempC(bd(r.nextInt(150, 321), 10));
        reading.setAirHumidityPct(bd(r.nextInt(350, 901), 10));
        reading.setLightLux(r.nextInt(5000, 45001));
        reading.setSoilTempC(bd(r.nextInt(140, 281), 10));
        reading.setReadingTime(new Date());
        agriSensorReadingService.insertAgriSensorReading(reading);
        return success(reading);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @Log(title = "?????????", businessType = BusinessType.DELETE)
    @DeleteMapping("/{readingIds}")
    public AjaxResult remove(@PathVariable Long[] readingIds)
    {
        return toAjax(agriSensorReadingService.deleteAgriSensorReadingByIds(readingIds));
    }

    private static BigDecimal bd(int v, int scale)
    {
        return BigDecimal.valueOf(v).divide(BigDecimal.valueOf(scale), 2, RoundingMode.HALF_UP);
    }

    @PreAuthorize("@ss.hasPermi('agri:monitor:view')")
    @Log(title = "传感器数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgriSensorReading reading)
    {
        List<AgriSensorReading> list = agriSensorReadingService.selectAgriSensorReadingList(reading);
        ExcelUtil<AgriSensorReading> util = new ExcelUtil<AgriSensorReading>(AgriSensorReading.class);
        util.exportExcel(response, list, "传感器数据");
    }
}
