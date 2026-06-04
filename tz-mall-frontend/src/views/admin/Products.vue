<template>
  <AdminLayout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-800">商品管理</h2>
        <router-link to="/admin/products/add" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
          添加商品
        </router-link>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div v-if="loading" class="flex justify-center py-12">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
        <div v-else-if="products.length > 0">
          <table class="w-full">
            <thead>
              <tr class="text-left text-gray-500 text-sm border-b border-gray-200 bg-gray-50">
                <th class="px-6 py-3 font-medium">ID</th>
                <th class="px-6 py-3 font-medium">商品</th>
                <th class="px-6 py-3 font-medium">价格</th>
                <th class="px-6 py-3 font-medium">库存</th>
                <th class="px-6 py-3 font-medium">状态</th>
                <th class="px-6 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="product in products" :key="product.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 text-gray-800">{{ product.id }}</td>
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div class="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
                      <img :src="product.image || 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=100&h=100&fit=crop'" :alt="product.name" class="w-full h-full object-cover">
                    </div>
                    <div>
                      <p class="text-gray-800 font-medium">{{ product.name }}</p>
                      <p class="text-gray-500 text-sm">{{ product.category || '未分类' }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 text-gray-800 font-medium">¥{{ product.price.toFixed(2) }}</td>
                <td class="px-6 py-4">
                  <div class="flex items-center gap-2">
                    <span class="text-gray-800">{{ product.stock }}</span>
                    <div class="flex gap-1">
                      <button @click="adjustStock(product.id, -10)" class="text-gray-400 hover:text-gray-600" title="减10">-10</button>
                      <button @click="adjustStock(product.id, 10)" class="text-gray-400 hover:text-gray-600" title="加10">+10</button>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <span :class="product.status === 1 ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-600'" class="px-2 py-1 rounded-full text-xs font-medium">
                    {{ product.status === 1 ? '上架' : '下架' }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex gap-3">
                    <router-link :to="`/admin/products/${product.id}/edit`" class="text-blue-600 hover:text-blue-700 text-sm">
                      编辑
                    </router-link>
                    <button @click="deleteProduct(product.id)" class="text-red-600 hover:text-red-700 text-sm">
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-center py-12 text-gray-500">
          暂无商品数据
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '@/components/admin/AdminLayout.vue'
import { itemApi } from '@/api'
import type { Item } from '@/types'

const loading = ref(true)
const products = ref<Item[]>([])

async function fetchProducts() {
  try {
    loading.value = true
    const data = await itemApi.getItemList()
    products.value = data
  } catch (error) {
    console.error('Failed to fetch products:', error)
  } finally {
    loading.value = false
  }
}

async function adjustStock(id: number, amount: number) {
  try {
    if (amount > 0) {
      await itemApi.addStock(id, amount)
    } else {
      await itemApi.deductStock(id, Math.abs(amount))
    }
    await fetchProducts()
  } catch (error) {
    console.error('Failed to adjust stock:', error)
    alert('调整库存失败')
  }
}

async function deleteProduct(id: number) {
  if (!confirm('确定要删除该商品吗？')) return

  try {
    await itemApi.deleteItem(id)
    await fetchProducts()
    alert('商品删除成功')
  } catch (error) {
    console.error('Failed to delete product:', error)
    alert('删除商品失败')
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
