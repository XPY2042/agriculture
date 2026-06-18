package com.ruoyi.common.utils.agri;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Agricultural sensor threshold checks (aligned with growth advice rules).
 */
public final class AgriThresholdChecker
{
    private static final String M_SOIL = "\u571f\u58e4\u6e7f\u5ea6";
    private static final String M_AIR_T = "\u7a7a\u6c14\u6e29\u5ea6";
    private static final String M_AIR_H = "\u7a7a\u6c14\u6e7f\u5ea6";
    private static final String M_LIGHT = "\u5149\u7167\u5f3a\u5ea6";
    private static final String M_SOIL_T = "\u571f\u58e4\u6e29\u5ea6";
    private static final String M_SOIL_PH = "\u571f\u58e4pH";
    private static final String M_CO2 = "CO2\u6d53\u5ea6";
    private static final String M_WATER_PH = "\u6c34\u4f53pH";
    private static final String DEG = "\u2103";

    private static final BigDecimal SOIL_LOW = new BigDecimal("25");
    private static final BigDecimal SOIL_WARN_LOW = new BigDecimal("35");
    private static final BigDecimal SOIL_WARN_HIGH = new BigDecimal("65");
    private static final BigDecimal SOIL_HIGH = new BigDecimal("75");

    private static final BigDecimal TEMP_COLD = new BigDecimal("12");
    private static final BigDecimal TEMP_LOW = new BigDecimal("18");
    private static final BigDecimal TEMP_HIGH = new BigDecimal("28");
    private static final BigDecimal TEMP_HOT = new BigDecimal("32");

    private static final BigDecimal HUMI_LOW = new BigDecimal("35");
    private static final BigDecimal HUMI_HIGH = new BigDecimal("85");

    private static final BigDecimal SOIL_TEMP_LOW = new BigDecimal("8");
    private static final BigDecimal SOIL_TEMP_HIGH = new BigDecimal("32");

    private static final int LIGHT_LOW = 8000;

    /* \u65b0\u589e\u6307\u6807\u9608\u503c */
    private static final BigDecimal SOIL_PH_LOW  = new BigDecimal("5.0");
    private static final BigDecimal SOIL_PH_HIGH = new BigDecimal("8.0");
    private static final BigDecimal CO2_LOW  = new BigDecimal("300");
    private static final BigDecimal CO2_HIGH = new BigDecimal("1500");
    private static final BigDecimal WATER_PH_LOW  = new BigDecimal("6.0");
    private static final BigDecimal WATER_PH_HIGH = new BigDecimal("8.5");

    private AgriThresholdChecker()
    {
    }

    public static List<AgriAlarmCandidate> evaluate(BigDecimal soilMoisturePct, BigDecimal airTempC,
        BigDecimal airHumidityPct, Integer lightLux, BigDecimal soilTempC,
        BigDecimal soilPh, Integer co2Ppm, BigDecimal waterPh)
    {
        List<AgriAlarmCandidate> list = new ArrayList<>();
        checkSoilMoisture(soilMoisturePct, list);
        checkAirTemp(airTempC, list);
        checkAirHumidity(airHumidityPct, list);
        checkLight(lightLux, list);
        checkSoilTemp(soilTempC, list);
        checkSoilPh(soilPh, list);
        checkCo2(co2Ppm, list);
        checkWaterPh(waterPh, list);
        return list;
    }

    private static void checkSoilMoisture(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null)
        {
            return;
        }
        String fv = fmt(v) + "%";
        if (v.compareTo(SOIL_LOW) < 0)
        {
            list.add(candidate("soil_moisture", M_SOIL, "1", "low", fv, "<" + SOIL_LOW + "%",
                M_SOIL + "\u8fc7\u4f4e\uff08" + fv + "\uff0c\u9608\u503c\u4f4e\u4e8e " + SOIL_LOW
                    + "%\uff09\uff0c\u8bf7\u5c3d\u5feb\u704c\u6e89\u8865\u6c34\u3002"));
        }
        else if (v.compareTo(SOIL_WARN_LOW) < 0)
        {
            list.add(candidate("soil_moisture", M_SOIL, "2", "low", fv, "<" + SOIL_WARN_LOW + "%",
                M_SOIL + "\u504f\u4f4e\uff08" + fv + "\uff09\uff0c\u5efa\u8bae\u5173\u6ce8\u5e76\u9002\u65f6\u8865\u6c34\u3002"));
        }
        else if (v.compareTo(SOIL_HIGH) > 0)
        {
            list.add(candidate("soil_moisture", M_SOIL, "2", "high", fv, ">" + SOIL_HIGH + "%",
                M_SOIL + "\u8fc7\u9ad8\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u6392\u6c34\u901a\u98ce\uff0c\u9632\u8303\u6839\u7cfb\u7f3a\u6c27\u3002"));
        }
        else if (v.compareTo(SOIL_WARN_HIGH) > 0)
        {
            list.add(candidate("soil_moisture", M_SOIL, "2", "high", fv, ">" + SOIL_WARN_HIGH + "%",
                M_SOIL + "\u504f\u9ad8\uff08" + fv + "\uff09\uff0c\u5efa\u8bae\u52a0\u5f3a\u6ca5\u6c34\u3002"));
        }
    }

    private static void checkAirTemp(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null)
        {
            return;
        }
        String fv = fmt(v) + DEG;
        if (v.compareTo(TEMP_COLD) < 0)
        {
            list.add(candidate("air_temp", M_AIR_T, "1", "low", fv, "<" + TEMP_COLD + DEG,
                "\u6c14\u6e29\u8fc7\u4f4e\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u9632\u5bd2\u4fdd\u6e29\u3002"));
        }
        else if (v.compareTo(TEMP_LOW) < 0)
        {
            list.add(candidate("air_temp", M_AIR_T, "2", "low", fv, "<" + TEMP_LOW + DEG,
                "\u6c14\u6e29\u504f\u4f4e\uff08" + fv + "\uff09\uff0c\u5173\u6ce8\u591c\u95f4\u4f4e\u6e29\u5f71\u54cd\u3002"));
        }
        else if (v.compareTo(TEMP_HOT) > 0)
        {
            list.add(candidate("air_temp", M_AIR_T, "1", "high", fv, ">" + TEMP_HOT + DEG,
                "\u6c14\u6e29\u9177\u70ed\uff08" + fv + "\uff09\uff0c\u52a0\u5f3a\u906e\u9634\u964d\u6e29\uff0c\u9632\u8303\u65e5\u70c1\u3002"));
        }
        else if (v.compareTo(TEMP_HIGH) > 0)
        {
            list.add(candidate("air_temp", M_AIR_T, "2", "high", fv, ">" + TEMP_HIGH + DEG,
                "\u6c14\u6e29\u504f\u9ad8\uff08" + fv + "\uff09\uff0c\u5efa\u8bae\u589e\u5f3a\u901a\u98ce\u6216\u77ed\u65f6\u906e\u9633\u3002"));
        }
    }

    private static void checkAirHumidity(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null)
        {
            return;
        }
        String fv = fmt(v) + "%";
        if (v.compareTo(HUMI_LOW) < 0)
        {
            list.add(candidate("air_humidity", M_AIR_H, "2", "low", fv, "<" + HUMI_LOW + "%",
                M_AIR_H + "\u8fc7\u4f4e\uff08" + fv + "\uff09\uff0c\u53f6\u9762\u84b8\u817e\u52a0\u5feb\uff0c\u53ef\u5fae\u55b7\u6216\u8986\u76d6\u4fdd\u5892\u3002"));
        }
        else if (v.compareTo(HUMI_HIGH) > 0)
        {
            list.add(candidate("air_humidity", M_AIR_H, "2", "high", fv, ">" + HUMI_HIGH + "%",
                M_AIR_H + "\u8fc7\u9ad8\uff08" + fv + "\uff09\uff0c\u75c5\u5bb3\u98ce\u9669\u4e0a\u5347\uff0c\u8bf7\u52a0\u5f3a\u901a\u98ce\u964d\u6e7f\u3002"));
        }
    }

    private static void checkLight(Integer v, List<AgriAlarmCandidate> list)
    {
        if (v == null)
        {
            return;
        }
        if (v < LIGHT_LOW)
        {
            list.add(candidate("light", M_LIGHT, "2", "low", v + " lux", "<" + LIGHT_LOW + " lux",
                "\u5149\u7167\u504f\u5f31\uff08" + v + " lux\uff09\uff0c\u559c\u5149\u4f5c\u7269\u53ef\u8003\u8651\u8865\u5149\u6216\u6539\u5584\u900f\u5149\u3002"));
        }
    }

    private static void checkSoilTemp(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null) return;
        String fv = fmt(v) + DEG;
        if (v.compareTo(SOIL_TEMP_LOW) < 0)
            list.add(candidate("soil_temp", M_SOIL_T, "2", "low", fv, "<" + SOIL_TEMP_LOW + DEG,
                M_SOIL_T + "\u8fc7\u4f4e\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u4fdd\u6e29\u3002"));
        else if (v.compareTo(SOIL_TEMP_HIGH) > 0)
            list.add(candidate("soil_temp", M_SOIL_T, "2", "high", fv, ">" + SOIL_TEMP_HIGH + DEG,
                M_SOIL_T + "\u8fc7\u9ad8\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u964d\u6e29\u4e0e\u704c\u6e89\u3002"));
    }

    /** \u571f\u58e4pH \u2014 \u591a\u6570\u4f5c\u7269\u9002\u5b9c5.0~8.0\uff0c\u8d85\u51fa\u8b66\u544a */
    private static void checkSoilPh(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null) return;
        String fv = fmt(v);
        if (v.compareTo(SOIL_PH_LOW) < 0)
            list.add(candidate("soil_ph", M_SOIL_PH, "1", "low", fv, "<" + SOIL_PH_LOW,
                M_SOIL_PH + "\u8fc7\u9178\uff08" + fv + "\uff09\uff0c\u53ef\u80fd\u94dd\u9530\u6bd2\u5bb3\uff0c\u5efa\u8bae\u65bd\u7528\u77f3\u7070\u6216\u8349\u6728\u7070\u4e2d\u548c\u3002"));
        else if (v.compareTo(SOIL_PH_HIGH) > 0)
            list.add(candidate("soil_ph", M_SOIL_PH, "1", "high", fv, ">" + SOIL_PH_HIGH,
                M_SOIL_PH + "\u8fc7\u78b1\uff08" + fv + "\uff09\uff0c\u5fae\u91cf\u5143\u7d20\u6709\u6548\u6027\u4e0b\u964d\uff0c\u5efa\u8bae\u589e\u65bd\u6709\u673a\u80a5\u6216\u786b\u78fa\u6539\u826f\u3002"));
    }

    /** CO2\u6d53\u5ea6 \u2014 300~1500ppm\u6b63\u5e38\uff0c\u4f4e\u5f71\u54cd\u5149\u5408\u4f5c\u7528\uff0c\u9ad8\u63d0\u793a\u901a\u98ce\u4e0d\u8db3 */
    private static void checkCo2(Integer v, List<AgriAlarmCandidate> list)
    {
        if (v == null) return;
        String fv = v + " ppm";
        if (BigDecimal.valueOf(v).compareTo(CO2_LOW) < 0)
            list.add(candidate("co2", M_CO2, "2", "low", fv, "<" + CO2_LOW + " ppm",
                M_CO2 + "\u504f\u4f4e\uff08" + fv + "\uff09\uff0c\u5149\u5408\u6548\u7387\u53d7\u9650\uff0c\u5efa\u8bae\u589e\u65bdCO2\u6c14\u80a5\u6216\u52a0\u5f3a\u901a\u98ce\u3002"));
        else if (BigDecimal.valueOf(v).compareTo(CO2_HIGH) > 0)
            list.add(candidate("co2", M_CO2, "2", "high", fv, ">" + CO2_HIGH + " ppm",
                M_CO2 + "\u504f\u9ad8\uff08" + fv + "\uff09\uff0c\u901a\u98ce\u4e0d\u8db3\uff0c\u6ce8\u610f\u4eba\u5458\u5b89\u5168\u5e76\u53ca\u65f6\u6362\u6c14\u3002"));
    }

    /** \u6c34\u4f53pH \u2014 \u704c\u6e89\u6c34pH 6.0~8.5\uff0c\u8d85\u51fa\u5f71\u54cd\u517b\u5206\u5438\u6536 */
    private static void checkWaterPh(BigDecimal v, List<AgriAlarmCandidate> list)
    {
        if (v == null) return;
        String fv = fmt(v);
        if (v.compareTo(WATER_PH_LOW) < 0)
            list.add(candidate("water_ph", M_WATER_PH, "2", "low", fv, "<" + WATER_PH_LOW,
                M_WATER_PH + "\u504f\u9178\uff08" + fv + "\uff09\uff0c\u957f\u671f\u704c\u6e89\u53ef\u80fd\u9178\u5316\u571f\u58e4\uff0c\u5efa\u8bae\u8c03\u8282\u6c34\u6e90pH\u3002"));
        else if (v.compareTo(WATER_PH_HIGH) > 0)
            list.add(candidate("water_ph", M_WATER_PH, "2", "high", fv, ">" + WATER_PH_HIGH,
                M_WATER_PH + "\u504f\u78b1\uff08" + fv + "\uff09\uff0c\u957f\u671f\u704c\u6e89\u53ef\u80fd\u78b1\u5316\u571f\u58e4\uff0c\u5efa\u8bae\u4e2d\u548c\u5904\u7406\u540e\u4f7f\u7528\u3002"));
    }

    private static AgriAlarmCandidate candidate(String code, String name, String level, String dir,
        String actual, String threshold, String msg)
    {
        AgriAlarmCandidate c = new AgriAlarmCandidate();
        c.setMetricCode(code);
        c.setMetricName(name);
        c.setAlarmLevel(level);
        c.setDirection(dir);
        c.setActualValue(actual);
        c.setThresholdValue(threshold);
        c.setAlarmMessage(msg);
        return c;
    }

    private static String fmt(BigDecimal v)
    {
        return v.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }
}
