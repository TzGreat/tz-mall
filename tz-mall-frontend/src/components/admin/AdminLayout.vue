<template>
  <div class="min-h-screen bg-gray-100 flex">
    <!-- Sidebar -->
    <aside class="w-64 bg-gray-800 text-white flex flex-col">
      <div class="p-6 border-b border-gray-700">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-gradient-to-r from-blue-600 to-purple-600 flex items-center justify-center">
            <span class="text-white font-bold">TZ</span>
          </div>
          <span class="text-xl font-bold">管理后台</span>
        </div>
      </div>

      <nav class="flex-1 py-4">
        <router-link v-for="item in menuItems" :key="item.path"
                     :to="item.path"
                     class="flex items-center gap-3 px-6 py-3 hover:bg-gray-700 transition-colors"
                     :class="{ 'bg-gray-700 text-blue-400': isActive(item.path) }">
          <span class="text-xl">{{ item.icon }}</span>
          <span>{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="p-4 border-t border-gray-700">
        <button @click="handleLogout" class="flex items-center gap-3 px-4 py-2 w-full hover:bg-gray-700 rounded-lg transition-colors">
          <span>🚪</span>
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col">
      <header class="bg-white shadow-sm px-8 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-gray-800">{{ pageTitle }}</h1>
        <div class="flex items-center gap-4">
          <router-link to="/" class="text-gray-600 hover:text-blue-600 transition-colors">
            查看店铺
          </router-link>
          <span class="text-gray-600">管理员</span>
        </div>
      </header>

      <main class="flex-1 p-8 overflow-auto">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const menuItems = [
  { name: '仪表盘', path: '/admin', icon: '📊' },
  { name: '商品管理', path: '/admin/products', icon: '📦' },
  { name: '订单管理', path: '/admin/orders', icon: '🛒' },
  { name: '用户管理', path: '/admin/users', icon: '👥' }
]

const pageTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item ? item.name : '管理后台'
})

function isActive(path: string) {
  return route.path === path
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>
