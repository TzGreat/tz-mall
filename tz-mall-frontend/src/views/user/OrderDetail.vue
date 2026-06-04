<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center gap-4 mb-8">
          <router-link
            to="/orders"
            class="text-gray-600 hover:text-blue-600 transition"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </router-link>
          <h1 class="text-3xl font-bold text-gray-900">订单详情</h1>
        </div>

        <div v-if="loading" class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <div v-else-if="order" class="bg-white rounded-xl shadow-sm overflow-hidden">
          <div class="p-6 border-b border-gray-200">
            <div class="flex items-center justify-between mb-4">
              <div>
                <span class="text-gray-500 text-sm">订单号:</span>
                <span class="text-gray-900 font-medium ml-2">{{ order.orderNo }}</span>
              </div>
              <span :class="getStatusClass(order.status)" class="text-lg font-bold">
                {{ getStatusText(order.status) }}
              </span>
            </div>
            <p class="text-gray-500 text-sm">
              下单时间: {{ formatDate(order.createTime) }}
            </p>
          </div>

          <div class="p-6">
            <h2 class="text-lg font-semibold text-gray-900 mb-4">商品信息</h2>
            <div class="space-y-4">
              <div v-for="item in order.orderDetails" :key="item.id" class="flex items-center gap-4">
                <div class="w-20 h-20 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
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
                <div class="text-gray-900 font-bold text-lg">
                  ¥{{ (item.itemPrice * item.num).toFixed(2) }}
                </div>
              </div>
            </div>
          </div>

          <div class="p-6 border-t border-gray-200">
            <div class="space-y-2 text-right">
              <div class="flex justify-between text-gray-600">
                <span>商品金额</span>
                <span>¥{{ order.totalPrice.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-gray-600">
                <span>运费</span>
                <span>¥0.00</span>
              </div>
              <div class="flex justify-between text-lg font-bold text-gray-900 pt-2 border-t border-gray-200">
                <span>合计</span>
                <span class="text-blue-600">¥{{ order.totalPrice.toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <div class="p-6 border-t border-gray-200 flex justify-end gap-3">
            <button
              v-if="order.status === 0"
              @click="cancelOrder"
              class="px-6 py-2 border border-red-300 rounded-lg text-red-600 hover:bg-red-50 transition"
            >
              取消订单
            </button>
            <button
              v-if="order.status === 0"
              @click="payOrder"
              class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
            >
              立即支付
            </button>
          </div>
        </div>

        <div v-else class="text-center py-12">
          <p class="text-gray-500 text-lg">订单不存在</p>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import { orderApi } from '@/api'
import type { Order } from '@/types'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref<Order | null>(null)

async function fetchOrder() {
  try {
    const id = Number(route.params.id)
    const data = await orderApi.getOrderDetail(id)
    order.value = data
  } catch (error) {
    console.error('Failed to fetch order:', error)
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

async function cancelOrder() {
  if (!order.value || !confirm('确定要取消订单吗？')) return

  try {
    await orderApi.cancelOrder(order.value.id)
    await fetchOrder()
  } catch (error) {
    console.error('Failed to cancel order:', error)
    alert('取消订单失败，请重试')
  }
}

async function payOrder() {
  if (!order.value) return

  try {
    await orderApi.payOrder({ orderId: order.value.id })
    await fetchOrder()
    alert('支付成功！')
  } catch (error) {
    console.error('Failed to pay order:', error)
    alert('支付失败，请重试')
  }
}

onMounted(() => {
  fetchOrder()
})
</script>
