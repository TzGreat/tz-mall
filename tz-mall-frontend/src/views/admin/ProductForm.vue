<template>
  <AdminLayout>
    <div class="max-w-2xl mx-auto space-y-6">
      <div class="flex items-center gap-4">
        <router-link to="/admin/products" class="text-gray-600 hover:text-blue-600">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </router-link>
        <h2 class="text-2xl font-bold text-gray-800">{{ isEdit ? '编辑商品' : '添加商品' }}</h2>
      </div>

      <div class="bg-white rounded-xl shadow-sm p-6">
        <form @submit.prevent="saveProduct">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">商品名称</label>
              <input v-model="form.name" type="text" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">价格</label>
                <input v-model.number="form.price" type="number" step="0.01" min="0" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">库存</label>
                <input v-model.number="form.stock" type="number" min="0" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
              <input v-model="form.category" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">图片URL</label>
              <input v-model="form.image" type="url" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">描述</label>
              <textarea v-model="form.description" rows="4" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">状态</label>
              <select v-model.number="form.status" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
                <option :value="1">上架</option>
                <option :value="0">下架</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <router-link to="/admin/products" class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
              取消
            </router-link>
            <button type="submit" :disabled="saving" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AdminLayout from '@/components/admin/AdminLayout.vue'
import { itemApi } from '@/api'
import type { ItemRequest } from '@/types'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const saving = ref(false)
const form = ref<ItemRequest>({
  name: '',
  price: 0,
  stock: 0,
  category: '',
  image: '',
  description: '',
  status: 1
})

async function fetchProduct() {
  if (!isEdit.value) return

  try {
    loading.value = true
    const data = await itemApi.getItemDetail(Number(route.params.id))
    form.value = {
      name: data.name,
      price: data.price,
      stock: data.stock,
      category: data.category || '',
      image: data.image || '',
      description: data.description || '',
      status: data.status
    }
  } catch (error) {
    console.error('Failed to fetch product:', error)
  } finally {
    loading.value = false
  }
}

async function saveProduct() {
  try {
    saving.value = true
    if (isEdit.value) {
      await itemApi.updateItem(Number(route.params.id), form.value)
    } else {
      await itemApi.addItem(form.value)
    }
    router.push('/admin/products')
  } catch (error) {
    console.error('Failed to save product:', error)
    alert('保存商品失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchProduct()
})
</script>
