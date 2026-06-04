<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900 mb-4">商品列表</h1>
          <div class="flex flex-wrap gap-4 items-center">
            <div class="flex-1 max-w-md">
              <div class="relative">
                <input
                  v-model="searchKeyword"
                  type="text"
                  placeholder="搜索商品..."
                  class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  @keyup.enter="handleSearch"
                />
                <svg class="absolute left-3 top-2.5 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
            </div>
            <select v-model="sortBy" @change="handleSort" class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
            </select>
          </div>
        </div>

        <div v-if="loading" class="flex justify-center items-center py-12">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <div v-else-if="products.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <ProductCard v-for="product in products" :key="product.id" :item="product" />
        </div>

        <div v-else class="text-center py-12">
          <svg class="w-16 h-16 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="text-gray-500 text-lg">暂无商品</p>
        </div>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import ProductCard from '@/components/user/ProductCard.vue'
import { itemApi } from '@/api'
import type { Item } from '@/types'

const route = useRoute()
const loading = ref(true)
const products = ref<Item[]>([])
const allProducts = ref<Item[]>([])
const searchKeyword = ref('')
const sortBy = ref('default')

async function fetchProducts() {
  try {
    loading.value = true
    const data = await itemApi.getItemList()
    allProducts.value = data
    products.value = data

    if (route.query.keyword) {
      searchKeyword.value = route.query.keyword as string
      handleSearch()
    }
  } catch (error) {
    console.error('Failed to fetch products:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  if (!searchKeyword.value.trim()) {
    products.value = [...allProducts.value]
  } else {
    products.value = allProducts.value.filter(p =>
      p.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  handleSort()
}

function handleSort() {
  if (sortBy.value === 'price-asc') {
    products.value.sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price-desc') {
    products.value.sort((a, b) => b.price - a.price)
  }
}

watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = newKeyword as string
    handleSearch()
  }
})

onMounted(() => {
  fetchProducts()
})
</script>
