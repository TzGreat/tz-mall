<template>
  <AdminLayout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-800">用户管理</h2>
        <button @click="showAddModal = true" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
          添加用户
        </button>
      </div>

      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div v-if="loading" class="flex justify-center py-12">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
        <div v-else-if="users.length > 0">
          <table class="w-full">
            <thead>
              <tr class="text-left text-gray-500 text-sm border-b border-gray-200 bg-gray-50">
                <th class="px-6 py-3 font-medium">ID</th>
                <th class="px-6 py-3 font-medium">用户名</th>
                <th class="px-6 py-3 font-medium">手机号</th>
                <th class="px-6 py-3 font-medium">角色</th>
                <th class="px-6 py-3 font-medium">创建时间</th>
                <th class="px-6 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 text-gray-800">{{ user.id }}</td>
                <td class="px-6 py-4 text-gray-800 font-medium">{{ user.username }}</td>
                <td class="px-6 py-4 text-gray-600">{{ user.phone || '-' }}</td>
                <td class="px-6 py-4">
                  <span :class="user.role === 0 ? 'bg-purple-100 text-purple-600' : 'bg-gray-100 text-gray-600'" class="px-2 py-1 rounded-full text-xs font-medium">
                    {{ user.role === 0 ? '管理员' : '普通用户' }}
                  </span>
                </td>
                <td class="px-6 py-4 text-gray-500 text-sm">{{ formatDate(user.createTime) }}</td>
                <td class="px-6 py-4">
                  <div class="flex gap-3">
                    <button @click="viewUserDetail(user)" class="text-blue-600 hover:text-blue-700 text-sm">
                      详情
                    </button>
                    <button @click="confirmDeleteUser(user)" class="text-red-600 hover:text-red-700 text-sm">
                      删除
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-center py-12 text-gray-500">
          暂无用户数据
        </div>
      </div>
    </div>

    <!-- Add User Modal -->
    <div v-if="showAddModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <h3 class="text-xl font-bold text-gray-800 mb-4">添加用户</h3>
        <form @submit.prevent="addUser">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
              <input v-model="newUser.username" type="text" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
              <input v-model="newUser.password" type="password" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
              <input v-model="newUser.phone" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">角色</label>
              <select v-model.number="newUser.role" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500">
                <option :value="1">普通用户</option>
                <option :value="0">管理员</option>
              </select>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button type="button" @click="showAddModal = false" class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
              取消
            </button>
            <button type="submit" :disabled="saving" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50">
              {{ saving ? '添加中...' : '添加' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- User Detail Modal -->
    <div v-if="detailUser" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <h3 class="text-xl font-bold text-gray-800 mb-4">用户详情</h3>
        <div class="space-y-3">
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-gray-500">ID</span>
            <span class="text-gray-900 font-medium">{{ detailUser.id }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-gray-500">用户名</span>
            <span class="text-gray-900 font-medium">{{ detailUser.username }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-gray-500">手机号</span>
            <span class="text-gray-900 font-medium">{{ detailUser.phone || '-' }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-gray-500">角色</span>
            <span class="text-gray-900 font-medium">{{ detailUser.role === 0 ? '管理员' : '普通用户' }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <span class="text-gray-500">创建时间</span>
            <span class="text-gray-900 font-medium">{{ formatDate(detailUser.createTime) }}</span>
          </div>
        </div>
        <div class="mt-6 flex justify-end">
          <button @click="detailUser = null" class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
            关闭
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="deleteTarget" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-full max-w-sm">
        <h3 class="text-xl font-bold text-gray-800 mb-2">确认删除</h3>
        <p class="text-gray-600 mb-6">确定要删除用户「{{ deleteTarget.username }}」吗？此操作不可恢复。</p>
        <div class="flex justify-end gap-3">
          <button @click="deleteTarget = null" class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
            取消
          </button>
          <button @click="deleteUser" :disabled="deleting" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 disabled:opacity-50">
            {{ deleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AdminLayout from '@/components/admin/AdminLayout.vue'
import { userApi } from '@/api'
import type { User } from '@/types'

const loading = ref(true)
const users = ref<User[]>([])
const showAddModal = ref(false)
const saving = ref(false)
const newUser = ref({
  username: '',
  password: '',
  phone: '',
  role: 1
})
const detailUser = ref<User | null>(null)
const deleteTarget = ref<User | null>(null)
const deleting = ref(false)

async function fetchUsers() {
  try {
    loading.value = true
    const data = await userApi.getUserList(1, 100)
    users.value = data.records
  } catch (error) {
    console.error('Failed to fetch users:', error)
  } finally {
    loading.value = false
  }
}

async function addUser() {
  try {
    saving.value = true
    await userApi.adminCreateUser(newUser.value)
    showAddModal.value = false
    newUser.value = { username: '', password: '', phone: '', role: 1 }
    await fetchUsers()
    alert('用户添加成功')
  } catch (error) {
    console.error('Failed to add user:', error)
    alert('添加用户失败')
  } finally {
    saving.value = false
  }
}

function viewUserDetail(user: User) {
  detailUser.value = user
}

function confirmDeleteUser(user: User) {
  deleteTarget.value = user
}

async function deleteUser() {
  if (!deleteTarget.value) return

  try {
    deleting.value = true
    await userApi.deleteUser(deleteTarget.value.id)
    deleteTarget.value = null
    await fetchUsers()
    alert('用户删除成功')
  } catch (error) {
    console.error('Failed to delete user:', error)
    alert('删除用户失败')
  } finally {
    deleting.value = false
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUsers()
})
</script>
