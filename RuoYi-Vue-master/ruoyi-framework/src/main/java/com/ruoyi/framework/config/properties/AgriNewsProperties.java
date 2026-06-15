package com.ruoyi.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ũҵ���� RSS ץȡ���ã�ruoyi.agri.news.*��
 */
@Component
@ConfigurationProperties(prefix = "ruoyi.agri.news")
public class AgriNewsProperties
{
    private boolean enabled = true;

    /** ����ʱ�������ӣ� */
    private int cacheMinutes = 15;

    private List<Feed> feeds = defaultFeeds();

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public int getCacheMinutes()
    {
        return cacheMinutes;
    }

    public void setCacheMinutes(int cacheMinutes)
    {
        this.cacheMinutes = cacheMinutes;
    }

    public List<Feed> getFeeds()
    {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds)
    {
        this.feeds = feeds;
    }

    private static List<Feed> defaultFeeds()
    {
        List<Feed> list = new ArrayList<>(2);
        Feed feed1 = new Feed();
        feed1.setName("�й����Ϲ�ҵ��Ϣ��");
        feed1.setUrl("http://www.chinafeed.com.cn/feed/rss.php?mid=100");
        list.add(feed1);
        Feed feed2 = new Feed();
        feed2.setName("�й�ũҵ��");
        feed2.setUrl("https://www.zgny.com/feed/rss.php?mid=21");
        list.add(feed2);
        return list;
    }

    public static class Feed
    {
        private String name;

        private String url;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }
    }
}
