<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-8">购物车</h1>

        <div v-if="cartStore.cartItems.length === 0" class="text-center py-12">
          <svg class="w-24 h-24 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <p class="text-gray-500 text-lg mb-4">购物车是空的</p>
          <router-link
            to="/products"
            class="inline-block bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
          >
            去购物
          </router-link>
        </div>

        <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2">
            <div class="bg-white rounded-xl shadow-sm overflow-hidden">
              <div class="p-4 border-b border-gray-200 flex items-center">
                <input
                  type="checkbox"
                  :checked="allChecked"
                  @change="toggleAllCheck"
                  class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                >
                <span class="ml-3 text-gray-700">全选</span>
              </div>

              <div class="divide-y divide-gray-200">
                <div v-for="item in cartStore.cartItems" :key="item.itemId" class="p-4 flex items-center gap-4">
                  <input
                    type="checkbox"
                    :checked="item.checked"
                    @change="toggleCheck(item.itemId)"
                    class="w-5 h-5 text-blue-600 rounded focus:ring-blue-500"
                  >

                  <router-link :to="`/products/${item.itemId}`" class="flex-shrink-0">
                    <div class="w-20 h-20 bg-gray-100 rounded-lg overflow-hidden">
                      <img
                        :src="item.itemImage || 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=200&h=200&fit=crop'"
                        :alt="item.itemName"
                        class="w-full h-full object-cover"
                      >
                    </div>
                  </router-link>

                  <div class="flex-1 min-w-0">
                    <router-link :to="`/products/${item.itemId}`" class="text-gray-900 font-medium hover:text-blue-600">
                      {{ item.itemName }}
                    </router-link>
                    <p class="text-blue-600 font-bold mt-1">¥{{ item.itemPrice.toFixed(2) }}</p>
                  </div>

                  <div class="flex items-center border border-gray-300 rounded-lg">
                    <button
                    @click="updateQuantity(item.itemId, item.num - 1)"
                      :disabled="item.num <= 1"
                      class="px-3 py-1 text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      -
                    </button>
                    <span class="px-4 py-1 border-x border-gray-300 min-w-[50px] text-center">{{ item.num }}</span>
                    <button
                    @click="updateQuantity(item.itemId, item.num + 1)"
                      class="px-3 py-1 text-gray-600 hover:bg-gray-100"
                    >
                      +
                    </button>
                  </div>

                  <button
                    @click="removeFromCart(item.itemId)"
                    class="text-gray-400 hover:text-red-500 transition"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="bg-white rounded-xl shadow-sm p-6 sticky top-24">
              <h2 class="text-xl font-bold text-gray-900 mb-4">订单汇总</h2>

              <div class="space-y-3 mb-6">
                <div class="flex justify-between text-gray-600">
                  <span>商品数量</span>
                  <span>{{ cartStore.checkedItems.reduce((sum, item) => sum + item.num, 0) }} 件</span>
                </div>
                <div class="flex justify-between text-gray-600">
                  <span>商品金额</span>
                  <span>¥{{ cartStore.totalPrice.toFixed(2) }}</span>
                </div>
                <div class="border-t border-gray-200 pt-3 flex justify-between text-lg font-bold">
                  <span>总计</span>
                  <span class="text-blue-600">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
                </div>
              </div>

            <button
              @click="checkout"
              :disabled="cartStore.checkedItems.length === 0"
              class="w-full bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              去结算
            </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { useOrderStore } from '@/stores/order'
import { orderApi } from '@/api'
import type { CreateOrderRequest } from '@/types'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const orderStore = useOrderStore()

const allChecked = computed(() =>
  cartStore.cartItems.length > 0 && cartStore.cartItems.every(item => item.checked)
)

function toggleAllCheck() {
  cartStore.toggleAllCheck(!allChecked.value)
}

function toggleCheck(itemId: number) {
  cartStore.toggleCheck(itemId)
}

function updateQuantity(itemId: number, num: number) {
  if (num < 1) return
  cartStore.updateQuantity(itemId, num)
}

function removeFromCart(itemId: number) {
  cartStore.removeFromCart(itemId)
}

async function checkout() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  if (cartStore.checkedItems.length === 0) {
    return
  }

  try {
    const orderData: CreateOrderRequest = {
      userId: userStore.userId,
      items: cartStore.checkedItems.map(item => ({
        itemId: item.itemId,
        num: item.num
      }))
    }

    const order = await orderApi.createOrder(orderData)
    orderStore.setCurrentOrder(order)
    cartStore.clearCheckedItems()
    router.push(`/orders/${order.id}`)
  } catch (error) {
    console.error('Failed to create order:', error)
    alert('创建订单失败，请重试')
  }
}
</script>
