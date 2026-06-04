# TZ-Mall 前端项目

基于 Vue 3 + TypeScript + Vite 的轻奢电商平台前端项目。

## 技术栈

- Vue 3 (Composition API)
- TypeScript
- Vite
- Vue Router
- Pinia
- Tailwind CSS
- Axios

## 功能特性

### 用户端
- 首页：轮播展示、分类导航、推荐商品
- 商品列表：筛选、排序
- 商品详情：商品信息、加入购物车
- 购物车：商品管理、数量调整
- 订单：订单创建、订单列表
- 用户认证：登录、注册

### 管理后台
- 仪表盘：数据统计
- 商品管理：CRUD、上架/下架
- 订单管理：订单查看、发货、取消
- 用户管理：用户列表、详情

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3001

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
tz-mall-frontend/
├── src/
│   ├── api/              # API 接口
│   ├── components/       # 组件
│   │   ├── user/        # 用户端组件
│   │   └── admin/       # 管理后台组件
│   ├── composables/      # 组合式函数
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia 状态管理
│   ├── types/           # TypeScript 类型定义
│   ├── utils/           # 工具函数
│   ├── views/           # 页面组件
│   │   ├── user/        # 用户端页面
│   │   └── admin/       # 管理后台页面
│   ├── App.vue
│   ├── main.ts
│   └── style.css
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

## 设计风格

- 轻奢风格：金色 (#D4AF37) 为主色调
- 字体：Playfair Display (标题) + Lato (正文)
- 动画：平滑过渡、悬停效果

## 后端集成

前端通过 API 与 Spring Cloud Alibaba 后端通信，代理配置在 `vite.config.ts` 中。

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```
