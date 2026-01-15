<script setup lang="ts">
  import { onMounted, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { getProducts } from '../api/productApi';
  import { createProduct } from '../api/adminProductsApi';
  import type { ProductDTO } from '../types/dto/product';
  import ProductCard from '../components/ProductCard.vue';
  import ToggleButton from '../components/ToggleButton.vue';
  import { useWishlistStore } from '../stores/wishlistStore';
  import { useAuthStore } from '../stores/authStore';

  const products = ref<ProductDTO[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const page = ref(0);
  const size = ref(10);
  const totalPages = ref(0);

  const showAddForm = ref(false);
  const newProduct = ref({ code: '', name: '', price: 0 });
  const addError = ref<string | null>(null);
  const addLoading = ref(false);

  const route = useRoute();
  const router = useRouter();
  const wishlistStore = useWishlistStore();
  const authStore = useAuthStore();

  onMounted(() => {
    loadProducts();
    if (authStore.isAuthenticated()) {
      wishlistStore.loadWishlist();
    }
  });

  async function onWishlistToggle(productId: number) {
    if (!authStore.isAuthenticated()) {
      router.push({ name: 'login', query: { redirect: route.fullPath } });
      return;
    }
    await wishlistStore.toggle(productId);
  }

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

  function openAddForm() {
    newProduct.value = { code: '', name: '', price: 0 };
    addError.value = null;
    showAddForm.value = true;
  }

  function cancelAddForm() {
    showAddForm.value = false;
    addError.value = null;
  }

  async function submitNewProduct() {
    if (!newProduct.value.code || !newProduct.value.name || newProduct.value.price <= 0) {
      addError.value = 'Code, nom et prix (> 0) sont obligatoires.';
      return;
    }
    try {
      addLoading.value = true;
      addError.value = null;
      await createProduct(newProduct.value);
      showAddForm.value = false;
      await loadProducts();
    } catch (e: any) {
      if (e?.response?.status === 401) {
        router.push({ name: 'login', query: { redirect: route.fullPath } });
      } else if (e?.response?.status === 403) {
        addError.value = 'Accès refusé : admin uniquement.';
      } else {
        addError.value = e?.response?.data?.message || e.message || 'Erreur lors de la création.';
      }
    } finally {
      addLoading.value = false;
    }
  }
</script>

<template>
  <div>
    <h1>Product List</h1>

    <div v-if="authStore.isAdmin()" class="admin-actions">
      <button v-if="!showAddForm" @click="openAddForm" class="btn-add">Ajouter un produit</button>
      
      <div v-if="showAddForm" class="add-form">
        <h2>Nouveau produit</h2>
        <p v-if="addError" class="form-error">{{ addError }}</p>
        <label>
          Code
          <input v-model="newProduct.code" type="text" placeholder="CODE001" />
        </label>
        <label>
          Nom
          <input v-model="newProduct.name" type="text" placeholder="Nom du produit" />
        </label>
        <label>
          Prix
          <input v-model.number="newProduct.price" type="number" min="0.01" step="0.01" />
        </label>
        <div class="form-buttons">
          <button @click="submitNewProduct" :disabled="addLoading">Créer</button>
          <button @click="cancelAddForm" type="button" class="btn-cancel">Annuler</button>
        </div>
      </div>
    </div>

    <p v-if="error">{{ error }}</p>

    <div class="product-grid" :class="{ 'is-loading': loading }">
      <div v-for="product in products" :key="product.id" class="product-item">
        <router-link
          class="product-link"
          :to="{ name: 'product-detail', params: { id: product.id } }"
        >
          <ProductCard :product="product" />
        </router-link>
      </div>
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

  .product-item {
    display: grid;
    gap: 10px;
  }

  .product-actions {
    display: flex;
    justify-content: center;
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

  .admin-actions {
    margin-bottom: clamp(14px, 2vw, 20px);
    padding: clamp(12px, 2vw, 16px);
    background: var(--ac-surface);
    border: 1px solid var(--ac-border);
    border-radius: clamp(10px, 1.2vw, 14px);
  }

  .btn-add {
    background: var(--ac-primary);
    color: white;
    font-weight: 600;
  }

  .add-form {
    display: grid;
    gap: 12px;
    max-width: 400px;
  }

  .add-form h2 {
    margin: 0;
    font-size: 1.1rem;
  }

  .add-form label {
    display: grid;
    gap: 4px;
    font-size: 0.9rem;
    color: var(--ac-muted);
  }

  .add-form input {
    padding: 0.5rem 0.7rem;
    border-radius: 8px;
    border: 1px solid var(--ac-border);
    background: var(--ac-surface-2);
    color: var(--ac-text);
  }

  .form-buttons {
    display: flex;
    gap: 10px;
    margin-top: 8px;
  }

  .btn-cancel {
    background: transparent;
    border: 1px solid var(--ac-border);
    color: var(--ac-muted);
  }

  .form-error {
    color: #c0392b;
    font-size: 0.9rem;
    margin: 0;
  }
</style>