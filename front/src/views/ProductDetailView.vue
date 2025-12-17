<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router';
    import { getProduct } from '../api/productApi';
    import type { ProductDTO } from '../types/dto/product';

    const product = ref<ProductDTO | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const quantity = ref(1);

    const route = useRoute();
    
    onMounted(() => {
        loadProduct();
    });

    watch(
        () => route.params.id,
        () => {
            loadProduct();
        }
    );

    function getProductIdFromRoute(): number | null {
        const raw = route.params.id;
        const value = Array.isArray(raw) ? raw[0] : raw;
        const id = Number(value);
        return Number.isFinite(id) ? id : null;
    }

    async function loadProduct() {
        try {
            loading.value = true;
            error.value = null;
            const id = getProductIdFromRoute();
            if (id === null) {
                product.value = null;
                error.value = 'Identifiant de produit invalide';
                return;
            }

            product.value = await getProduct(id);
        } catch (e: any) {
            error.value = e.message;
        } finally {
            loading.value = false;
        }
    }

    function addToCart() {
        const q = Math.max(1, Number(quantity.value) || 1);
        quantity.value = q;
    }

</script>

<template>
    <div>
        <p v-if="loading">Loading...</p>
        <p v-else-if="error">{{ error }}</p>

        <div v-else-if="product">
            <div class="detail">
                <div class="detail-media">
                    <img
                        v-if="product.image && product.image.trim().length > 0"
                        class="detail-img"
                        :src="product.image"
                        :alt="product.name"
                    />
                    <div v-else class="detail-img-placeholder"></div>
                </div>

                <div class="detail-info">
                    <h1 class="title">{{ product.name }}</h1>

                    <div class="meta">
                        <span class="category">{{ product.category ?? 'Sans catégorie' }}</span>
                        <span class="price">{{ product.price }} €</span>
                    </div>

                    <p class="description">{{ product.description }}</p>

                    <div class="actions">
                        <label class="qty">
                            Quantité
                            <input type="number" min="1" step="1" v-model.number="quantity" />
                        </label>
                        <button @click="addToCart">Ajouter au panier</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .detail {
        display: grid;
        grid-template-columns: minmax(clamp(220px, 35vw, 420px), 1fr) 2fr;
        gap: clamp(16px, 3vw, 28px);
        align-items: start;
    }

    @media (max-width: 720px) {
        .detail {
            grid-template-columns: 1fr;
        }
    }

    .detail-media {
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        border-radius: clamp(12px, 1.4vw, 16px);
        overflow: hidden;
        aspect-ratio: 1 / 1;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: clamp(10px, 1.8vw, 16px);
    }

    .detail-img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    .detail-img-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, rgba(88, 185, 154, 0.10), rgba(243, 201, 107, 0.18));
    }

    .detail-info {
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        border-radius: clamp(12px, 1.4vw, 16px);
        padding: clamp(14px, 2vw, 18px);
    }

    .title {
        margin: 0 0 clamp(10px, 2vw, 14px) 0;
        font-size: clamp(1.4rem, 2.4vw, 2rem);
        line-height: 1.15;
    }

    .meta {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 10px;
        margin-bottom: clamp(10px, 2vw, 14px);
    }

    .category {
        display: inline-flex;
        align-items: center;
        font-size: clamp(0.78rem, 1.1vw, 0.9rem);
        color: var(--ac-muted);
        padding: 0.28em 0.7em;
        border-radius: 999px;
        background: color-mix(in srgb, var(--ac-primary) 14%, transparent);
        border: 1px solid color-mix(in srgb, var(--ac-primary) 22%, transparent);
    }

    .price {
        font-weight: 800;
        font-size: clamp(1.05rem, 1.8vw, 1.4rem);
        color: var(--ac-primary);
    }

    .description {
        margin: 0 0 clamp(14px, 2.4vw, 18px) 0;
        color: var(--ac-text);
    }

    .actions {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        align-items: end;
    }

    .qty {
        display: grid;
        gap: 6px;
        font-size: clamp(0.85rem, 1.2vw, 0.95rem);
        color: var(--ac-muted);
    }

    .qty input {
        width: clamp(90px, 10vw, 120px);
        padding: 0.55rem 0.7rem;
        border-radius: 12px;
        border: 1px solid var(--ac-border);
        background: var(--ac-surface-2);
        color: var(--ac-text);
    }
</style>

