package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.enums.agri.AgriCropKindEnum;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AgriSensorNode;
import com.ruoyi.system.domain.AgriSensorReading;
import com.ruoyi.system.domain.vo.AgriAdviceItemVo;
import com.ruoyi.system.domain.vo.AgriGrowthAdviceVo;
import com.ruoyi.system.domain.vo.AgriReadingAggregateVo;
import com.ruoyi.system.mapper.AgriSensorNodeMapper;
import com.ruoyi.system.mapper.AgriSensorReadingMapper;
import com.ruoyi.system.service.IAgriAlarmService;
import com.ruoyi.system.service.IAgriSensorReadingService;

@Service
public class AgriSensorReadingServiceImpl implements IAgriSensorReadingService
{
    /** 土壤湿度阈值（%） */
    private static final BigDecimal SOIL_LOW = new BigDecimal("25");
    private static final BigDecimal SOIL_OPT_LOW = new BigDecimal("35");
    private static final BigDecimal SOIL_OPT_HIGH = new BigDecimal("65");
    private static final BigDecimal SOIL_HIGH = new BigDecimal("75");

    /** 气温（℃） */
    private static final BigDecimal TEMP_COLD = new BigDecimal("12");
    private static final BigDecimal TEMP_LOW = new BigDecimal("18");
    private static final BigDecimal TEMP_HIGH = new BigDecimal("28");
    private static final BigDecimal TEMP_HOT = new BigDecimal("32");

    /** 空气湿度（%） */
    private static final BigDecimal HUMI_LOW = new BigDecimal("35");
    private static final BigDecimal HUMI_OPT_LOW = new BigDecimal("45");
    private static final BigDecimal HUMI_OPT_HIGH = new BigDecimal("75");
    private static final BigDecimal HUMI_HIGH = new BigDecimal("85");

    /** 光照（lux） */
    private static final int LIGHT_LOW = 8000;
    private static final int LIGHT_OPT = 15000;

    @Autowired
    private AgriSensorReadingMapper agriSensorReadingMapper;

    @Autowired
    private AgriSensorNodeMapper agriSensorNodeMapper;

    @Autowired
    private IAgriAlarmService agriAlarmService;

    @Override
    public AgriSensorReading selectLatestByNodeId(Long nodeId)
    {
        return agriSensorReadingMapper.selectLatestByNodeId(nodeId);
    }

    @Override
    public List<AgriSensorReading> selectTrendByNodeId(Long nodeId, int hours)
    {
        return agriSensorReadingMapper.selectTrendByNodeId(nodeId, hours);
    }

    @Override
    public List<AgriSensorReading> selectAgriSensorReadingList(AgriSensorReading reading)
    {
        return agriSensorReadingMapper.selectAgriSensorReadingList(reading);
    }

    @Override
    public int insertAgriSensorReading(AgriSensorReading reading)
    {
        int rows = agriSensorReadingMapper.insertAgriSensorReading(reading);
        if (rows > 0)
        {
            AgriSensorNode node = agriSensorNodeMapper.selectAgriSensorNodeById(reading.getNodeId());
            String nodeName = node != null ? node.getNodeName() : null;
            agriAlarmService.evaluateAndSave(reading, nodeName);
        }
        return rows;
    }

    @Override
    public int deleteAgriSensorReadingByIds(Long[] readingIds)
    {
        return agriSensorReadingMapper.deleteAgriSensorReadingByIds(readingIds);
    }

    @Override
    public AgriGrowthAdviceVo analyzeGrowthAdvice(Long nodeId, int windowHours, BigDecimal outdoorAirTempC,
        Integer outdoorRhPct, String outdoorObservationTime)
    {
        AgriGrowthAdviceVo vo = new AgriGrowthAdviceVo();
        vo.setNodeId(nodeId);
        vo.setWindowHours(windowHours);

        AgriSensorNode node = agriSensorNodeMapper.selectAgriSensorNodeById(nodeId);
        if (node == null)
        {
            return vo;
        }
        vo.setNodeName(node.getNodeName());
        vo.setCropType(node.getCropType());
        AgriCropKindEnum cropKind = AgriCropKindEnum.fromCropType(node.getCropType());

        AgriSensorReading latest = agriSensorReadingMapper.selectLatestByNodeId(nodeId);
        AgriReadingAggregateVo avg = agriSensorReadingMapper.selectAvgInWindow(nodeId, windowHours);
        List<AgriSensorReading> trendRows = agriSensorReadingMapper.selectTrendByNodeId(nodeId, windowHours);

        if (latest != null)
        {
            vo.setLatestSoilMoisturePct(scale(latest.getSoilMoisturePct()));
            vo.setLatestAirTempC(scale(latest.getAirTempC()));
            vo.setLatestAirHumidityPct(scale(latest.getAirHumidityPct()));
            vo.setLatestLightLux(latest.getLightLux());
            vo.setLatestSoilTempC(scale(latest.getSoilTempC()));
        }
        if (avg != null)
        {
            vo.setAvgSoilMoisturePct(scale(avg.getAvgSoilMoisturePct()));
            vo.setAvgAirTempC(scale(avg.getAvgAirTempC()));
            vo.setAvgAirHumidityPct(scale(avg.getAvgAirHumidityPct()));
            vo.setAvgLightLux(scale(avg.getAvgLightLux()));
        }

        String trendHint = buildSoilTrendHint(trendRows, windowHours);
        vo.setTrendHint(trendHint);

        List<AgriAdviceItemVo> items = new ArrayList<>();

        BigDecimal soil = latest != null ? latest.getSoilMoisturePct() : null;
        BigDecimal airT = latest != null ? latest.getAirTempC() : null;
        BigDecimal hum = latest != null ? latest.getAirHumidityPct() : null;
        Integer lux = latest != null ? latest.getLightLux() : null;

        if (soil != null)
        {
            if (soil.compareTo(SOIL_LOW) < 0)
            {
                items.add(new AgriAdviceItemVo("土壤湿度",
                    "土壤偏干（低于25%），建议尽快灌溉或滴灌补水，避免植株持续萎蔫。", "danger"));
            }
            else if (soil.compareTo(SOIL_OPT_LOW) < 0)
            {
                items.add(new AgriAdviceItemVo("土壤湿度",
                    "湿度略低（低于35%），可关注未来24小时天气预报，适时适量补水。", "warning"));
            }
            else if (soil.compareTo(SOIL_HIGH) > 0)
            {
                items.add(new AgriAdviceItemVo("土壤湿度",
                    "土壤偏湿（高于75%），注意排水与通风，防范根系缺氧与病害。", "warning"));
            }
            else if (soil.compareTo(SOIL_OPT_HIGH) <= 0)
            {
                items.add(new AgriAdviceItemVo("土壤湿度", "墒情与灌水节奏较为匹配，维持当前管水策略即可。", "info"));
            }
        }

        if (airT != null)
        {
            if (airT.compareTo(TEMP_COLD) < 0)
            {
                items.add(new AgriAdviceItemVo("空气温度",
                    "气温偏低（低于12℃），注意防寒保温或覆膜，推迟敏感农事。", "danger"));
            }
            else if (airT.compareTo(TEMP_LOW) < 0)
            {
                items.add(new AgriAdviceItemVo("空气温度", "气温稍低，幼苗阶段关注夜间低温对出苗的影响。", "warning"));
            }
            else if (airT.compareTo(TEMP_HOT) > 0)
            {
                items.add(new AgriAdviceItemVo("空气温度",
                    "气温酷热（高于32℃），午间前后加强遮阴降温，防范日灼与蒸腾过强。", "danger"));
            }
            else if (airT.compareTo(TEMP_HIGH) > 0)
            {
                items.add(new AgriAdviceItemVo("空气温度", "气温偏高，建议增强通风、叶面喷水或短时遮阳。", "warning"));
            }
            else
            {
                items.add(new AgriAdviceItemVo("空气温度", "气温处在适宜生长区间内。", "info"));
            }
        }

        if (hum != null)
        {
            if (hum.compareTo(HUMI_LOW) < 0)
            {
                items.add(new AgriAdviceItemVo("空气湿度",
                    "空气偏干（低于35%），叶面蒸腾与土壤失水加快，可微喷或覆盖保墒。", "warning"));
            }
            else if (hum.compareTo(HUMI_HIGH) > 0)
            {
                items.add(new AgriAdviceItemVo("空气湿度",
                    "湿度过高（高于85%），病害风险上升，建议加强通风、降低环境湿度。", "warning"));
            }
            else if (hum.compareTo(HUMI_OPT_LOW) >= 0 && hum.compareTo(HUMI_OPT_HIGH) <= 0)
            {
                items.add(new AgriAdviceItemVo("空气湿度", "空气湿度较为适宜。", "info"));
            }
        }

        if (lux != null)
        {
            if (lux < LIGHT_LOW)
            {
                items.add(new AgriAdviceItemVo("光照强度",
                    "当前光照偏弱（低于8000 lux），喜光作物可考虑补光或疏枝改善透光。", "warning"));
            }
            else if (lux < LIGHT_OPT)
            {
                items.add(new AgriAdviceItemVo("光照强度", "光照中等，结合通风与肥水管理即可。", "info"));
            }
            else
            {
                items.add(new AgriAdviceItemVo("光照强度", "光照充足，夏季注意日灼，高温时段可适当遮阴。", "info"));
            }
        }

        appendCropIntelligence(cropKind, soil, airT, items);

        if (outdoorAirTempC != null || outdoorRhPct != null)
        {
            appendOutdoorContext(outdoorAirTempC, outdoorRhPct, outdoorObservationTime, airT, items);
        }

        if (StringUtils.isNotEmpty(trendHint))
        {
            items.add(new AgriAdviceItemVo("趋势研判", trendHint, "info"));
        }

        if (StringUtils.isNotEmpty(vo.getCropType()))
        {
            items.add(new AgriAdviceItemVo("种植档案",
                "节点档案作物为「" + vo.getCropType() + "」，已按常见生理需求做规则化加权（非替代农艺专家现场判断）。", "info"));
        }

        if (items.isEmpty())
        {
            items.add(new AgriAdviceItemVo("综合", "暂无有效传感器数据，请确认设备已上报，或使用「模拟上报」生成演示数据。", "warning"));
        }

        sortAdviceItems(items);

        vo.setItems(items);
        int[] rw = recountSeverity(items);
        int risk = rw[0];
        int warn = rw[1];
        if (risk > 0)
        {
            vo.setOverallLevel("risk");
        }
        else if (warn > 0)
        {
            vo.setOverallLevel("warning");
        }
        else
        {
            vo.setOverallLevel("good");
        }

        int health = computeHealthScore(risk, warn);
        vo.setHealthScore(health);
        vo.setSmartSummary(buildSmartSummary(vo, cropKind, outdoorAirTempC, outdoorRhPct, risk, warn, health));
        return vo;
    }

    private void appendCropIntelligence(AgriCropKindEnum kind, BigDecimal soil, BigDecimal airT,
        List<AgriAdviceItemVo> items)
    {
        if (kind == AgriCropKindEnum.DEFAULT)
        {
            return;
        }
        CropOpt opt = cropOpt(kind);
        String name = cropKindLabel(kind);
        if (soil != null)
        {
            if (soil.compareTo(opt.soilMin) < 0)
            {
                items.add(new AgriAdviceItemVo("作物智能适配",
                    "按「" + name + "」常见管理，当前土湿低于适宜下限（约" + opt.soilMin + "%），优先检查灌溉与渗漏。", "warning"));
            }
            else if (soil.compareTo(opt.soilMax) > 0)
            {
                items.add(new AgriAdviceItemVo("作物智能适配",
                    "按「" + name + "」常见管理，当前土湿高于适宜上限（约" + opt.soilMax + "%），注意沥水与根系呼吸。", "warning"));
            }
        }
        if (airT != null)
        {
            if (airT.compareTo(opt.airMin) < 0)
            {
                items.add(new AgriAdviceItemVo("作物智能适配",
                    "「" + name + "」在当前气温下生长偏慢，注意保温与寒害；参考要点：" + opt.tip, "warning"));
            }
            else if (airT.compareTo(opt.airMax) > 0)
            {
                items.add(new AgriAdviceItemVo("作物智能适配",
                    "「" + name + "」处于偏高温区间，加强通风降温与蒸腾补偿；参考要点：" + opt.tip, "warning"));
            }
            else
            {
                items.add(new AgriAdviceItemVo("作物智能适配",
                    "气温与「" + name + "」常见适宜区间匹配度较好。" + opt.tip, "info"));
            }
        }
    }

    private void appendOutdoorContext(BigDecimal outdoorAirTempC, Integer outdoorRhPct, String outdoorObservationTime,
        BigDecimal fieldAirT, List<AgriAdviceItemVo> items)
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(outdoorObservationTime))
        {
            sb.append("参考气象站观测时间 ").append(outdoorObservationTime).append("。");
        }
        if (outdoorAirTempC != null)
        {
            sb.append(" 参考气温约 ").append(scale(outdoorAirTempC)).append("℃。");
        }
        if (outdoorRhPct != null)
        {
            sb.append(" 参考相对湿度约 ").append(outdoorRhPct).append("%。");
        }
        String severity = "info";
        if (fieldAirT != null && outdoorAirTempC != null)
        {
            BigDecimal diff = fieldAirT.subtract(outdoorAirTempC).abs();
            if (diff.compareTo(new BigDecimal("6")) > 0)
            {
                sb.append(" 田间气温与参考站差异较大，可能存在棚室效应、地形或探头位置影响，请交叉验证。");
                severity = "warning";
            }
            else
            {
                sb.append(" 田间与参考气温接近，小气候一致性较好。");
            }
        }
        if (outdoorAirTempC != null && outdoorAirTempC.compareTo(new BigDecimal("3")) < 0)
        {
            sb.append(" 参考站温度较低，留意辐射霜与叶面结露风险。");
            severity = "warning";
        }
        items.add(new AgriAdviceItemVo("公网气象融合", sb.toString().trim(), severity));
    }

    private static String buildSoilTrendHint(List<AgriSensorReading> rows, int windowHours)
    {
        if (rows == null || rows.size() < 4)
        {
            return null;
        }
        int n = rows.size();
        int mid = n / 2;
        BigDecimal early = avgSoilMoisture(rows, 0, mid);
        BigDecimal late = avgSoilMoisture(rows, mid, n);
        if (early == null || late == null)
        {
            return null;
        }
        BigDecimal diff = late.subtract(early);
        if (diff.compareTo(new BigDecimal("3")) > 0)
        {
            return "近" + windowHours + "小时土壤湿度时间序列整体抬升，注意沥水与病害预警。";
        }
        if (diff.compareTo(new BigDecimal("-3")) < 0)
        {
            return "近" + windowHours + "小时土壤湿度时间序列整体下降，灌溉需求可能上升。";
        }
        return "近" + windowHours + "小时土壤湿度波动较小，墒情相对稳定。";
    }

    private static BigDecimal avgSoilMoisture(List<AgriSensorReading> rows, int from, int to)
    {
        BigDecimal sum = BigDecimal.ZERO;
        int cnt = 0;
        for (int i = from; i < to; i++)
        {
            AgriSensorReading r = rows.get(i);
            if (r.getSoilMoisturePct() != null)
            {
                sum = sum.add(r.getSoilMoisturePct());
                cnt++;
            }
        }
        if (cnt == 0)
        {
            return null;
        }
        return sum.divide(BigDecimal.valueOf(cnt), 2, RoundingMode.HALF_UP);
    }

    private static int computeHealthScore(int risk, int warn)
    {
        int score = 100 - risk * 22 - warn * 9;
        if (score < 0)
        {
            return 0;
        }
        if (score > 100)
        {
            return 100;
        }
        return score;
    }

    private static int[] recountSeverity(List<AgriAdviceItemVo> items)
    {
        int risk = 0;
        int warn = 0;
        for (AgriAdviceItemVo it : items)
        {
            if ("danger".equals(it.getSeverity()))
            {
                risk++;
            }
            else if ("warning".equals(it.getSeverity()))
            {
                warn++;
            }
        }
        return new int[] { risk, warn };
    }

    private static void sortAdviceItems(List<AgriAdviceItemVo> items)
    {
        items.sort(Comparator.comparingInt(AgriSensorReadingServiceImpl::severityOrder));
    }

    private static int severityOrder(AgriAdviceItemVo it)
    {
        String s = it.getSeverity();
        if ("danger".equals(s))
        {
            return 0;
        }
        if ("warning".equals(s))
        {
            return 1;
        }
        return 2;
    }

    private static String buildSmartSummary(AgriGrowthAdviceVo vo, AgriCropKindEnum kind,
        BigDecimal outdoorAirTempC, Integer outdoorRhPct, int risk, int warn, int health)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("健康指数 ").append(health).append(" 分（规则引擎加权，非遥感反演）。");
        if ("risk".equals(vo.getOverallLevel()))
        {
            sb.append(" 当前存在需立即处置的高风险因子，请优先处理标红条目。");
        }
        else if ("warning".equals(vo.getOverallLevel()))
        {
            sb.append(" 存在若干需关注项，建议在24～48小时内安排巡田与校正水肥。");
        }
        else
        {
            sb.append(" 主要环境因子整体在可接受区间，可保持现有管理节奏。");
        }
        if (kind != AgriCropKindEnum.DEFAULT && StringUtils.isNotEmpty(vo.getCropType()))
        {
            sb.append(" 已按「").append(vo.getCropType()).append("」常见生理区间做适配提示。");
        }
        if (StringUtils.isNotEmpty(vo.getTrendHint()))
        {
            sb.append(" ").append(vo.getTrendHint());
        }
        if (outdoorAirTempC != null || outdoorRhPct != null)
        {
            sb.append(" 已融合公网参考气象，用于与田间探头对照。");
        }
        return sb.toString();
    }

    private static String cropKindLabel(AgriCropKindEnum k)
    {
        switch (k)
        {
            case RICE:
                return "水稻";
            case WHEAT:
                return "麦类";
            case CORN:
                return "玉米";
            case TOMATO:
                return "番茄";
            case CUCUMBER:
                return "黄瓜";
            case LEAFY:
                return "叶菜";
            default:
                return "通用";
        }
    }

    private static CropOpt cropOpt(AgriCropKindEnum k)
    {
        switch (k)
        {
            case RICE:
                return new CropOpt(bd(40), bd(70), bd(20), bd(30),
                    "分蘖至孕穗忌长期断水，晒田需看苗情与天气。");
            case WHEAT:
                return new CropOpt(bd(35), bd(60), bd(12), bd(22),
                    "拔节至灌浆警惕干热风与短暂根区干旱。");
            case CORN:
                return new CropOpt(bd(35), bd(65), bd(18), bd(30),
                    "大喇叭口至抽雄保证水分，花期高温注意辅助授粉。");
            case TOMATO:
                return new CropOpt(bd(40), bd(70), bd(18), bd(28),
                    "花果期控湿防病，夜温过高易徒长。");
            case CUCUMBER:
                return new CropOpt(bd(38), bd(72), bd(18), bd(30),
                    "喜湿但要通风降湿，防范霜霉、角斑等。");
            case LEAFY:
                return new CropOpt(bd(38), bd(68), bd(15), bd(25),
                    "速生阶段忌忽干忽湿，注意氮肥与硝态氮管理。");
            default:
                return new CropOpt(SOIL_OPT_LOW, SOIL_OPT_HIGH, TEMP_LOW, TEMP_HIGH,
                    "按通用水肥与植保规范管理即可。");
        }
    }

    private static BigDecimal bd(int v)
    {
        return BigDecimal.valueOf(v);
    }

    private static final class CropOpt
    {
        final BigDecimal soilMin;
        final BigDecimal soilMax;
        final BigDecimal airMin;
        final BigDecimal airMax;
        final String tip;

        CropOpt(BigDecimal soilMin, BigDecimal soilMax, BigDecimal airMin, BigDecimal airMax, String tip)
        {
            this.soilMin = soilMin;
            this.soilMax = soilMax;
            this.airMin = airMin;
            this.airMax = airMax;
            this.tip = tip;
        }
    }

    private static BigDecimal scale(BigDecimal v)
    {
        if (v == null)
        {
            return null;
        }
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
