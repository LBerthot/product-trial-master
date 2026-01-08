import type { ProductDTO } from './dto/product';
import type { CartItemDTO } from './dto/cart';

export type CartLine = {
    item: CartItemDTO;
    product: ProductDTO;
    lineTotal: number;
}