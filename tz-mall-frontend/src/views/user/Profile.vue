<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <Header />

    <main class="flex-1 py-8">
      <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-8">个人中心</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="md:col-span-1">
            <div class="bg-white rounded-xl shadow-sm p-6">
              <div class="flex items-center gap-4 mb-6">
                <div class="w-20 h-20 bg-blue-600 rounded-full flex items-center justify-center">
                  <span class="text-white text-3xl font-bold">
                    {{ userStore.username.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div>
                  <h2 class="text-xl font-bold text-gray-900">{{ userStore.username }}</h2>
                  <p class="text-gray-500">{{ userStore.isAdmin ? '管理员' : '普通用户' }}</p>
                </div>
              </div>

              <nav class="space-y-2">
                <router-link
                  to="/orders"
                  class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  我的订单
                </router-link>
                <router-link
                  to="/cart"
                  class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  购物车
                </router-link>
                <router-link
                  v-if="userStore.isAdmin"
                  to="/admin"
                  class="flex items-center gap-3 px-4 py-3 rounded-lg text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  管理后台
                </router-link>
              </nav>

              <div class="mt-6 pt-6 border-t border-gray-200">
                <button
                  @click="handleLogout"
                  class="w-full flex items-center justify-center gap-3 px-4 py-3 rounded-lg text-red-600 hover:bg-red-50 transition"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  退出登录
                </button>
              </div>
            </div>
          </div>

          <div class="md:col-span-2">
            <div class="bg-white rounded-xl shadow-sm p-6">
              <h2 class="text-xl font-bold text-gray-900 mb-6">账户信息</h2>

              <div v-if="!editing" class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">用户ID</label>
                  <div class="px-4 py-3 bg-gray-50 rounded-lg text-gray-900">{{ userStore.userId }}</div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                  <div class="px-4 py-3 bg-gray-50 rounded-lg text-gray-900">{{ userData.username }}</div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
                  <div class="px-4 py-3 bg-gray-50 rounded-lg text-gray-900">{{ userData.phone || '未设置' }}</div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">角色</label>
                  <div class="px-4 py-3 bg-gray-50 rounded-lg text-gray-900">{{ userStore.isAdmin ? '管理员' : '普通用户' }}</div>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">创建时间</label>
                  <div class="px-4 py-3 bg-gray-50 rounded-lg text-gray-900">{{ formatDate(userData.createTime) }}</div>
                </div>
              </div>

              <div v-else class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
                  <input v-model="editForm.username" type="text" required minlength="3" maxlength="50"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
                  <input v-model="editForm.phone" type="tel"
                    class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="请输入手机号">
                </div>
              </div>

              <div v-if="!editing" class="mt-6">
                <button @click="startEditing"
                  class="w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
                  编辑信息
                </button>
              </div>
              <div v-else class="mt-6 flex gap-3">
                <button @click="cancelEditing"
                  class="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition">
                  取消
                </button>
                <button @click="saveProfile" :disabled="saving"
                  class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50">
                  {{ saving ? '保存中...' : '保存' }}
                </button>
              </div>

              <div class="mt-8 pt-6 border-t border-gray-200">
                <h3 class="text-lg font-semibold text-gray-900 mb-4">快捷操作</h3>
                <div class="grid grid-cols-2 gap-4">
                  <router-link
                    to="/products"
                    class="flex flex-col items-center p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition"
                  >
                    <svg class="w-8 h-8 text-blue-600 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                    </svg>
                    <span class="text-gray-700 font-medium">浏览商品</span>
                  </router-link>
                  <router-link
                    to="/orders"
                    class="flex flex-col items-center p-4 border border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition"
                  >
                    <svg class="w-8 h-8 text-blue-600 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                    <span class="text-gray-700 font-medium">查看订单</span>
                  </router-link>
                </div>
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
import { useRouter } from 'vue-router'
import Header from '@/components/user/Header.vue'
import Footer from '@/components/user/Footer.vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api'
import type { User } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const userData = ref<User>({
  id: userStore.userId,
  username: userStore.username,
  phone: '',
  role: userStore.role,
  createTime: ''
})

const editing = ref(false)
const saving = ref(false)
const editForm = ref({ username: '', phone: '' })

async function fetchUserDetail() {
  try {
    const data = await userApi.getUserById(userStore.userId)
    userData.value = data
    userStore.setUser(data)
  } catch (error) {
    console.error('Failed to fetch user detail:', error)
  }
}

function startEditing() {
  editForm.value = {
    username: userData.value.username,
    phone: userData.value.phone || ''
  }
  editing.value = true
}

function cancelEditing() {
  editing.value = false
}

async function saveProfile() {
  try {
    saving.value = true
    await userApi.updateUser(userStore.userId, {
      username: editForm.value.username,
      phone: editForm.value.phone
    })
    userData.value.username = editForm.value.username
    userData.value.phone = editForm.value.phone
    userStore.username = editForm.value.username
    localStorage.setItem('username', editForm.value.username)
    editing.value = false
    alert('个人信息更新成功')
  } catch (error) {
    console.error('Failed to update profile:', error)
    alert('更新失败，请重试')
  } finally {
    saving.value = false
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}

onMounted(() => {
  fetchUserDetail()
})
</script>
