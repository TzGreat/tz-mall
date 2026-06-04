<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1">
      <section class="bg-gradient-to-r from-blue-600 to-blue-800 text-white py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 class="text-4xl md:text-5xl font-bold mb-4">欢迎来到 TZ-Mall</h1>
          <p class="text-xl md:text-2xl text-blue-100 mb-8">发现优质商品，享受愉快购物体验</p>
          <router-link
            to="/products"
            class="inline-block bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-blue-50 transition"
          >
            立即选购
          </router-link>
        </div>
      </section>

      <section class="py-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between items-center mb-8">
            <h2 class="text-2xl font-bold text-gray-900">热门商品</h2>
            <router-link to="/products" class="text-blue-600 hover:text-blue-700 font-medium">
              查看全部 →
            </router-link>
          </div>

          <div v-if="loading" class="flex justify-center items-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>

          <div v-else-if="products.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <ProductCard v-for="product in products" :key="product.id" :item="product" />
          </div>

          <div v-else class="text-center py-12 text-gray-500">
            暂无商品
          </div>
        </div>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import ProductCard from '@/components/user/ProductCard.vue'
import { itemApi } from '@/api'
import type { Item } from '@/types'

const loading = ref(true)
const products = ref<Item[]>([])

async function fetchProducts() {
  try {
    const data = await itemApi.getItemList()
    products.value = data.slice(0, 8)
  } catch (error) {
    console.error('Failed to fetch products:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
