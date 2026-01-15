<script setup lang="ts">
    import { onMounted, ref, watch } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { getProduct } from '../api/productApi';
    import { updateProduct, deleteProduct } from '../api/adminProductsApi';
    import type { ProductDTO } from '../types/dto/product';
    import { useCartStore } from '../stores/cartStore';
    import { useAuthStore } from '../stores/authStore';
    import { useWishlistStore } from '../stores/wishlistStore';
    import ToggleButton from '../components/ToggleButton.vue';

    const authStore = useAuthStore();

    const product = ref<ProductDTO | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const quantity = ref(1);

    const isEditing = ref(false);
    const editForm = ref({ code: '', name: '', price: 0, description: '' });
    const editError = ref<string | null>(null);
    const editLoading = ref(false);
    const deleteLoading = ref(false);

    const route = useRoute();
    const router = useRouter();
    const cartStore = useCartStore();
    const wishlistStore = useWishlistStore();
    
    onMounted(() => {
        loadProduct();
        if (authStore.isAuthenticated()) {
            wishlistStore.loadWishlist();
        }
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

    async function addToCart() {
        if (!product.value) return;
        if (!authStore.isAuthenticated()) {
            router.push({ name: 'login', query: { redirect: route.fullPath } });
            return;
        }
        const q = Math.max(1, Number(quantity.value) || 1);
        quantity.value = q;
        await cartStore.addItem(product.value.id, q);
    }

    async function onWishlistToggle() {
        if (!product.value) return;
        if (!authStore.isAuthenticated()) {
            router.push({ name: 'login', query: { redirect: route.fullPath } });
            return;
        }
        await wishlistStore.toggle(product.value.id);
    }

    function startEdit() {
        if (!product.value) return;
        editForm.value = {
            code: product.value.code,
            name: product.value.name,
            price: product.value.price,
            description: product.value.description ?? ''
        };
        editError.value = null;
        isEditing.value = true;
    }

    function cancelEdit() {
        isEditing.value = false;
        editError.value = null;
    }

    async function submitEdit() {
        if (!product.value) return;
        if (!editForm.value.code || !editForm.value.name || editForm.value.price <= 0) {
            editError.value = 'Code, nom et prix (> 0) sont obligatoires.';
            return;
        }
        try {
            editLoading.value = true;
            editError.value = null;
            const updated = await updateProduct(product.value.id, editForm.value);
            product.value = updated;
            isEditing.value = false;
        } catch (e: any) {
            if (e?.response?.status === 401) {
                router.push({ name: 'login', query: { redirect: route.fullPath } });
            } else if (e?.response?.status === 403) {
                editError.value = 'Accès refusé : admin uniquement.';
            } else {
                editError.value = e?.response?.data?.message || e.message || 'Erreur lors de la modification.';
            }
        } finally {
            editLoading.value = false;
        }
    }

    async function confirmDelete() {
        if (!product.value) return;
        if (!confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) return;
        try {
            deleteLoading.value = true;
            await deleteProduct(product.value.id);
            router.push({ name: 'products' });
        } catch (e: any) {
            if (e?.response?.status === 401) {
                router.push({ name: 'login', query: { redirect: route.fullPath } });
            } else if (e?.response?.status === 403) {
                error.value = 'Accès refusé : admin uniquement.';
            } else {
                error.value = e?.response?.data?.message || e.message || 'Erreur lors de la suppression.';
            }
        } finally {
            deleteLoading.value = false;
        }
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
                        <ToggleButton
                            v-if="product"
                            :model-value="wishlistStore.isInWishlist(product.id)"
                            inactive-label="Ajouter à la wishlist"
                            active-label="Enlever de la wishlist"
                            :disabled="wishlistStore.loading"
                            @toggle="onWishlistToggle"
                        />
                    </div>

                    <div v-if="authStore.isAdmin()" class="admin-section">
                        <h3>Administration</h3>
                        
                        <div v-if="!isEditing" class="admin-buttons">
                            <button @click="startEdit" class="btn-edit">Modifier</button>
                            <button @click="confirmDelete" :disabled="deleteLoading" class="btn-delete">
                                {{ deleteLoading ? 'Suppression...' : 'Supprimer' }}
                            </button>
                        </div>

                        <div v-else class="edit-form">
                            <p v-if="editError" class="form-error">{{ editError }}</p>
                            <label>
                                Code
                                <input v-model="editForm.code" type="text" />
                            </label>
                            <label>
                                Nom
                                <input v-model="editForm.name" type="text" />
                            </label>
                            <label>
                                Prix
                                <input v-model.number="editForm.price" type="number" min="0.01" step="0.01" />
                            </label>
                            <label>
                                Description
                                <textarea v-model="editForm.description" rows="3"></textarea>
                            </label>
                            <div class="form-buttons">
                                <button @click="submitEdit" :disabled="editLoading">
                                    {{ editLoading ? 'Enregistrement...' : 'Enregistrer' }}
                                </button>
                                <button @click="cancelEdit" type="button" class="btn-cancel">Annuler</button>
                            </div>
                        </div>
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

    .admin-section {
        margin-top: clamp(16px, 2.5vw, 24px);
        padding-top: clamp(14px, 2vw, 18px);
        border-top: 1px solid var(--ac-border);
    }

    .admin-section h3 {
        margin: 0 0 12px 0;
        font-size: 1rem;
        color: var(--ac-muted);
    }

    .admin-buttons {
        display: flex;
        gap: 10px;
    }

    .btn-edit {
        background: var(--ac-primary);
        color: white;
    }

    .btn-delete {
        background: #c0392b;
        color: white;
    }

    .btn-delete:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .edit-form {
        display: grid;
        gap: 12px;
        max-width: 400px;
    }

    .edit-form label {
        display: grid;
        gap: 4px;
        font-size: 0.9rem;
        color: var(--ac-muted);
    }

    .edit-form input,
    .edit-form textarea {
        padding: 0.5rem 0.7rem;
        border-radius: 8px;
        border: 1px solid var(--ac-border);
        background: var(--ac-surface-2);
        color: var(--ac-text);
        font-family: inherit;
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

