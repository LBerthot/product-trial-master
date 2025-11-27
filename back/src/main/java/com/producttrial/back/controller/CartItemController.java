package com.producttrial.back.controller;

import com.producttrial.back.dto.CartItemDTO;
import com.producttrial.back.entity.CartItem;
import com.producttrial.back.mapper.CartItemMapper;
import com.producttrial.back.service.iservice.IAuthorizationService;
import com.producttrial.back.service.iservice.ICartItemService;
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
@RequestMapping("/cart")
@Validated
@Slf4j
@AllArgsConstructor
public class CartItemController {
    private final ICartItemService cartItemService;
    private final IAuthorizationService authzService;

    @GetMapping
    public Page<CartItemDTO> getCart(@PageableDefault(size = 50, page = 0) Pageable pageable) {
        log.info("GET /cart page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        if (pageable.getPageSize() < 200) {
            return cartItemService.getCart(pageable, authzService.getCurrentUserId());
        }
        log.warn("Bad request: page size must be less than 200 (requested={})", pageable.getPageSize());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be less than 200");
    }

    @GetMapping("/{id}")
    public CartItemDTO getCartItem(@PathVariable @Positive Long id) {
        log.info("GET /cart/{}", id);
        return cartItemService.getCartItemById(id, authzService.getCurrentUserId()).orElseThrow(() -> {
            log.warn("Cart item not found id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found with id " + id);
        });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDTO createCartItem(@RequestBody @Valid CartItemDTO cartItemDTO) {
        log.info("POST /cart - payload quantity={}, productId={}", cartItemDTO.getQuantity(), cartItemDTO.getProductId());
        CartItem saved = cartItemService.save(cartItemDTO, authzService.getCurrentUserId());
        return CartItemMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCartItem(@PathVariable @Positive Long id) {

        log.info("DELETE /cart/{}", id);
        cartItemService.delete(id, authzService.getCurrentUserId());
    }
}
