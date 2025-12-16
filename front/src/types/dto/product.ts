export type ProductDTO = {
    id: number;
    code: string;
    name: string;
    description?: string | null;
    image?: string | null;
    category?: string | null;
    price: number;
    quantity?: number | null;
    internalReference?: string | null;
    shellId?: number | null;
    inventoryStatus?: string | null;
    rating?: number | null;
}