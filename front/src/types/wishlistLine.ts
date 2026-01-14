import type { WishlistItemDTO } from './dto/wishlist';
import type { ProductDTO } from './dto/product';

export type WishlistLine = {
    item: WishlistItemDTO;
    product: ProductDTO;
}