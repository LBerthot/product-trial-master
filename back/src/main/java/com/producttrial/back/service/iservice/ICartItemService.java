package com.producttrial.back.service.iservice;

import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICartItemService {
    // Méthodes de cartItem

    /**
     * Retrieves a list of cart items associated with a specific user ID.
     *
     * @param userId the unique identifier of the user whose cart items are to be retrieved
     * @return a list of CartItem objects associated with the given user ID, or an empty list if no cart items are found
     */
    List<CartItem> findByUserId(Long userId);
    /**
     * Persists the provided cart item details for a specific user.
     * If a cart item with the same product already exists for the user, it will be updated with the new quantity.
     *
     * @param dto the data transfer object containing the cart item details, including product ID and quantity
     * @param userId the unique identifier of the user for whom the cart item is to be saved
     * @return the saved cart item as a persisted entity
     */
    CartItem save(CartItemDTO dto, Long userId);

    /**
     * Deletes a cart item associated with the specified ID and user ID.
     *
     * @param id the unique identifier of the cart item to delete
     * @param userId the unique identifier of the user who owns the cart item
     */
    void delete(Long id, Long userId);
    /**
     * Deletes all cart items associated with the user or application.
     * This operation removes all records in the cart, leaving it empty.
     * Typically used for cleanup or resetting the cart's state.
     */
    void deleteAll();

    // Méthode de cartItemDTO

    /**
     * Retrieves a paginated list of cart items associated with a specific user ID.
     *
     * @param pageable the pagination information, including page number and size
     * @param userId the unique identifier of the user whose cart items are to be retrieved
     * @return a paginated list of CartItemDTOs associated with the specified user ID
     */
    Page<CartItemDTO> getCart (Pageable pageable, Long userId);
    /**
     * Retrieves the cart item associated with the specified ID and user ID.
     *
     * @param id the unique identifier of the cart item to retrieve
     * @param userId the unique identifier of the user owning the cart item
     * @return an Optional containing the CartItemDTO if found, or an empty Optional if no cart item exists with the given ID and user ID
     */
    Optional<CartItemDTO> getCartItemById(Long id, Long userId);
}
