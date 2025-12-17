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
            <img v-if="product.image && product.image.trim().length > 0" :src="product.image" :alt="product.name" />
            <h1>{{ product.name }}</h1>
            <p>{{ product.description }}</p>
            <p>{{ product.price }}</p>
            <p>{{ product.category }}</p>

            <label>
                Quantité
                <input type="number" min="1" step="1" v-model.number="quantity" />
            </label>
            <button @click="addToCart">Ajouter au panier</button>
        </div>
    </div>
</template>

