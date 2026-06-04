<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div v-if="loading" class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <div v-else-if="product" class="bg-white rounded-xl shadow-sm overflow-hidden">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8 p-8">
            <div class="aspect-square bg-gray-100 rounded-lg overflow-hidden">
              <img
                :src="product.image || 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=600&h=600&fit=crop'"
                :alt="product.name"
                class="w-full h-full object-cover"
              >
            </div>

            <div>
              <div class="mb-6">
                <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ product.name }}</h1>
                <div class="flex items-center gap-4 text-sm text-gray-500">
                  <span>分类: {{ product.category || '未分类' }}</span>
                  <span>库存: {{ product.stock }}</span>
                </div>
              </div>

              <div class="bg-gray-50 rounded-lg p-4 mb-6">
                <div class="flex items-baseline gap-2">
                  <span class="text-gray-500">¥</span>
                  <span class="text-4xl font-bold text-blue-600">{{ product.price.toFixed(2) }}</span>
                </div>
              </div>

              <div v-if="product.description" class="mb-6">
                <h3 class="text-lg font-semibold text-gray-900 mb-2">商品描述</h3>
                <p class="text-gray-600">{{ product.description }}</p>
              </div>

              <div class="flex items-center gap-4 mb-6">
                <span class="text-gray-700">数量:</span>
                <div class="flex items-center border border-gray-300 rounded-lg">
                  <button
                    @click="decreaseQuantity"
                    :disabled="quantity <= 1"
                    class="px-4 py-2 text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    -
                  </button>
                  <span class="px-6 py-2 border-x border-gray-300 min-w-[60px] text-center">{{ quantity }}</span>
                  <button
                    @click="increaseQuantity"
                    :disabled="quantity >= product.stock"
                    class="px-4 py-2 text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    +
                  </button>
                </div>
              </div>

              <div class="flex gap-4">
                <button
                  @click="addToCart"
                  :disabled="product.stock === 0"
                  class="flex-1 bg-orange-500 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-600 transition disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  加入购物车
                </button>
                <button
                  @click="buyNow"
                  :disabled="product.stock === 0"
                  class="flex-1 bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  立即购买
                </button>
              </div>

              <div v-if="product.stock === 0" class="mt-4 text-center text-red-500 font-medium">
                商品已售罄
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-12">
          <p class="text-gray-500 text-lg">商品不存在</p>
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
import { itemApi, orderApi } from '@/api'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { useOrderStore } from '@/stores/order'
import type { Item, CartItem, CreateOrderRequest } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const orderStore = useOrderStore()

const loading = ref(true)
const product = ref<Item | null>(null)
const quantity = ref(1)

async function fetchProduct() {
  try {
    const id = Number(route.params.id)
    const data = await itemApi.getItemDetail(id)
    product.value = data
  } catch (error) {
    console.error('Failed to fetch product:', error)
  } finally {
    loading.value = false
  }
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

function increaseQuantity() {
  if (product.value && quantity.value < product.value.stock) {
    quantity.value++
  }
}

function addToCart() {
  if (!product.value) return

  const cartItem: CartItem = {
    itemId: product.value.id,
    itemName: product.value.name,
    itemPrice: product.value.price,
    itemImage: product.value.image,
    num: quantity.value,
    checked: true
  }

  cartStore.addToCart(cartItem)
  alert('已加入购物车！')
}

async function buyNow() {
  if (!product.value) return
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  const cartItem: CartItem = {
    itemId: product.value.id,
    itemName: product.value.name,
    itemPrice: product.value.price,
    itemImage: product.value.image,
    num: quantity.value,
    checked: true
  }

  try {
    const orderData: CreateOrderRequest = {
      userId: userStore.userId,
      items: [{ itemId: product.value.id, num: quantity.value }]
    }

    const order = await orderApi.createOrder(orderData)
    orderStore.setCurrentOrder(order)
    router.push(`/orders/${order.id}`)
  } catch (error) {
    console.error('Failed to create order:', error)
    alert('创建订单失败，请重试')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>
