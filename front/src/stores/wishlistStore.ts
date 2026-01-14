import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { addToWishlist as addToWishlistApi, getWishlist as getWishlistApi, removeFromWishlist as removeFromWishlistApi } from '../api/wishlistApi';
import { getProduct } from '../api/productApi';
import type { WishlistItemDTO } from '../types/dto/wishlist';
import type { ProductDTO } from '../types/dto/product';
import { useAuthStore } from './authStore';
import type { WishlistLine } from '../types/wishlistLine';

export const useWishlistStore = defineStore('wishlist', () => {
    const authStore = useAuthStore();
    const wishlistItems = ref<WishlistItemDTO[]>([]);
    const productsById = ref<Record<number, ProductDTO>>({});

    const loading = ref(false);
    const error = ref<string | null>(null);
    const unauthorized = ref(false);

    const page = ref(0);
    const size = ref(50);
    const totalPages = ref(0);

    const lines = computed<WishlistLine[]>(() => {
        return wishlistItems.value.map((item) => {
            const product = productsById.value[item.productId];
            if (!product) return null;
            return {
                item,
                product
            } satisfies WishlistLine;
        })
        .filter((line): line is WishlistLine => line !== null);
    });
    
    async function loadWishlist(params: { page?: number; size?: number } = {}) {
        try {
            loading.value = true;
            error.value = null;
            unauthorized.value = false;

            if (typeof params.page === 'number') page.value = params.page;
            if  (typeof params.size === 'number') size.value = params.size;

            const result = await getWishlistApi({ page: page.value, size: size.value });
            totalPages.value = result.totalPages;

            const items = result.content;
            const uniqueProductIds = Array.from(new Set(items.map((i) => i.productId)));
            const missingProductIds = uniqueProductIds.filter((id) => !productsById.value[id]);

            if  (missingProductIds.length > 0) {
                const products = await Promise.all(missingProductIds.map((id) => getProduct(id)));
                for (const product of products) {
                    productsById.value[product.id] = product;
                }
            }
            wishlistItems.value = items;
            
        } catch (e: any) {
            if (e?.response?.status === 401){
                authStore.logout();
                unauthorized.value = true;
                wishlistItems.value = [];
                error.value = 'Session expirée, veuillez vous reconnecter.';
                return;
            }
            error.value = e?.message ?? 'Erreur lors du chargement de la liste des souhaits';
        } finally {
            loading.value = false;
        }
    }

    async function addToWishlist(productId: number) {
        try {
            loading.value = true;
            error.value = null;
            const created = await addToWishlistApi(productId);
            if (!wishlistItems.value.some((item) => item.id === created.id)) {
                wishlistItems.value = [created, ...wishlistItems.value];
            }
        } catch (e: any) {
            if  (e?.response?.status === 409){
                error.value = 'Le produit est déjà dans votre liste des souhaits';
                return;
            }
            error.value = e?.message ?? 'Erreur lors de l\'ajout au panier';
        } finally {
            loading.value = false;
            await loadWishlist();
        }
    }

    async function removeFromWishlist(id: number) {
        try {
            loading.value = true;
            error.value = null;
            wishlistItems.value = wishlistItems.value.filter((item) => item.id !== id);
            await removeFromWishlistApi(id);
        } finally {
            loading.value = false;
            await loadWishlist();
        }
    }

    function isInWishlist(productId: number) {
        return wishlistItems.value.some((item) => item.productId === productId);
    }

    function getWislistItemByProductId(productId: number) {
        return wishlistItems.value.find((item) => item.productId === productId);
    }

    async function toggle(productId: number) {
        if (isInWishlist(productId)) {
            const item = getWislistItemByProductId(productId);
            if (!item) return;
            await removeFromWishlist(item.id);
            return;
        }
        await addToWishlist(productId);
    }

    return {
        wishlistItems,
        productsById,
        lines,
        loading,
        error,
        unauthorized,
        page,
        size,
        totalPages,
        loadWishlist,
        addToWishlist,
        removeFromWishlist,
        isInWishlist,
        getWislistItemByProductId,
        toggle,
    }
});