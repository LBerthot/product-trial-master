package com.producttrial.back.service.iservice;

import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.WishlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IWishlistItemService {
    // Méthodes de whishlistItem

    /**
     * Retrieves a list of wishlist items associated with the specified user.
     *
     * @param userId the unique identifier of the user whose wishlist items are to be retrieved
     * @return a list of WishlistItem objects associated with the given user ID. If no wishlist items are found, an empty list will be returned.
     */
    List<WishlistItem> findByUserId(Long userId);

    /**
     * Saves a new wishlist item or updates an existing one for the specified user.
     *
     * @param dto the data transfer object containing the details of the wishlist item to be saved
     * @param userId the unique identifier of the user to whom the wishlist item belongs
     * @return the saved WishlistItem entity
     */
    WishlistItem save(WishlistItemDTO dto, Long userId);

    /**
     * Deletes a wishlist item based on its unique identifier and the user's identifier.
     *
     * @param id the unique identifier of the wishlist item to be deleted
     * @param userId the unique identifier of the user associated with the wishlist item
     */
    void delete(Long id, Long userId);

    /**
     * Deletes all wishlist items associated with all users.
     * This operation removes all records of wishlist items, leaving the related table empty.
     * Typically used for cleanup or resetting data purposes.
     */
    void deleteAll();


    // Méthode de wishlistItemDTO

    /**
     * Retrieves a paginated list of wishlist items for the specified user.
     *
     * @param pageable the pagination and sorting information
     * @param userId the unique identifier of the user whose wishlist is to be retrieved
     * @return a paginated list of WishlistItemDTO objects representing the user's wishlist items
     */
    Page<WishlistItemDTO> getWishlist (Pageable pageable, Long userId);

    /**
     * Retrieves a wishlist item by its unique identifier for a specific user.
     *
     * @param id the unique identifier of the wishlist item to retrieve
     * @param userId the unique identifier of the user owning the wishlist item
     * @return an Optional containing the WishlistItemDTO if found, or an empty Optional if no item exists with the given id for the specified user
     */
    Optional<WishlistItemDTO> getWishlistItemById(Long id, Long userId);
}
