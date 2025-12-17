<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { getProducts } from '../api/productApi';
  import type { ProductDTO } from '../types/dto/product';

  const products = ref<ProductDTO[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const page = ref(0);
  const size = ref(10);
  const totalPages = ref(0);

  onMounted(() => {
    loadProducts();
  });

  async function loadProducts() {
    try {
      loading.value = true;
      error.value = null;
      const result = await getProducts({ page: page.value, size: size.value });
      products.value = result.content;
      totalPages.value = result.totalPages;
    } catch (e: any) {
      error.value = e.message;
    } finally {
      loading.value = false;
    }  
  }

  async function previousPage() {
    if (page.value <= 0) return;
    page.value -= 1;
    await loadProducts();
  }

  async function nextPage() {
    if (page.value >= totalPages.value - 1) return;
    page.value += 1;
    await loadProducts();
  }

</script>

<template>
  <div>
    <h1>Product List</h1>

    <p v-if="loading">Loading...</p>
    <p v-else-if="error">{{ error }}</p>
    <div v-else>
      <ul>
        <li v-for="product in products" :key="product.id">
          <router-link :to="{name : 'product-detail', params: {id: product.id}}">
            <img v-if="product.image && product.image.trim().length > 0" :src="product.image" :alt="product.name" /> {{ product.category }}
            {{ product.name }} {{ product.price }}
          </router-link>
        </li>
      </ul>

      <button v-if="page > 0" @click="previousPage">Precedent</button>
      <span>{{ page + 1 }} / {{ totalPages }}</span>
      <button v-if="page < totalPages - 1" @click="nextPage">Suivant</button>
    </div>
  </div>
</template>