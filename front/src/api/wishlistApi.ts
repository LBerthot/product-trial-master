import type { Page } from '../types/pagination';
import type { WishlistItemDTO } from '../types/dto/wishlist';
import { http } from './http';

export type GetWishlistParams = {
    page?: number;
    size?: number;
}

export async function getWishlist(params: GetWishlistParams = {}): Promise<Page<WishlistItemDTO>> {
    const response = await http.get<Page<WishlistItemDTO>>('/wishlist', { params });
    return response.data;
}

export async function addToWishlist(productId: number): Promise<WishlistItemDTO> {
    const response = await http.post<WishlistItemDTO>('/wishlist', { productId });
    return response.data;
}

export async function removeFromWishlist(id: number): Promise<void> {
    await http.delete<void>(`/wishlist/${id}`);
}