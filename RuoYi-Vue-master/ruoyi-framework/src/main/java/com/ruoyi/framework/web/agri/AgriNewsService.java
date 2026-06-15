package com.ruoyi.framework.web.agri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ruoyi.common.domain.agri.AgriNewsArticle;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.agri.AgriRssCharsetUtils;
import com.ruoyi.common.utils.agri.AgriRssParser;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.config.properties.AgriNewsProperties;
import com.ruoyi.framework.web.outbound.NetworkOutboundService;

/**
 * ?????? RSS ??????
 */
@Service
public class AgriNewsService
{
    private static final Logger log = LoggerFactory.getLogger(AgriNewsService.class);

    private final AgriNewsProperties properties;
    private final NetworkOutboundService networkOutboundService;

    private final AtomicReference<List<AgriNewsArticle>> cache = new AtomicReference<>(Collections.emptyList());
    private volatile long cacheExpireAtMs;

    public AgriNewsService(AgriNewsProperties properties, NetworkOutboundService networkOutboundService)
    {
        this.properties = properties;
        this.networkOutboundService = networkOutboundService;
    }

    public List<AgriNewsArticle> listArticles(String titleKeyword, boolean refresh)
    {
        List<AgriNewsArticle> all = loadArticles(refresh);
        if (StringUtils.isEmpty(titleKeyword))
        {
            return all;
        }
        String kw = titleKeyword.trim().toLowerCase(Locale.ROOT);
        List<AgriNewsArticle> filtered = new ArrayList<>();
        for (AgriNewsArticle article : all)
        {
            if (article.getTitle() != null && article.getTitle().toLowerCase(Locale.ROOT).contains(kw))
            {
                filtered.add(article);
            }
        }
        return filtered;
    }

    public AgriNewsArticle getByArticleId(String articleId)
    {
        if (StringUtils.isEmpty(articleId))
        {
            return null;
        }
        for (AgriNewsArticle article : loadArticles(false))
        {
            if (articleId.equals(article.getArticleId()))
            {
                return article;
            }
        }
        return null;
    }

    public List<AgriNewsArticle> refresh()
    {
        return loadArticles(true);
    }

    private List<AgriNewsArticle> loadArticles(boolean refresh)
    {
        if (!properties.isEnabled())
        {
            throw new ServiceException("??????????????????");
        }
        long now = System.currentTimeMillis();
        if (!refresh && now < cacheExpireAtMs && !cache.get().isEmpty())
        {
            return cache.get();
        }
        synchronized (this)
        {
            now = System.currentTimeMillis();
            if (!refresh && now < cacheExpireAtMs && !cache.get().isEmpty())
            {
                return cache.get();
            }
            List<AgriNewsArticle> merged = fetchAllFeeds();
            merged.sort(Comparator.comparing(AgriNewsArticle::getPublishTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
            cache.set(Collections.unmodifiableList(merged));
            cacheExpireAtMs = now + Math.max(1, properties.getCacheMinutes()) * 60_000L;
            return cache.get();
        }
    }

    private List<AgriNewsArticle> fetchAllFeeds()
    {
        List<AgriNewsArticle> merged = new ArrayList<>();
        List<AgriNewsProperties.Feed> feeds = properties.getFeeds();
        if (feeds == null || feeds.isEmpty())
        {
            return merged;
        }
        for (AgriNewsProperties.Feed feed : feeds)
        {
            if (feed == null || StringUtils.isEmpty(feed.getUrl()))
            {
                continue;
            }
            try
            {
                String xml = fetchRss(feed.getUrl());
                if (StringUtils.isNotEmpty(xml))
                {
                    merged.addAll(AgriRssParser.parse(xml, feed.getName()));
                }
            }
            catch (Exception e)
            {
                log.warn("agri news feed failed: {} {}", feed.getName(), feed.getUrl(), e);
            }
        }
        if (merged.isEmpty())
        {
            throw new ServiceException("???????????????????????????????????");
        }
        return merged;
    }

    private String fetchRss(String url)
    {
        try
        {
            return networkOutboundService.httpGetText(url).getBody();
        }
        catch (ServiceException ex)
        {
            log.warn("network fetch failed, fallback HttpUtils: {}", url);
            byte[] raw = HttpUtils.sendGetBytes(url);
            return AgriRssCharsetUtils.decodeXmlBytes(raw);
        }
    }
}
