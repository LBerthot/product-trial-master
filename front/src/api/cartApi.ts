import { http } from './http';
import type { Page } from '../types/pagination';
import type { CartItemDTO } from '../types/dto/cart';

export type GetCartParams = {
    page?: number;
    size?: number;
}

export async function getCart(params: GetCartParams = {}): Promise<Page<CartItemDTO>> {
    const response = await http.get<Page<CartItemDTO>>('/cart', { params });
    return response.data;
}

export async function addToCart(productId: number, quantity: number): Promise<CartItemDTO> {
    const response = await http.post<CartItemDTO>('/cart', { productId, quantity });
    return response.data;
}

export async function removeCartItem(id: number): Promise<void> {
    await http.delete<void>(`/cart/${id}`);
}