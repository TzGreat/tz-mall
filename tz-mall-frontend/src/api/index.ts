import request from '@/utils/request'
import type {
  Result,
  PageResult,
  User,
  LoginRequest,
  RegisterRequest,
  LoginResponse,
  Item,
  ItemRequest,
  ItemQuery,
  Order,
  CreateOrderRequest,
  PayOrderRequest,
  PayOrderResult
} from '@/types'

// 用户 API
export const userApi = {
  login(data: LoginRequest) {
    return request.post<Result<LoginResponse>>('/user/login', data)
  },
  register(data: RegisterRequest) {
    return request.post<Result<User>>('/user/register', data)
  },
  getUserById(id: number) {
    return request.get<Result<User>>(`/user/${id}`)
  },
  updateUser(id: number, data: Partial<User>) {
    return request.put<Result<User>>(`/user/${id}`, data)
  },
  deleteUser(id: number) {
    return request.delete<Result<void>>(`/user/${id}`)
  },
  getUserList(page: number = 1, size: number = 10) {
    return request.get<Result<PageResult<User>>>('/user/list', { params: { page, size } })
  },
  adminCreateUser(data: {
    username: string
    password: string
    phone?: string
    role: number
  }) {
    return request.post<Result<User>>('/admin/user', data)
  }
}

// 商品 API
export const itemApi = {
  searchItems(keyword: string) {
    return request.get<Result<Item[]>>('/item/search', { params: { keyword } })
  },
  getItemList() {
    return request.get<Result<Item[]>>('/item/list')
  },
  getItemDetail(id: number) {
    return request.get<Result<Item>>(`/item/${id}`)
  },
  addItem(data: ItemRequest) {
    return request.post<Result<Item>>('/item/add', data)
  },
  updateItem(id: number, data: ItemRequest) {
    return request.put<Result<Item>>(`/item/${id}`, data)
  },
  deleteItem(id: number) {
    return request.delete<Result<Item>>(`/item/${id}`)
  },
  deductStock(id: number, quantity: number) {
    return request.post<Result<void>>(`/item/${id}/stock/deduct`, null, { params: { quantity } })
  },
  addStock(id: number, quantity: number) {
    return request.post<Result<void>>(`/item/${id}/stock/add`, null, { params: { quantity } })
  }
}

// 订单 API
export const orderApi = {
  createOrder(data: CreateOrderRequest) {
    return request.post<Result<Order>>('/order/create', data)
  },
  getOrderDetail(id: number) {
    return request.get<Result<Order>>(`/order/${id}`)
  },
  getOrderByOrderNo(orderNo: string) {
    return request.get<Result<Order>>(`/order/no/${orderNo}`)
  },
  getOrderList(userId: number, page: number = 1, size: number = 10) {
    return request.get<Result<PageResult<Order>>>('/order/list', { params: { userId, page, size } })
  },
  cancelOrder(orderId: number) {
    return request.patch<Result<void>>(`/order/cancel/${orderId}`)
  },
  payOrder(data: PayOrderRequest) {
    return request.post<Result<PayOrderResult>>('/order/pay', data)
  },
  getAdminOrderList(page: number = 1, size: number = 10) {
    return request.get<Result<PageResult<Order>>>('/order/admin/list', { params: { page, size } })
  },
  updateOrderStatus(id: number, status: number) {
    return request.patch<Result<void>>(`/order/admin/${id}/status`, null, { params: { status } })
  }
}
