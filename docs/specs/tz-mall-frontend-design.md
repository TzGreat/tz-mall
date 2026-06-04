# TZ-Mall 电商平台前端设计规范

## 1. 项目概述

### 1.1 项目背景
- 项目名称：TZ-Mall 电商平台
- 项目类型：B2C 电商平台
- 目标用户：普通消费者（用户端）和平台管理员（管理端）

### 1.2 技术栈
- 前端框架：Vue 3 + TypeScript
- 构建工具：Vite
- 状态管理：Pinia
- 路由管理：Vue Router
- UI 样式：Tailwind CSS
- HTTP 客户端：Axios
- 图标库：Heroicons 或 Lucide Icons

### 1.3 项目结构
```
tz-mall-frontend/
├── src/
│   ├── api/                  # API 接口定义
│   │   ├── user.ts           # 用户相关 API
│   │   ├── item.ts           # 商品相关 API
│   │   ├── order.ts          # 订单相关 API
│   │   └── index.ts          # API 统一导出
│   ├── components/           # 公共组件
│   │   ├── user/             # 用户端公共组件
│   │   │   ├── Header.vue
│   │   │   ├── ProductCard.vue
│   │   │   └── Footer.vue
│   │   └── admin/            # 管理端公共组件
│   │       └── AdminLayout.vue
│   ├── router/               # 路由配置
│   │   └── index.ts
│   ├── stores/               # Pinia 状态管理
│   │   ├── user.ts           # 用户状态
│   │   ├── cart.ts           # 购物车状态
│   │   └── order.ts          # 订单状态
│   ├── types/               # TypeScript 类型定义
│   │   └── index.ts
│   ├── utils/                # 工具函数
│   │   ├── request.ts        # Axios 封装
│   │   └── auth.ts           # 认证工具
│   ├── views/                # 页面视图
│   │   ├── user/             # 用户端页面
│   │   │   ├── Home.vue      # 首页
│   │   │   ├── ProductList.vue    # 商品列表
│   │   │   ├── ProductDetail.vue   # 商品详情
│   │   │   ├── Cart.vue      # 购物车
│   │   │   ├── Orders.vue    # 订单列表
│   │   │   ├── Profile.vue   # 个人中心
│   │   │   └── OrderDetail.vue    # 订单详情
│   │   └── admin/            # 管理端页面
│   │       ├── Dashboard.vue # 管理后台首页
│   │       ├── Users.vue     # 用户管理
│   │       ├── Products.vue  # 商品管理
│   │       └── Orders.vue    # 订单管理
│   ├── App.vue               # 根组件
│   ├── main.ts               # 应用入口
│   └── style.css             # 全局样式
├── vite.config.ts            # Vite 配置
├── tailwind.config.js        # Tailwind 配置
└── package.json              # 依赖管理
```

## 2. API 接口规范

### 2.1 用户管理 API

#### 2.1.1 用户注册
- **接口**: `POST /api/user/register`
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "phone": "string (可选)"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "username": "string",
      "phone": "string",
      "role": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  }
  ```

#### 2.1.2 用户登录
- **接口**: `POST /api/user/login`
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "username": "string",
      "role": 0 | 1,
      "token": "jwt_token_string"
    }
  }
  ```

#### 2.1.3 查询用户详情
- **接口**: `GET /api/user/{id}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "username": "string",
      "phone": "string",
      "role": 0 | 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  }
  ```

#### 2.1.4 更新用户信息
- **接口**: `PUT /api/user/{id}`
- **请求体**:
  ```json
  {
    "phone": "string"
  }
  ```
- **响应**: 同查询用户详情

#### 2.1.5 删除用户
- **接口**: `DELETE /api/user/{id}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 2.1.6 分页查询用户列表（管理员）
- **接口**: `GET /api/user/list?page=1&size=10`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "id": 1,
          "username": "string",
          "phone": "string",
          "role": 0 | 1,
          "createTime": "2024-01-01T00:00:00",
          "updateTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 100,
      "size": 10,
      "current": 1,
      "pages": 10
    }
  }
  ```

#### 2.1.7 管理员创建用户
- **接口**: `POST /api/admin/user`
- **请求头**: 需要管理员权限
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "phone": "string (可选)",
    "role": 0 | 1
  }
  ```
- **响应**: 同查询用户详情

### 2.2 商品管理 API

#### 2.2.1 添加商品（管理员）
- **接口**: `POST /api/item/add`
- **请求头**: 需要管理员权限
- **请求体**:
  ```json
  {
    "name": "string",
    "price": 99.99,
    "stock": 100,
    "description": "string (可选)",
    "image": "string (可选)",
    "category": "string (可选)",
    "status": 0 | 1
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "name": "string",
      "price": 99.99,
      "stock": 100,
      "description": "string",
      "image": "string",
      "category": "string",
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  }
  ```

#### 2.2.2 查询商品详情
- **接口**: `GET /api/item/{id}`
- **响应**: 同添加商品响应

#### 2.2.3 更新商品（管理员）
- **接口**: `PUT /api/item/{id}`
- **请求头**: 需要管理员权限
- **请求体**: 同添加商品请求体
- **响应**: 同添加商品响应

#### 2.2.4 删除商品（管理员）
- **接口**: `DELETE /api/item/{id}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 2.2.5 扣减库存
- **接口**: `POST /api/item/{id}/stock/deduct?quantity=1`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 2.2.6 增加库存（管理员）
- **接口**: `POST /api/item/{id}/stock/add?quantity=1`
- **响应**: 同扣减库存响应

#### 2.2.7 商品搜索
- **接口**: `GET /api/item/search?keyword=手机`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [
      {
        "id": 1,
        "name": "string",
        "price": 99.99,
        "stock": 100,
        "description": "string",
        "image": "string",
        "category": "string",
        "status": 1,
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-01T00:00:00"
      }
    ]
  }
  ```

### 2.3 订单管理 API

#### 2.3.1 创建订单
- **接口**: `POST /api/order/create`
- **请求体**:
  ```json
  {
    "userId": 1,
    "items": [
      {
        "itemId": 1,
        "num": 2
      }
    ]
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "id": 1,
      "orderNo": "ORD20240101123456",
      "userId": 1,
      "totalPrice": 199.98,
      "status": 0,
      "statusDesc": "待支付",
      "orderDetails": [
        {
          "id": 1,
          "orderId": 1,
          "itemId": 1,
          "itemName": "string",
          "itemPrice": 99.99,
          "num": 2
        }
      ],
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  }
  ```

#### 2.3.2 查询订单详情
- **接口**: `GET /api/order/{id}` 或 `GET /api/order/no/{orderNo}`
- **响应**: 同创建订单响应

#### 2.3.3 查询用户订单列表
- **接口**: `GET /api/order/list?userId=1&page=1&size=10`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "records": [
        {
          "id": 1,
          "orderNo": "ORD20240101123456",
          "userId": 1,
          "totalPrice": 199.98,
          "status": 0,
          "statusDesc": "待支付",
          "orderDetails": [],
          "createTime": "2024-01-01T00:00:00",
          "updateTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 100,
      "size": 10,
      "current": 1,
      "pages": 10
    }
  }
  ```

#### 2.3.4 取消订单
- **接口**: `PATCH /api/order/cancel/{orderId}`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": null
  }
  ```

#### 2.3.5 订单支付
- **接口**: `POST /api/order/pay`
- **请求体**:
  ```json
  {
    "orderId": 1,
    "payMethod": 1
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "orderId": 1,
      "payStatus": "SUCCESS",
      "payTime": "2024-01-01T00:00:00"
    }
  }
  ```

## 3. 功能模块设计

### 3.1 管理员端功能

#### 3.1.1 用户管理
- **用户列表**
  - 展示所有用户信息（分页）
  - 显示字段：ID、用户名、手机号、角色、创建时间
  - 支持按用户名搜索
  - 支持按角色筛选（全部、管理员、普通用户）
  - 支持删除用户
  - 支持添加新用户（设置用户名、密码、手机号、角色）

- **添加用户表单**
  - 用户名（必填，3-50字符）
  - 密码（必填，6-100字符）
  - 手机号（选填，11位手机号格式）
  - 角色（必填，单选：管理员/普通用户）

#### 3.1.2 商品管理
- **商品列表**
  - 展示所有商品信息（分页）
  - 显示字段：ID、商品名称、价格、库存、分类、状态、创建时间
  - 支持按商品名称搜索
  - 支持按分类筛选
  - 支持按状态下架筛选（全部、上架、下架）
  - 支持添加商品
  - 支持编辑商品
  - 支持删除商品

- **添加/编辑商品表单**
  - 商品名称（必填，2-20字符）
  - 价格（必填，大于0）
  - 库存（必填，大于等于0）
  - 商品描述（选填，最多5000字符）
  - 商品图片URL（选填，最多1000字符）
  - 分类（选填，最多50字符）
  - 状态（必填，单选：上架/下架）

- **库存管理**
  - 在商品列表中可以直接调整库存
  - 点击"增加库存"按钮，输入增加数量
  - 点击"减少库存"按钮，输入减少数量

#### 3.1.3 订单管理
- **订单列表**
  - 展示所有订单信息（分页）
  - 显示字段：订单ID、订单号、用户ID、总价、状态、创建时间
  - 支持按订单号搜索
  - 支持按状态筛选（全部、待支付、已支付、已发货、已完成、已取消）
  - 支持查看订单详情

- **订单详情**
  - 订单基本信息：订单ID、订单号、用户ID、总价、状态、创建时间
  - 订单商品列表：商品名称、单价、数量、小计

### 3.2 普通用户端功能

#### 3.2.1 首页
- 展示热门商品推荐
- 展示商品分类导航
- 搜索商品功能

#### 3.2.2 商品浏览
- **商品列表页**
  - 展示所有上架商品（分页）
  - 显示字段：商品图片、名称、价格、库存
  - 支持按商品名称搜索
  - 支持按分类筛选
  - 点击商品卡片进入商品详情页

- **商品详情页**
  - 展示商品完整信息：图片、名称、价格、库存、分类、描述
  - 显示商品库存状态（充足/库存不足）
  - 添加到购物车功能
  - 立即购买功能
  - 返回商品列表

#### 3.2.3 购物车
- **购物车页面**
  - 展示已添加的商品列表
  - 每个商品显示：商品图片、名称、单价、数量、小计
  - 支持修改商品数量
  - 支持删除商品
  - 显示购物车总价
  - 结算功能（创建订单）

#### 3.2.4 订单管理
- **订单列表页**
  - 展示当前用户的所有订单（分页）
  - 显示字段：订单号、总价、状态、创建时间
  - 支持按状态筛选（全部、待支付、已支付、已完成、已取消）
  - 点击订单进入订单详情

- **订单详情页**
  - 订单基本信息：订单号、总价、状态、创建时间
  - 订单商品列表：商品名称、单价、数量、小计
  - 订单操作：根据状态显示"去支付"、"取消订单"等按钮

- **订单状态说明**
  - 0 - 待支付：显示"去支付"和"取消订单"按钮
  - 1 - 已支付：显示"已支付，请等待发货"
  - 2 - 已发货：显示"确认收货"
  - 3 - 已完成：显示"订单已完成"
  - 4 - 已取消：显示"订单已取消"

#### 3.2.5 个人中心
- **个人信息页面**
  - 展示当前用户信息：用户名、手机号、角色、注册时间
  - 修改个人信息表单：手机号
  - 退出登录功能

## 4. 页面路由设计

### 4.1 用户端路由
```
/login                    # 登录页
/register                 # 注册页
/home                     # 首页
/products                 # 商品列表
/products/:id             # 商品详情
/cart                     # 购物车
/orders                   # 订单列表
/orders/:id               # 订单详情
/profile                  # 个人中心
/profile/edit             # 编辑个人信息
```

### 4.2 管理员端路由
```
/admin                    # 管理后台首页
/admin/users              # 用户管理
/admin/products           # 商品管理
/admin/products/add       # 添加商品
/admin/products/:id/edit  # 编辑商品
/admin/orders             # 订单管理
/admin/orders/:id         # 订单详情
```

### 4.3 路由守卫
- 未登录用户访问需要登录的页面时，重定向到登录页
- 非管理员访问管理端页面时，重定向到用户首页
- 管理员访问用户端需要权限的页面时，重定向到管理后台

## 5. 状态管理设计

### 5.1 用户状态 (user store)
```typescript
interface UserState {
  userInfo: User | null;
  token: string | null;
  isLoggedIn: boolean;
  isAdmin: boolean;
}

// Actions
login(username: string, password: string): Promise<void>
register(username: string, password: string, phone?: string): Promise<void>
logout(): void
updateUserInfo(data: UpdateUserDTO): Promise<void>
getUserInfo(id: number): Promise<void>
```

### 5.2 购物车状态 (cart store)
```typescript
interface CartItem {
  item: Item;
  quantity: number;
}

interface CartState {
  items: CartItem[];
}

// Actions
addToCart(item: Item, quantity: number): void
removeFromCart(itemId: number): void
updateQuantity(itemId: number, quantity: number): void
clearCart(): void
checkout(): Promise<Order>
```

### 5.3 订单状态 (order store)
```typescript
interface OrderState {
  orders: Order[];
  currentOrder: Order | null;
  loading: boolean;
}

// Actions
fetchOrders(userId: number, page: number, size: number): Promise<void>
fetchOrderDetail(orderId: number): Promise<void>
createOrder(userId: number, items: OrderItem[]): Promise<Order>
cancelOrder(orderId: number): Promise<void>
payOrder(orderId: number, payMethod: number): Promise<void>
```

## 6. 认证与权限设计

### 6.1 JWT Token
- 用户登录成功后，后端返回 JWT Token
- 前端将 Token 存储在 localStorage
- 所有需要认证的请求，在请求头中携带 Token
- Token 格式：`Authorization: Bearer <token>`

### 6.2 权限控制
- 用户角色：0（管理员）、1（普通用户）
- 管理员接口需要 `@RequireAdmin` 注解标记
- 前端路由守卫检查用户角色，非管理员不能访问管理端

### 6.3 登录状态维护
- 应用启动时检查 localStorage 中是否有 Token
- 如果有 Token，解析并恢复用户状态
- Token 过期或无效时，清除登录状态并跳转到登录页

## 7. 错误处理设计

### 7.1 HTTP 错误处理
- 401 Unauthorized：Token 无效或过期，跳转登录页
- 403 Forbidden：无权限访问，显示提示信息
- 404 Not Found：资源不存在，显示提示信息
- 500 Internal Server Error：服务器错误，显示提示信息

### 7.2 业务错误处理
- 登录失败：显示"用户名或密码错误"
- 注册失败：显示具体校验错误信息
- 下单失败：显示"库存不足"或"商品已下架"等提示
- 支付失败：显示支付失败原因

### 7.3 网络错误处理
- 网络断开：显示"网络连接失败，请检查网络"
- 请求超时：显示"请求超时，请重试"

## 8. 响应数据结构

### 8.1 统一响应格式
```typescript
interface Response<T> {
  code: number;      // 状态码，200表示成功
  message: string;   // 响应消息
  data: T;          // 响应数据
}

// 分页响应格式
interface PageResponse<T> {
  records: T[];      // 数据列表
  total: number;    // 总记录数
  size: number;      // 每页条数
  current: number;   // 当前页码
  pages: number;     // 总页数
}
```

## 9. 页面布局设计

### 9.1 用户端布局
- **顶部导航栏**
  - Logo + 网站名称
  - 搜索框
  - 购物车图标 + 数量
  - 用户头像 + 下拉菜单（个人中心、退出登录）

- **底部**
  - 版权信息
  - 联系方式

- **页面结构**
  ```
  +----------------------------------+
  |           Header (导航栏)         |
  +----------------------------------+
  |                                  |
  |         Main Content             |
  |        (页面主要内容)              |
  |                                  |
  +----------------------------------+
  |            Footer (底部)          |
  +----------------------------------+
  ```

### 9.2 管理员端布局
- **左侧边栏**
  - Logo + 网站名称
  - 导航菜单：
    - 首页/仪表盘
    - 用户管理
    - 商品管理
    - 订单管理
  - 退出登录按钮

- **右侧内容区**
  - 顶部工具栏：页面标题 + 操作按钮
  - 内容区域：表格、表单等

- **页面结构**
  ```
  +--------+------------------------+
  |        |       Header            |
  |  Side  +------------------------+
  |  bar   |                        |
  |        |       Main Content      |
  |        |                        |
  |        |                        |
  +--------+------------------------+
  ```

## 10. 表单验证规则

### 10.1 用户注册表单
- 用户名：必填，3-50字符
- 密码：必填，6-100字符
- 确认密码：必填，与密码一致
- 手机号：选填，11位手机号格式

### 10.2 用户登录表单
- 用户名：必填
- 密码：必填

### 10.3 添加商品表单
- 商品名称：必填，2-20字符
- 价格：必填，大于0，最多2位小数
- 库存：必填，大于等于0，整数
- 分类：选填，最多50字符
- 描述：选填，最多5000字符
- 图片URL：选填，有效的URL格式
- 状态：必填，单选

### 10.4 添加用户表单
- 用户名：必填，3-50字符
- 密码：必填，6-100字符
- 手机号：选填，11位手机号格式
- 角色：必填，单选

## 11. 状态码映射

### 11.1 订单状态
- 0: 待支付
- 1: 已支付
- 2: 已发货
- 3: 已完成
- 4: 已取消

### 11.2 商品状态
- 0: 下架
- 1: 上架

### 11.3 用户角色
- 0: 管理员
- 1: 普通用户

### 11.4 支付方式
- 1: 微信支付
- 2: 支付宝支付

## 12. 性能优化

### 12.1 图片懒加载
- 商品列表中的商品图片使用懒加载
- 用户滚动到可视区域时才加载图片

### 12.2 数据缓存
- 商品列表数据缓存到 Pinia Store
- 用户信息缓存到 Pinia Store
- 避免重复请求

### 12.3 分页加载
- 所有列表页面使用分页加载
- 每页默认10条数据
- 避免一次加载过多数据

## 13. 安全考虑

### 13.1 XSS 防护
- 所有用户输入的内容在展示时进行转义
- 避免直接渲染 HTML

### 13.2 CSRF 防护
- 使用 JWT Token 进行身份验证
- Token 存储在 localStorage 中

### 13.3 敏感信息处理
- 用户密码不在前端存储
- 敏感操作需要二次确认

## 14. 浏览器兼容性
- Chrome: 最新版本
- Firefox: 最新版本
- Safari: 最新版本
- Edge: 最新版本

## 15. 移动端适配
- 使用响应式设计
- 移动端布局自适应
- 触摸操作优化
