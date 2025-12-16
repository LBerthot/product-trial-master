import { http } from './http';
import type { Page } from '../types/pagination';
import type { ProductDTO } from '../types/dto/product';

export type GetProductsParams = {
    page?: number;
    size?: number;
}

export async function getProducts(params: GetProductsParams = {}): Promise<Page<ProductDTO>> {
    const response = await http.get<Page<ProductDTO>>('/products', { params });
    return response.data;
}

export async function getProduct(id: number): Promise<ProductDTO> {
    const response = await http.get<ProductDTO>(`/products/${id}`);
    return response.data;
}
