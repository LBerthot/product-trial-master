import { http } from './http';
import type { ProductDTO } from '../types/dto/product';

export type CreateProductPayload = {
    code: string;
    name: string;
    price: number;
    description?: string | null;
    image?: string | null;
    category?: string | null;
    quantity?: number | null;
    internalReference?: string | null;
    shellId?: number | null;
    inventoryStatus?: string | null;
    rating?: number | null;
};

export type UpdateProductPayload = CreateProductPayload;

export async function createProduct(payload: CreateProductPayload): Promise<ProductDTO> {
    const response = await http.post<ProductDTO>('/products', payload);
    return response.data;
}

export async function updateProduct(id: number, payload: UpdateProductPayload): Promise<ProductDTO> {
    const response = await http.put<ProductDTO>(`/products/${id}`, payload);
    return response.data;
}

export async function deleteProduct(id: number): Promise<void> {
    await http.delete(`/products/${id}`);
}