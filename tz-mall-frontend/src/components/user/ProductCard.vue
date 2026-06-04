<template>
  <div class="bg-white rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300 overflow-hidden group">
    <router-link :to="`/products/${item.id}`" class="block">
      <div class="relative aspect-square bg-gray-100 overflow-hidden">
        <img 
          :src="item.image || 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400&h=400&fit=crop'" 
          :alt="item.name"
          class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
        >
        <div v-if="item.stock === 0" class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <span class="text-white font-bold text-lg">已售罄</span>
        </div>
      </div>
      <div class="p-4">
        <h3 class="font-medium text-gray-900 truncate">{{ item.name }}</h3>
        <p class="mt-1 text-blue-600 font-bold text-lg">¥{{ item.price.toFixed(2) }}</p>
        <p class="mt-1 text-sm text-gray-500">库存: {{ item.stock }}</p>
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import type { Item } from '@/types'

defineProps<{
  item: Item
}>()
</script>
