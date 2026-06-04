<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-8">我的订单</h1>

        <div v-if="loading" class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <div v-else-if="orders.length === 0" class="text-center py-12">
          <svg class="w-24 h-24 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <p class="text-gray-500 text-lg mb-4">暂无订单</p>
          <router-link
            to="/products"
            class="inline-block bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
          >
            去购物
          </router-link>
        </div>

        <div v-else class="space-y-6">
          <div v-for="order in orders" :key="order.id" class="bg-white rounded-xl shadow-sm overflow-hidden">
            <div class="p-4 border-b border-gray-200 flex items-center justify-between">
              <div>
                <span class="text-gray-500 text-sm">订单号: {{ order.orderNo }}</span>
                <span class="text-gray-400 text-sm ml-4">{{ formatDate(order.createTime) }}</span>
              </div>
              <span :class="getStatusClass(order.status)" class="font-medium">
                {{ getStatusText(order.status) }}
              </span>
            </div>

            <div class="p-4">
              <div class="space-y-3">
                <div v-for="item in order.orderDetails" :key="item.id" class="flex items-center gap-4">
                  <div class="w-16 h-16 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
                    <img
                      src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=100&h=100&fit=crop"
                      :alt="item.itemName"
                      class="w-full h-full object-cover"
                    >
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-gray-900 font-medium">{{ item.itemName }}</p>
                    <p class="text-gray-500 text-sm">¥{{ item.itemPrice.toFixed(2) }} × {{ item.num }}</p>
                  </div>
                  <div class="text-gray-900 font-medium">
                    ¥{{ (item.itemPrice * item.num).toFixed(2) }}
                  </div>
                </div>
              </div>
            </div>

            <div class="p-4 border-t border-gray-200 flex items-center justify-between">
              <div class="text-gray-600">
                共 {{ order.orderDetails.reduce((sum, item) => sum + item.num, 0) }} 件商品
                <span class="text-gray-900 font-bold ml-2">合计: ¥{{ order.totalPrice.toFixed(2) }}</span>
              </div>
              <div class="flex gap-3">
                <router-link
                  :to="`/orders/${order.id}`"
                  class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
                >
                  查看详情
                </router-link>
                <button
                  v-if="order.status === 0"
                  @click="cancelOrder(order.id)"
                  class="px-4 py-2 border border-red-300 rounded-lg text-red-600 hover:bg-red-50 transition"
                >
                  取消订单
                </button>
                <button
                  v-if="order.status === 0"
                  @click="payOrder(order.id)"
                  class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                >
                  立即支付
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import { orderApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const userStore = useUserStore()

const loading = ref(true)
const orders = ref<Order[]>([])

async function fetchOrders() {
  try {
    loading.value = true
    const data = await orderApi.getOrderList(userStore.userId)
    orders.value = data.records
  } catch (error) {
    console.error('Failed to fetch orders:', error)
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
    0: 'text-orange-500',
    1: 'text-blue-500',
    2: 'text-purple-500',
    3: 'text-yellow-500',
    4: 'text-green-500',
    5: 'text-gray-500'
  }
  return classMap[status] || 'text-gray-500'
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleString('zh-CN')
}

async function cancelOrder(orderId: number) {
  if (!confirm('确定要取消订单吗？')) return

  try {
    await orderApi.cancelOrder(orderId)
    await fetchOrders()
  } catch (error) {
    console.error('Failed to cancel order:', error)
    alert('取消订单失败，请重试')
  }
}

async function payOrder(orderId: number) {
  try {
    await orderApi.payOrder({ orderId })
    await fetchOrders()
    alert('支付成功！')
  } catch (error) {
    console.error('Failed to pay order:', error)
    alert('支付失败，请重试')
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
