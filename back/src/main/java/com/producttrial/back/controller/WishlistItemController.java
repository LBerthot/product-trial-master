package com.producttrial.back.controller;


import com.producttrial.back.dto.WishlistItemDTO;
import com.producttrial.back.entity.WishlistItem;
import com.producttrial.back.mapper.WishlistItemMapper;
import com.producttrial.back.service.IAuthorizationService;
import com.producttrial.back.service.IWishlistItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/wishlist")
@Validated
@Slf4j
@AllArgsConstructor
public class WishlistItemController {
    private final IWishlistItemService wishlistItemService;
    private final IAuthorizationService authzService;

    @GetMapping
    public Page<WishlistItemDTO> getWhishlist(@PageableDefault(size = 50, page = 0) Pageable pageable) {
        log.info("GET /wishlist page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        if (pageable.getPageSize() < 200) {
            return wishlistItemService.getWishlist(pageable, authzService.getCurrentUserId());
        }
        log.warn("Bad request: page size must be less than 200 (requested={})", pageable.getPageSize());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be less than 200");
    }

    @GetMapping("/{id}")
    public WishlistItemDTO getWishlistItem(@PathVariable @Positive Long id) {
        log.info("GET /wishlist/{}", id);
        return wishlistItemService.getWishlistItemById(id, authzService.getCurrentUserId()).orElseThrow(() -> {
            log.warn("Wishlist item not found id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist item not found with id " + id);
        });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemDTO createWishlistItem(@RequestBody @Valid WishlistItemDTO wishlistItemDTO) {
        log.info("POST /wishlist - payload productId={}", wishlistItemDTO.getProductId());
        WishlistItem saved = wishlistItemService.save(wishlistItemDTO, authzService.getCurrentUserId());
        return WishlistItemMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishlistItem(@PathVariable @Positive Long id) {

        log.info("DELETE /wishlist/{}", id);
        wishlistItemService.delete(id, authzService.getCurrentUserId());
    }
}
