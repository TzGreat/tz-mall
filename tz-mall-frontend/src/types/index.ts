// 通用响应类型
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 分页响应类型
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 用户相关
export interface User {
  id: number
  username: string
  phone?: string
  role?: number
  createTime: string
  updateTime?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  phone?: string
}

export interface LoginResponse {
  token: string
  userInfo: User
}

// 商品相关
export interface Item {
  id: number
  name: string
  price: number
  stock: number
  image?: string
  category?: string
  description?: string
  status: number
  createTime: string
  updateTime?: string
}

export interface ItemRequest {
  name: string
  price: number
  stock: number
  image?: string
  category?: string
  description?: string
  status?: number
}

export interface ItemQuery {
  keyword?: string
  category?: string
  minPrice?: number
  maxPrice?: number
  page?: number
  size?: number
}

// 购物车相关
export interface CartItem {
  itemId: number
  itemName: string
  itemPrice: number
  itemImage?: string
  num: number
  checked?: boolean
}

// 订单相关
export interface Order {
  id: number
  orderNo: string
  userId: number
  totalPrice: number
  status: number
  statusDesc?: string
  createTime: string
  updateTime?: string
  orderDetails: OrderDetail[]
}

export interface OrderDetail {
  id: number
  orderId: number
  itemId: number
  itemName: string
  itemPrice: number
  num: number
}

export interface CreateOrderRequest {
  userId: number
  items: { itemId: number; num: number }[]
}

export interface PayOrderRequest {
  orderId: number
  payMethod?: number
}

export interface PayOrderResult {
  orderId: number
  orderNo: string
  payStatus: number
  payTime?: string
  payUrl?: string
}
