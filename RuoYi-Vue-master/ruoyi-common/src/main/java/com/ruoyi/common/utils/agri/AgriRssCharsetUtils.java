package com.ruoyi.common.utils.agri;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.ruoyi.common.utils.StringUtils;

/**
 * RSS / XML response charset detection and decode.
 */
public final class AgriRssCharsetUtils
{
    private static final Pattern XML_ENCODING = Pattern.compile(
        "encoding\\s*=\\s*[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    private AgriRssCharsetUtils()
    {
    }

    public static String decodeXmlBytes(byte[] raw)
    {
        return decodeXmlBytes(raw, null);
    }

    public static String decodeXmlBytes(byte[] raw, String contentTypeCharset)
    {
        if (raw == null || raw.length == 0)
        {
            return "";
        }
        Charset charset = resolveCharset(raw, contentTypeCharset);
        String decoded = new String(raw, charset);
        if (shouldTryGbkFallback(charset, raw, decoded))
        {
            String gbkDecoded = new String(raw, Charset.forName("GBK"));
            if (cjkScore(gbkDecoded) > cjkScore(decoded))
            {
                return gbkDecoded;
            }
        }
        return decoded;
    }

    /** Many Chinese RSS feeds misdeclare UTF-8 but send GBK bytes. */
    private static boolean shouldTryGbkFallback(Charset used, byte[] raw, String decoded)
    {
        String name = used.name();
        if ("GBK".equalsIgnoreCase(name) || "GB2312".equalsIgnoreCase(name)
            || "GB18030".equalsIgnoreCase(name))
        {
            return false;
        }
        if (!hasHighBytes(raw))
        {
            return false;
        }
        return cjkScore(decoded) < 8;
    }

    private static boolean hasHighBytes(byte[] raw)
    {
        for (byte b : raw)
        {
            if ((b & 0xFF) > 0x7F)
            {
                return true;
            }
        }
        return false;
    }

    private static int cjkScore(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return 0;
        }
        int score = 0;
        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c >= 0x4E00 && c <= 0x9FFF)
            {
                score++;
            }
        }
        return score;
    }

    private static Charset resolveCharset(byte[] raw, String contentTypeCharset)
    {
        if (StringUtils.isNotEmpty(contentTypeCharset))
        {
            try
            {
                return Charset.forName(contentTypeCharset.trim());
            }
            catch (Exception ignored)
            {
                // fall through
            }
        }
        String head = new String(raw, 0, Math.min(raw.length, 512), StandardCharsets.ISO_8859_1);
        Matcher matcher = XML_ENCODING.matcher(head);
        if (matcher.find())
        {
            try
            {
                return Charset.forName(matcher.group(1).trim());
            }
            catch (Exception ignored)
            {
                // fall through
            }
        }
        return StandardCharsets.UTF_8;
    }
}
