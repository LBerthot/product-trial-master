<script setup lang="ts">
    import { onMounted } from 'vue';
    import { useWishlistStore } from '../stores/wishlistStore';

    const wishlistStore = useWishlistStore();
    onMounted(() => {
        wishlistStore.loadWishlist();
    });
</script>

<template>
    <div class="cart-page" :class="{ 'is-loading': wishlistStore.loading }">
        <div class="cart-main">
            <div class="cart-header">
                <h1 class="cart-title">Liste de souhait</h1>
                <p class="cart-subtitle" v-if="wishlistStore.lines.length > 0">
                    {{ wishlistStore.lines.length }} produit(s) en liste de souhait
                </p>
            </div>

            <p v-if="wishlistStore.error" class="cart-alert">{{ wishlistStore.error }}</p>

            <div v-if="wishlistStore.unauthorized" class="cart-empty">
                <p>Connecte-toi pour voir ton panier.</p>
                <router-link class="cart-link" :to="{ name: 'login', query: { redirect: '/cart' } }">Aller à la connexion</router-link>
            </div>

            <div v-else-if="wishlistStore.lines.length === 0" class="cart-empty">
                <p>Votre liste de souhait est vide.</p>
                <router-link class="cart-link" to="/products">Découvrir des produits</router-link>
            </div>

            <div v-else class="cart-list" role="list">
                <article v-for="line in wishlistStore.lines" :key="line.item.id" class="cart-item" role="listitem">
                    <div class="cart-item-media">
                        <img
                            v-if="line.product.image && line.product.image.trim().length > 0"
                            class="cart-item-img"
                            :src="line.product.image"
                            :alt="line.product.name"
                        />
                        <div v-else class="cart-item-img-placeholder"></div>
                    </div>

                    <div class="cart-item-body">
                        <div class="cart-item-top">
                            <div>
                                <h2 class="cart-item-title">{{ line.product.name }}</h2>
                                <p class="cart-item-meta">{{ line.product.category ?? 'Sans catégorie' }}</p>
                            </div>
                            <div class="cart-item-price">
                                <div class="cart-item-unit">{{ line.product.price }} €</div>
                            </div>
                        </div>

                        <div class="cart-item-actions">
                            <button class="cart-remove" @click="wishlistStore.removeFromWishlist(line.item.id)">Enveler de la wishlist</button>
                        </div>
                    </div>
                </article>
            </div>
        </div>

        <p class="sr-only" aria-live="polite">{{ wishlistStore.loading ? 'Chargement…' : '' }}</p>
        <div class="loading-overlay" :class="{ visible: wishlistStore.loading }" aria-hidden="true">
        <span class="spinner" aria-hidden="true"></span>
        </div>
  </div>

</template>

<style scoped>
    .cart-page {
        display: grid;
        gap: clamp(12px, 2vw, 18px);
        align-items: start;
    }

    @media (max-width: 860px) {
        .cart-page {
            grid-template-columns: 1fr;
        }
    }

    .cart-main {
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        border-radius: 16px;
        padding: clamp(14px, 2vw, 18px);
        box-shadow: var(--ac-shadow);
    }

    .cart-header {
        display: flex;
        align-items: baseline;
        justify-content: space-between;
        gap: 12px;
        margin-bottom: 12px;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--ac-border);
    }

    .cart-title {
        margin: 0;
        font-size: clamp(1.4rem, 2.4vw, 1.9rem);
    }

    .cart-subtitle {
        margin: 0;
        color: var(--ac-muted);
        font-size: 0.95rem;
    }

    .cart-alert {
        margin: 10px 0 14px 0;
        padding: 10px 12px;
        border-radius: 12px;
        border: 1px solid color-mix(in srgb, var(--ac-accent) 50%, var(--ac-border));
        background: color-mix(in srgb, var(--ac-accent) 18%, transparent);
    }

    .cart-empty {
        padding: 16px;
        border-radius: 14px;
        border: 1px dashed var(--ac-border);
        background: var(--ac-surface-2);
    }

    .cart-link {
        display: inline-flex;
        margin-top: 10px;
        font-weight: 600;
    }

    .cart-list {
        display: grid;
        gap: 12px;
    }

    .cart-item {
        display: grid;
        grid-template-columns: 120px 1fr;
        gap: 16px;
        padding: 12px 14px;
        border: 1px solid var(--ac-border);
        border-radius: 14px;
        background: var(--ac-surface-2);
    }

    .cart-item-media {
        width: 108px;
        height: 108px;
        border-radius: 12px;
        overflow: hidden;
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 8px;
    }

    .cart-item-img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    .cart-item-img-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, rgba(88, 185, 154, 0.10), rgba(243, 201, 107, 0.18));
    }

    .cart-item-body {
        display: grid;
        gap: 10px;
    }

    .cart-item-top {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        align-items: start;
    }

    .cart-item-title {
        margin: 0;
        font-size: 1rem;
        line-height: 1.25;
    }

    .cart-item-meta {
        margin: 4px 0 0 0;
        color: var(--ac-muted);
        font-size: 0.9rem;
    }

    .cart-item-price {
        text-align: right;
        display: grid;
        gap: 2px;
        min-width: 88px;
    }

    .cart-item-unit {
        color: var(--ac-muted);
        font-size: 0.9rem;
    }

    .cart-item-line {
        font-weight: 900;
        color: var(--ac-text);
    }

    .cart-item-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 12px;
    }

    .cart-qty {
        display: inline-flex;
        align-items: center;
        gap: 10px;
        padding: 0.35rem 0.75rem;
        border-radius: 999px;
        border: 1px solid var(--ac-border);
        background: var(--ac-surface);
    }

    .cart-qty-label {
        color: var(--ac-muted);
        font-size: 0.85rem;
    }

    .cart-qty-value {
        font-weight: 800;
    }

    .cart-remove {
        background: var(--ac-accent);
        border-color: color-mix(in srgb, var(--ac-primary) 45%, var(--ac-border));
    }

    .cart-summary {
        position: sticky;
        top: 16px;
    }

    .summary-card {
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        border-radius: 16px;
        padding: 14px;
        box-shadow: var(--ac-shadow);
        display: grid;
        gap: 10px;
    }

    .summary-row {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        font-size: 1.05rem;
        font-weight: 800;
    }

    .summary-hint {
        margin: 0;
        color: var(--ac-muted);
        font-size: 0.9rem;
    }

    .summary-cta {
        width: 100%;
        background: var(--ac-primary);
        border-color: color-mix(in srgb, var(--ac-primary-hover) 65%, var(--ac-border));
        font-weight: 800;
    }

    .summary-cta:hover {
        border-color: color-mix(in srgb, var(--ac-primary-hover) 55%, var(--ac-border));
    }

    .summary-cta:disabled {
        opacity: 0.55;
        cursor: not-allowed;
    }

    .summary-link {
        text-align: center;
        font-weight: 600;
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
</style>