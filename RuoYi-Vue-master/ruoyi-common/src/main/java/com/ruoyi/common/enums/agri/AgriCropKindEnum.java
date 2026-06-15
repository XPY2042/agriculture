package com.ruoyi.common.enums.agri;

import com.ruoyi.common.utils.StringUtils;

/**
 * Crop kind for growth-rule matching (thresholds applied in service layer).
 */
public enum AgriCropKindEnum
{
    DEFAULT,
    RICE,
    WHEAT,
    CORN,
    TOMATO,
    CUCUMBER,
    LEAFY;

    public static AgriCropKindEnum fromCropType(String cropType)
    {
        if (StringUtils.isEmpty(cropType))
        {
            return DEFAULT;
        }
        String t = cropType.trim();
        if (t.contains("ˮ��") || t.toLowerCase().contains("rice"))
        {
            return RICE;
        }
        if (t.contains("С��") || t.contains("����") || t.toLowerCase().contains("wheat"))
        {
            return WHEAT;
        }
        if (t.contains("����") || t.toLowerCase().contains("corn") || t.toLowerCase().contains("maize"))
        {
            return CORN;
        }
        if (t.contains("����") || t.contains("������") || t.toLowerCase().contains("tomato"))
        {
            return TOMATO;
        }
        if (t.contains("�ƹ�") || t.toLowerCase().contains("cucumber"))
        {
            return CUCUMBER;
        }
        if (t.contains("Ҷ��") || t.contains("����") || t.contains("����") || t.contains("�ײ�")
            || t.toLowerCase().contains("lettuce") || t.toLowerCase().contains("leafy"))
        {
            return LEAFY;
        }
        return DEFAULT;
    }
}
