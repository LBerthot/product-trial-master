import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { addToCart as addToCartApi, getCart as getCartApi, removeCartItem as removeCartItemApi } from '../api/cartApi';
import { getProduct } from '../api/productApi';
import type { CartLine } from '../types/cartLine';
import type { CartItemDTO } from '../types/dto/cart';
import type { ProductDTO } from '../types/dto/product';
import { useAuthStore } from './authStore';

export const useCartStore = defineStore('cart', () => {
    const authStore = useAuthStore();
    const cartItems = ref<CartItemDTO[]>([]);
    const productsById = ref<Record<number, ProductDTO>>({});

    const loading = ref(false);
    const error = ref<string | null>(null);
    const unauthorized = ref(false);

    const page = ref(0);
    const size = ref(50);
    const totalPages = ref(0);

    const lines = computed<CartLine[]>(() => {
        return cartItems.value
            .map((item) => {
                const product = productsById.value[item.productId];
                if (!product) return null;
                return {
                    item,
                    product,
                    lineTotal: product.price * item.quantity,
                } satisfies CartLine;
            })
            .filter((line): line is CartLine => line !== null);
    });

    const total = computed(() => {
        return lines.value.reduce((sum, line) => sum + line.lineTotal, 0);
    });

    async function loadCart(params: { page?: number; size?: number } = {}) {
        try {
            loading.value = true;
            error.value = null;
            unauthorized.value = false;

            if (typeof params.page === 'number') page.value = params.page;
            if (typeof params.size === 'number') size.value = params.size;

            const result = await getCartApi({ page: page.value, size: size.value });
            totalPages.value = result.totalPages;

            const items = result.content;
            const uniqueProductIds = Array.from(new Set(items.map((i) => i.productId)));
            const missingProductIds = uniqueProductIds.filter((id) => !productsById.value[id]);

            if (missingProductIds.length > 0) {
                const products = await Promise.all(missingProductIds.map((id) => getProduct(id)));
                for (const product of products) {
                    productsById.value[product.id] = product;
                }
            }

            cartItems.value = items;
        } catch (e: any) {
            if (e?.response?.status === 401) {
                authStore.logout();
                unauthorized.value = true;
                cartItems.value = [];
                error.value = 'Session expirée, veuillez vous reconnecter.';
                return;
            }
            error.value = e?.message ?? 'Erreur lors du chargement du panier';
        } finally {
            loading.value = false;
        }
    }

    async function addItem(productId: number, quantity: number) {
        const q = Math.max(1, Number(quantity) || 1);
        await addToCartApi(productId, q);
        await loadCart();
    }

    async function removeItem(cartItemId: number) {
        await removeCartItemApi(cartItemId);
        await loadCart();
    }

    function clear() {
        cartItems.value = [];
        productsById.value = {};
        totalPages.value = 0;
        page.value = 0;
        size.value = 50;
        error.value = null;
        loading.value = false;
    }

    return {
        cartItems,
        productsById,
        loading,
        error,
        unauthorized,
        page,
        size,
        totalPages,
        lines,
        total,
        loadCart,
        addItem,
        removeItem,
        clear,
    };
});