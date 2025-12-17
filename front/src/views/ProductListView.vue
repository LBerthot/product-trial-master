<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { getProducts } from '../api/productApi';
  import type { ProductDTO } from '../types/dto/product';
  import ProductCard from '../components/ProductCard.vue';

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

    <p v-if="error">{{ error }}</p>

    <div class="product-grid" :class="{ 'is-loading': loading }">
      <router-link
        v-for="product in products"
        :key="product.id"
        class="product-link"
        :to="{ name: 'product-detail', params: { id: product.id } }"
      >
        <ProductCard :product="product" />
      </router-link>
    </div>

    <div class="pagination">
      <button :disabled="loading || page <= 0" @click="previousPage">Precedent</button>
      <span class="page-indicator">{{ page + 1 }} / {{ totalPages }}</span>
      <button :disabled="loading || page >= totalPages - 1" @click="nextPage">Suivant</button>
    </div>

    <p class="sr-only" aria-live="polite">{{ loading ? 'Chargement…' : '' }}</p>
    <div class="loading-overlay" :class="{ visible: loading }" aria-hidden="true">
      <span class="spinner" aria-hidden="true"></span>
    </div>
  </div>
</template>

<style scoped>
  .product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(clamp(160px, 22vw, 240px), 1fr));
    gap: clamp(12px, 2vw, 20px);
    align-items: stretch;
    transition: opacity 160ms ease;
  }

  .product-link {
    text-decoration: none;
    color: inherit;
  }

  .product-grid.is-loading {
    opacity: 0.82;
  }

  .loading-overlay {
    position: fixed;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(234, 246, 239, 0.55);
    backdrop-filter: blur(2px);
    opacity: 0;
    pointer-events: none;
    transition: opacity 160ms ease;
  }

  .loading-overlay.visible {
    opacity: 1;
    pointer-events: auto;
  }

  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border-width: 0;
  }

  .spinner {
    width: clamp(20px, 3vw, 28px);
    height: clamp(20px, 3vw, 28px);
    border-radius: 999px;
    border: 3px solid color-mix(in srgb, var(--ac-primary) 25%, transparent);
    border-top-color: var(--ac-primary);
    animation: spin 650ms linear infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  .pagination {
    margin-top: clamp(12px, 2vw, 20px);
    display: flex;
    justify-content: center;
    gap: clamp(10px, 2vw, 16px);
    align-items: center;
  }

  .page-indicator {
    min-width: 7ch;
    text-align: center;
  }

  .pagination button:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
</style>