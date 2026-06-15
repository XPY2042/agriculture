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

    private AgriThresholdChecker()
    {
    }

    public static List<AgriAlarmCandidate> evaluate(BigDecimal soilMoisturePct, BigDecimal airTempC,
        BigDecimal airHumidityPct, Integer lightLux, BigDecimal soilTempC)
    {
        List<AgriAlarmCandidate> list = new ArrayList<>();
        checkSoilMoisture(soilMoisturePct, list);
        checkAirTemp(airTempC, list);
        checkAirHumidity(airHumidityPct, list);
        checkLight(lightLux, list);
        checkSoilTemp(soilTempC, list);
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
        if (v == null)
        {
            return;
        }
        String fv = fmt(v) + DEG;
        if (v.compareTo(SOIL_TEMP_LOW) < 0)
        {
            list.add(candidate("soil_temp", M_SOIL_T, "2", "low", fv, "<" + SOIL_TEMP_LOW + DEG,
                M_SOIL_T + "\u8fc7\u4f4e\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u4fdd\u6e29\u3002"));
        }
        else if (v.compareTo(SOIL_TEMP_HIGH) > 0)
        {
            list.add(candidate("soil_temp", M_SOIL_T, "2", "high", fv, ">" + SOIL_TEMP_HIGH + DEG,
                M_SOIL_T + "\u8fc7\u9ad8\uff08" + fv + "\uff09\uff0c\u6ce8\u610f\u964d\u6e29\u4e0e\u704c\u6e89\u3002"));
        }
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
