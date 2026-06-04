import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Order } from '@/types'

export const useOrderStore = defineStore('order', () => {
  const currentOrder = ref<Order | null>(null)
  const orders = ref<Order[]>([])
  const loading = ref(false)

  function setCurrentOrder(order: Order | null) {
    currentOrder.value = order
  }

  function setOrders(orderList: Order[]) {
    orders.value = orderList
  }

  function addOrder(order: Order) {
    orders.value.unshift(order)
  }

  function updateOrderStatus(orderId: number, status: number) {
    const order = orders.value.find(o => o.id === orderId)
    if (order) {
      order.status = status
    }
    if (currentOrder.value && currentOrder.value.id === orderId) {
      currentOrder.value.status = status
    }
  }

  function setLoading(value: boolean) {
    loading.value = value
  }

  return {
    currentOrder,
    orders,
    loading,
    setCurrentOrder,
    setOrders,
    addOrder,
    updateOrderStatus,
    setLoading
  }
})
