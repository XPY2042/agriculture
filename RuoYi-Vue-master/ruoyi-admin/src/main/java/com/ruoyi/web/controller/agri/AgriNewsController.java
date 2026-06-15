package com.ruoyi.web.controller.agri;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.domain.agri.AgriNewsArticle;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.web.agri.AgriNewsService;

/**
 * ???????RSS ????
 */
@RestController
@RequestMapping("/agri/news")
public class AgriNewsController extends BaseController
{
    @Autowired
    private AgriNewsService agriNewsService;

    @PreAuthorize("@ss.hasPermi('agri:news:list') or @ss.hasPermi('agri:monitor:view')")
    @GetMapping("/list")
    public TableDataInfo list(
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "refresh", defaultValue = "false") boolean refresh)
    {
        List<AgriNewsArticle> all = agriNewsService.listArticles(title, refresh);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        int pageSize = pageDomain.getPageSize() == null ? 10 : pageDomain.getPageSize();
        int from = Math.max(0, (pageNum - 1) * pageSize);
        int to = Math.min(all.size(), from + pageSize);
        List<AgriNewsArticle> page = from >= all.size() ? List.of() : all.subList(from, to);
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("??????");
        data.setRows(page);
        data.setTotal(all.size());
        return data;
    }

    @PreAuthorize("@ss.hasPermi('agri:news:list') or @ss.hasPermi('agri:monitor:view')")
    @GetMapping("/{articleId}")
    public AjaxResult getInfo(@PathVariable String articleId)
    {
        AgriNewsArticle article = agriNewsService.getByArticleId(articleId);
        if (article == null)
        {
            return error("???????????????????????????");
        }
        return success(article);
    }

    @PreAuthorize("@ss.hasPermi('agri:news:refresh') or @ss.hasPermi('agri:monitor:view')")
    @Log(title = "ũҵ����", businessType = BusinessType.OTHER)
    @PutMapping("/refresh")
    public AjaxResult refresh()
    {
        List<AgriNewsArticle> list = agriNewsService.refresh();
        return success("?????????? " + list.size() + " ??????");
    }
}
