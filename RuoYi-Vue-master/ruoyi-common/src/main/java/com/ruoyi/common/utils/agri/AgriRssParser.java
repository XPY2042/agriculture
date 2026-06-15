package com.ruoyi.common.utils.agri;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.ruoyi.common.domain.agri.AgriNewsArticle;
import com.ruoyi.common.utils.agri.AgriRssCharsetUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.Md5Utils;

/**
 * RSS 2.0 ???????????????
 */
public final class AgriRssParser
{
    private AgriRssParser()
    {
    }

    public static List<AgriNewsArticle> parse(byte[] raw, String sourceName)
    {
        if (raw == null || raw.length == 0)
        {
            return new ArrayList<>();
        }
        return parse(AgriRssCharsetUtils.decodeXmlBytes(raw), sourceName);
    }

    public static List<AgriNewsArticle> parse(String xml, String sourceName)
    {
        List<AgriNewsArticle> list = new ArrayList<>();
        if (StringUtils.isEmpty(xml))
        {
            return list;
        }
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(
                xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++)
            {
                Node node = items.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                {
                    continue;
                }
                Element item = (Element) node;
                String title = unwrapCdata(textOf(item, "title"));
                String link = textOf(item, "link");
                if (StringUtils.isEmpty(title) || StringUtils.isEmpty(link))
                {
                    continue;
                }
                AgriNewsArticle article = new AgriNewsArticle();
                article.setTitle(title.trim());
                article.setLink(link.trim());
                article.setSummary(stripHtml(unwrapCdata(textOf(item, "description"))));
                article.setPublishTime(textOf(item, "pubDate"));
                article.setSource(StringUtils.isNotEmpty(sourceName) ? sourceName : textOfChannel(doc, "title"));
                article.setArticleId(Md5Utils.hash(link.trim()));
                list.add(article);
            }
        }
        catch (Exception ignored)
        {
            // ???????????????????????????????
        }
        return list;
    }

    private static String textOfChannel(Document doc, String tag)
    {
        NodeList channels = doc.getElementsByTagName("channel");
        if (channels.getLength() == 0)
        {
            return "";
        }
        Node channel = channels.item(0);
        if (channel.getNodeType() != Node.ELEMENT_NODE)
        {
            return "";
        }
        return textOf((Element) channel, tag);
    }

    private static String textOf(Element parent, String tag)
    {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0)
        {
            return "";
        }
        Node node = nodes.item(0);
        return node == null ? "" : node.getTextContent();
    }

    private static String unwrapCdata(String value)
    {
        if (value == null)
        {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("<![CDATA[") && trimmed.endsWith("]]>"))
        {
            return trimmed.substring(9, trimmed.length() - 3).trim();
        }
        return trimmed;
    }

    private static String stripHtml(String html)
    {
        if (StringUtils.isEmpty(html))
        {
            return "";
        }
        String text = html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        if (text.length() > 300)
        {
            return text.substring(0, 300) + "...";
        }
        return text;
    }
}
