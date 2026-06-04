import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginResponse } from '@/types'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string>(localStorage.getItem('token') || '')
  const userId = ref<number>(Number(localStorage.getItem('userId') || 0))
  const username = ref<string>(localStorage.getItem('username') || '')
  const role = ref<number>(Number(localStorage.getItem('role') || 0))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 0)

  function setUser(userData: User) {
    user.value = userData
  }

  function setAuthData(loginResponse: LoginResponse) {
    token.value = loginResponse.token
    user.value = loginResponse.userInfo
    userId.value = loginResponse.userInfo.id
    username.value = loginResponse.userInfo.username
    role.value = loginResponse.userInfo.role ?? 1

    localStorage.setItem('token', loginResponse.token)
    localStorage.setItem('userId', String(loginResponse.userInfo.id))
    localStorage.setItem('username', loginResponse.userInfo.username)
    localStorage.setItem('role', String(loginResponse.userInfo.role ?? 1))
  }

  function logout() {
    user.value = null
    token.value = ''
    userId.value = 0
    username.value = ''
    role.value = 0

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return {
    user,
    token,
    userId,
    username,
    role,
    isLoggedIn,
    isAdmin,
    setUser,
    setAuthData,
    logout
  }
})
