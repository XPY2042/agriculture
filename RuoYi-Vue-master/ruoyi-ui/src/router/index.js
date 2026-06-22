import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'index',
    children: [
      {
        path: 'index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由（用户管理主路由 + 隐藏子页）
export const dynamicRoutes = [
  {
    path: '/system/user',
    component: Layout,
    hidden: true,
    permissions: ['system:user:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/system/user/index'),
        name: 'SystemUser',
        meta: { title: '用户管理', icon: 'user', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/agriFlash',
    component: Layout,
    hidden: true,
    permissions: ['agri:news:list', 'agri:monitor:view'],
    children: [
      {
        path: 'newsFeed',
        component: () => import('@/views/agri/news/index'),
        name: 'AgriNewsFeed',
        meta: { title: '农业新闻', activeMenu: '/agriFlash/newsFeed' }
      }
    ]
  },
  // 维修人员 - 工单池
  {
    path: '/repair/technician/ticket-pool',
    component: Layout,
    hidden: true,
    permissions: ['repair:tech:pool'],
    children: [
      {
        path: '',
        component: () => import('@/views/repair/technician/ticket-pool/index'),
        name: 'RepairTicketPool',
        meta: { title: '工单池', activeMenu: '/repair/technician/ticket-pool' }
      }
    ]
  },
  // 维修人员 - 我的工单
  {
    path: '/repair/technician/my-tickets',
    component: Layout,
    hidden: true,
    permissions: ['repair:tech:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/repair/technician/my-tickets/index'),
        name: 'RepairMyTickets',
        meta: { title: '我的工单', activeMenu: '/repair/technician/my-tickets' }
      }
    ]
  },
  // 维修人员 - 维修记录
  {
    path: '/repair/technician/history',
    component: Layout,
    hidden: true,
    permissions: ['repair:tech:history'],
    children: [
      {
        path: '',
        component: () => import('@/views/repair/technician/history/index'),
        name: 'RepairHistory',
        meta: { title: '维修记录', activeMenu: '/repair/technician/history' }
      }
    ]
  },
  // 维修统计
  {
    path: '/repair/statistics',
    component: Layout,
    hidden: true,
    permissions: ['repair:admin:list', 'repair:tech:list'],
    children: [
      {
        path: '',
        component: () => import('@/views/repair/statistics/index'),
        name: 'RepairStatistics',
        meta: { title: '维修统计', activeMenu: '/repair/statistics' }
      }
    ]
  },
  {
    path: '/report/statistics',
    component: Layout,
    hidden: true,
    permissions: ['report:statistics:view', 'repair:admin:list', 'repair:tech:list', 'agri:monitor:view'],
    children: [
      {
        path: '',
        component: () => import('@/views/report/statistics/index'),
        name: 'ReportStatisticsDirect',
        meta: { title: '报表统计', activeMenu: '/agri/reportStatistics' }
      }
    ]
  }
]

// 防止连续点击多次路由报错
let routerPush = Router.prototype.push
let routerReplace = Router.prototype.replace
// push
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(err => err)
}
// replace
Router.prototype.replace = function push(location) {
  return routerReplace.call(this, location).catch(err => err)
}

export default new Router({
  mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
