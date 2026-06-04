<template>
  <AdminLayout>
    <div class="space-y-6">
      <h2 class="text-2xl font-bold text-gray-800">订单管理</h2>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div v-if="loading" class="flex justify-center py-12">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
        <div v-else-if="orders.length > 0">
          <table class="w-full">
            <thead>
              <tr class="text-left text-gray-500 text-sm border-b border-gray-200 bg-gray-50">
                <th class="px-6 py-3 font-medium">订单号</th>
                <th class="px-6 py-3 font-medium">用户</th>
                <th class="px-6 py-3 font-medium">金额</th>
                <th class="px-6 py-3 font-medium">状态</th>
                <th class="px-6 py-3 font-medium">时间</th>
                <th class="px-6 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 text-gray-800 font-medium">{{ order.orderNo }}</td>
                <td class="px-6 py-4 text-gray-600">用户 #{{ order.userId }}</td>
                <td class="px-6 py-4 text-gray-800 font-medium">¥{{ order.totalPrice.toFixed(2) }}</td>
                <td class="px-6 py-4">
                  <span :class="getStatusClass(order.status)" class="px-2 py-1 rounded-full text-xs font-medium">
                    {{ getStatusText(order.status) }}
                  </span>
                </td>
                <td class="px-6 py-4 text-gray-500 text-sm">{{ formatDate(order.createTime) }}</td>
                <td class="px-6 py-4">
                  <div class="flex gap-3">
                    <select v-if="order.status === 1" v-model.number="order.status" @change="updateStatus(order.id, order.status)" class="text-sm border border-gray-300 rounded px-2 py-1">
                      <option :value="1">已支付</option>
                      <option :value="2">已发货</option>
                    </select>
                    <select v-else-if="order.status === 2" v-model.number="order.status" @change="updateStatus(order.id, order.status)" class="text-sm border border-gray-300 rounded px-2 py-1">
                      <option :value="2">已发货</option>
                      <option :value="3">已完成</option>
                    </select>
                    <span v-else>{{ getStatusText(order.status) }}</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-center py-12 text-gray-500">
          暂无订单数据
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '@/components/admin/AdminLayout.vue'
import { orderApi } from '@/api'
import type { Order } from '@/types'

const loading = ref(true)
const orders = ref<Order[]>([])

async function fetchOrders() {
  try {
    loading.value = true
    const data = await orderApi.getAdminOrderList(1, 100)
    orders.value = data.records
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  } finally {
    loading.value = false
  }
}

async function updateStatus(id: number, status: number) {
  try {
    await orderApi.updateOrderStatus(id, status)
    await fetchOrders()
  } catch (error) {
    console.error('Failed to update order status:', error)
    alert('更新订单状态失败')
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
  fetchOrders()
})
</script>
