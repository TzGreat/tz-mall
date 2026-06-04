<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4">
    <div class="max-w-md w-full">
      <div class="bg-white rounded-2xl shadow-xl p-8">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-800 mb-2">TZ-Mall</h1>
          <p class="text-gray-600">欢迎回来</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-6">
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
              用户名
            </label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="请输入用户名"
            />
          </div>

          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
              密码
            </label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="请输入密码"
            />
          </div>

          <div class="flex items-center">
            <input id="adminLogin" v-model="adminMode" type="checkbox" class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500">
            <label for="adminLogin" class="ml-2 text-sm text-gray-600">以管理员身份登录</label>
          </div>

          <div v-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg text-sm">
            {{ error }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 text-white py-3 px-4 rounded-lg font-medium hover:bg-blue-700 focus:ring-4 focus:ring-blue-200 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading">登录中...</span>
            <span v-else>{{ adminMode ? '管理员登录' : '登录' }}</span>
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-gray-600">
            还没有账号?
            <router-link to="/register" class="text-blue-600 hover:text-blue-700 font-medium">
              立即注册
            </router-link>
          </p>
        </div>

        <div class="mt-8 pt-6 border-t border-gray-200">
          <p class="text-sm text-gray-500 text-center">
            测试账号：管理员 admin/123456，普通用户 user1/123456
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api'
import type { LoginRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const form = ref<LoginRequest>({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')
const adminMode = ref(false)

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    const response = await userApi.login(form.value)
    userStore.setAuthData(response)

    if (adminMode.value) {
      if (!userStore.isAdmin) {
        error.value = '该账号不是管理员，无法以管理员身份登录'
        userStore.logout()
        loading.value = false
        return
      }
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (err: any) {
    error.value = err.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>
