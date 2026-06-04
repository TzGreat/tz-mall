import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/user/Home.vue'),
    meta: { title: '首页 - TZ-Mall' }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/views/user/ProductList.vue'),
    meta: { title: '商品列表 - TZ-Mall' }
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('@/views/user/ProductDetail.vue'),
    meta: { title: '商品详情 - TZ-Mall' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/user/Cart.vue'),
    meta: { title: '购物车 - TZ-Mall' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心 - TZ-Mall' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/user/Orders.vue'),
    meta: { title: '我的订单 - TZ-Mall' }
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('@/views/user/OrderDetail.vue'),
    meta: { title: '订单详情 - TZ-Mall' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录 - TZ-Mall' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册 - TZ-Mall' }
  },
  {
    path: '/admin',
    name: 'AdminHome',
    meta: { title: '管理后台 - TZ-Mall' },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘 - TZ-Mall' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理 - TZ-Mall' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue'),
        meta: { title: '商品管理 - TZ-Mall' }
      },
      {
        path: 'products/add',
        name: 'AdminAddProduct',
        component: () => import('@/views/admin/ProductForm.vue'),
        meta: { title: '添加商品 - TZ-Mall' }
      },
      {
        path: 'products/:id/edit',
        name: 'AdminEditProduct',
        component: () => import('@/views/admin/ProductForm.vue'),
        meta: { title: '编辑商品 - TZ-Mall' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理 - TZ-Mall' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

export default router
