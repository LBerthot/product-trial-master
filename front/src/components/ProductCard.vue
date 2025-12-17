<script setup lang="ts">
    import type { ProductDTO } from '../types/dto/product';

    defineProps<{ product: ProductDTO }>();
</script>

<template>
    <article class="card">
        <div class="media">
            <img
                v-if="product.image && product.image.trim().length > 0"
                class="img"
                :src="product.image"
                :alt="product.name"
            />
            <div v-else class="img-placeholder"></div>
        </div>

        <div class="body">
            <div class="category">{{ product.category ?? 'Sans catégorie' }}</div>
            <h3 class="title">{{ product.name }}</h3>
            <div class="price">{{ product.price }} €</div>
        </div>
    </article>
</template>


<style scoped>
    .card {
        background: var(--ac-surface);
        border: 1px solid var(--ac-border);
        border-radius: clamp(10px, 1.2vw, 14px);
        overflow: hidden;
        transition: transform 140ms ease, box-shadow 140ms ease, border-color 140ms ease;
    }

    .card:hover {
        transform: translateY(-2px);
        box-shadow: var(--ac-shadow);
        border-color: color-mix(in srgb, var(--ac-primary) 35%, var(--ac-border));
    }

    .media {
        background: var(--ac-surface-2);
        aspect-ratio: 4 / 3;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: clamp(8px, 1.2vw, 12px);
    }

    .img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    .img-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, rgba(88, 185, 154, 0.10), rgba(243, 201, 107, 0.18));
    }

    .body {
        padding: clamp(10px, 1.5vw, 14px);
    }

    .category {
        display: inline-flex;
        align-items: center;
        font-size: clamp(0.72rem, 1.0vw, 0.82rem);
        color: var(--ac-muted);
        margin-bottom: clamp(6px, 1vw, 10px);
        padding: 0.22em 0.6em;
        border-radius: 999px;
        background: color-mix(in srgb, var(--ac-primary) 14%, transparent);
        border: 1px solid color-mix(in srgb, var(--ac-primary) 22%, transparent);
    }

    .title {
        font-size: clamp(0.92rem, 1.25vw, 1.02rem);
        margin: 0 0 clamp(8px, 1.2vw, 12px) 0;
        line-height: 1.25;
        display: -webkit-box;
        line-clamp: 2;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        color: var(--ac-text);
    }

    .price {
        font-size: clamp(1.0rem, 1.6vw, 1.2rem);
        font-weight: 800;
        color: var(--ac-primary);
    }
</style>