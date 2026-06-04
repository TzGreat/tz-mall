import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  // 初始化购物车
  const savedCart = localStorage.getItem('cart')
  const cartItems = ref<CartItem[]>(savedCart ? JSON.parse(savedCart) : [])

  const totalPrice = computed(() => {
    return cartItems.value
      .filter(item => item.checked)
      .reduce((sum, item) => sum + item.itemPrice * item.num, 0)
  })

  const totalCount = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.num, 0)
  })

  const checkedItems = computed(() => {
    return cartItems.value.filter(item => item.checked)
  })

  function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cartItems.value))
  }

  function addToCart(item: CartItem) {
    const existingItem = cartItems.value.find(i => i.itemId === item.itemId)
    if (existingItem) {
      existingItem.num += item.num
    } else {
      cartItems.value.push({ ...item, checked: true })
    }
    saveCart()
  }

  function removeFromCart(itemId: number) {
    const index = cartItems.value.findIndex(i => i.itemId === itemId)
    if (index > -1) {
      cartItems.value.splice(index, 1)
      saveCart()
    }
  }

  function updateQuantity(itemId: number, num: number) {
    const item = cartItems.value.find(i => i.itemId === itemId)
    if (item) {
      item.num = Math.max(1, num)
      saveCart()
    }
  }

  function toggleCheck(itemId: number) {
    const item = cartItems.value.find(i => i.itemId === itemId)
    if (item) {
      item.checked = !item.checked
      saveCart()
    }
  }

  function toggleAllCheck(checked: boolean) {
    cartItems.value.forEach(item => {
      item.checked = checked
    })
    saveCart()
  }

  function clearCheckedItems() {
    cartItems.value = cartItems.value.filter(item => !item.checked)
    saveCart()
  }

  function clearCart() {
    cartItems.value = []
    saveCart()
  }

  return {
    cartItems,
    totalPrice,
    totalCount,
    checkedItems,
    addToCart,
    removeFromCart,
    updateQuantity,
    toggleCheck,
    toggleAllCheck,
    clearCheckedItems,
    clearCart
  }
})
