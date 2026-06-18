<template>
  <div class="app-container agri-news-page">
    <el-alert
      :title="t.pageTip"
      type="info"
      :closable="false"
      show-icon
      class="mb8"
    />

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item :label="t.titleLabel" prop="title">
        <el-input
          v-model="queryParams.title"
          :placeholder="t.titlePlaceholder"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">{{ t.search }}</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">{{ t.reset }}</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          :loading="refreshing"
          @click="handleRefresh"
          v-hasPermi="['agri:news:refresh', 'agri:monitor:view']"
        >{{ t.refresh }}</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-tag type="success" effect="plain" size="medium">{{ t.totalPrefix }} {{ total }} {{ t.totalSuffix }}</el-tag>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" class="mb8" />

    <div v-loading="loading" class="news-list">
      <el-empty v-if="!loading && !loadError && newsList.length === 0" :description="t.empty" />
      <el-card
        v-for="item in newsList"
        :key="item.articleId"
        shadow="hover"
        class="news-card"
        @click.native="openDetail(item)"
      >
        <div class="news-card__head">
          <h3 class="news-card__title">{{ item.title }}</h3>
          <el-tag size="mini" type="info" effect="plain">{{ item.source || t.defaultSource }}</el-tag>
        </div>
        <p class="news-card__summary">{{ item.summary || t.noSummary }}</p>
        <div class="news-card__foot">
          <span><i class="el-icon-time" /> {{ item.publishTime || t.unknownTime }}</span>
          <el-button type="text" size="mini" @click.stop="openOriginal(item.link)">{{ t.readOriginal }}</el-button>
        </div>
      </el-card>
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="detail.title" :visible.sync="detailOpen" width="720px" append-to-body>
      <div class="news-detail">
        <div class="news-detail__meta">
          <el-tag size="mini">{{ detail.source }}</el-tag>
          <span class="news-detail__time">{{ detail.publishTime }}</span>
        </div>
        <p class="news-detail__summary">{{ detail.summary || t.noSummary }}</p>
        <div class="news-detail__actions">
          <el-button type="primary" size="small" @click="openOriginal(detail.link)">{{ t.openOriginal }}</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import labelsZh from './labels-zh'
import { listAgriNews, getAgriNews, refreshAgriNews } from '@/api/agri/news'

export default {
  name: 'AgriNewsFeed',
  data() {
    return {
      t: labelsZh,
      loading: false,
      refreshing: false,
      showSearch: true,
      loadError: '',
      total: 0,
      newsList: [],
      detailOpen: false,
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      this.loadError = ''
      listAgriNews(this.queryParams).then(res => {
        this.newsList = res.rows || []
        this.total = res.total || 0
      }).catch(err => {
        this.newsList = []
        this.total = 0
        this.loadError = (err && err.message) || this.t.loadFail
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleRefresh() {
      this.refreshing = true
      refreshAgriNews().then(res => {
        this.$modal.msgSuccess(res.msg || this.t.refreshOk)
        this.queryParams.pageNum = 1
        return listAgriNews({ ...this.queryParams, refresh: true })
      }).then(res => {
        if (res) {
          this.newsList = res.rows || []
          this.total = res.total || 0
          this.loadError = ''
        }
      }).catch(() => {
        this.loadError = this.t.refreshFail
      }).finally(() => {
        this.refreshing = false
      })
    },
    openDetail(item) {
      getAgriNews(item.articleId).then(res => {
        this.detail = res.data || item
        this.detailOpen = true
      })
    },
    openOriginal(link) {
      if (!link) {
        this.$modal.msgWarning(this.t.linkUnavailable)
        return
      }
      window.open(link, '_blank', 'noopener,noreferrer')
    }
  }
}
</script>

<style scoped lang="scss">
.agri-news-page {
  .news-list {
    min-height: 200px;
  }

  .news-card {
    margin-bottom: 12px;
    cursor: pointer;
    transition: transform 0.15s ease;

    &:hover {
      transform: translateY(-2px);
    }

    &__head {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 12px;
      margin-bottom: 8px;
    }

    &__title {
      margin: 0;
      font-size: 16px;
      line-height: 1.5;
      color: #303133;
      flex: 1;
    }

    &__summary {
      margin: 0 0 10px;
      color: #606266;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    &__foot {
      display: flex;
      align-items: center;
      justify-content: space-between;
      color: #909399;
      font-size: 12px;
    }
  }

  .news-detail {
    &__meta {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
    }

    &__time {
      color: #909399;
      font-size: 13px;
    }

    &__summary {
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
    }

    &__actions {
      margin-top: 20px;
      text-align: right;
    }
  }
}
</style>
