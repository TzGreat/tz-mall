<template>
  <AdminLayout>
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-gray-800">仪表盘</h2>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">总用户数</p>
              <p class="text-3xl font-bold text-gray-800">{{ stats.users }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">👥</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">商品总数</p>
              <p class="text-3xl font-bold text-gray-800">{{ stats.products }}</p>
            </div>
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">📦</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">订单总数</p>
              <p class="text-3xl font-bold text-gray-800">{{ stats.orders }}</p>
            </div>
            <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">🛒</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">总销售额</p>
              <p class="text-3xl font-bold text-gray-800">¥{{ stats.revenue.toFixed(2) }}</p>
            </div>
            <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">💰</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Orders -->
      <div class="bg-white rounded-xl shadow-sm">
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800">最近订单</h3>
        </div>
        <div class="p-6">
          <div v-if="loading" class="flex justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
          <div v-else-if="recentOrders.length > 0" class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="text-left text-gray-500 text-sm border-b border-gray-200">
                  <th class="pb-3 font-medium">订单号</th>
                  <th class="pb-3 font-medium">用户</th>
                  <th class="pb-3 font-medium">金额</th>
                  <th class="pb-3 font-medium">状态</th>
                  <th class="pb-3 font-medium">时间</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="order in recentOrders" :key="order.id" class="hover:bg-gray-50">
                  <td class="py-3 text-gray-800">{{ order.orderNo }}</td>
                  <td class="py-3 text-gray-600">用户 #{{ order.userId }}</td>
                  <td class="py-3 text-gray-800 font-medium">¥{{ order.totalPrice.toFixed(2) }}</td>
                  <td class="py-3">
                    <span :class="getStatusClass(order.status)" class="px-2 py-1 rounded-full text-xs font-medium">
                      {{ getStatusText(order.status) }}
                    </span>
                  </td>
                  <td class="py-3 text-gray-500 text-sm">{{ formatDate(order.createTime) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="text-center py-8 text-gray-500">
            暂无订单数据
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '@/components/admin/AdminLayout.vue'
import { orderApi, userApi, itemApi } from '@/api'
import type { Order } from '@/types'

const loading = ref(true)
const recentOrders = ref<Order[]>([])
const stats = ref({
  users: 0,
  products: 0,
  orders: 0,
  revenue: 0
})

async function fetchDashboardData() {
  try {
    loading.value = true

    // Fetch all orders
    const ordersData = await orderApi.getAdminOrderList(1, 10)
    recentOrders.value = ordersData.records

    // Calculate stats
    stats.value.orders = ordersData.total
    stats.value.revenue = recentOrders.value.reduce((sum, o) => sum + o.totalPrice, 0)

    // Fetch users count
    try {
      const usersData = await userApi.getUserList(1, 1)
      stats.value.users = usersData.total
    } catch {
      stats.value.users = 0
    }

    // Fetch products
    const productsData = await itemApi.getItemList()
    stats.value.products = productsData.length
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  } finally {
    loading.value = false
  }
}

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    3: '待评价',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知'
}

function getStatusClass(status: number): string {
  const classMap: Record<number, string> = {
    0: 'bg-orange-100 text-orange-600',
    1: 'bg-blue-100 text-blue-600',
    2: 'bg-purple-100 text-purple-600',
    3: 'bg-yellow-100 text-yellow-600',
    4: 'bg-green-100 text-green-600',
    5: 'bg-gray-100 text-gray-600'
  }
  return classMap[status] || 'bg-gray-100 text-gray-600'
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchDashboardData()
})
</script>
