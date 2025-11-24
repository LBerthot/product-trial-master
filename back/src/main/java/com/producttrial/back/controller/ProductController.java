package com.producttrial.back.controller;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.mapper.ProductMapper;
import com.producttrial.back.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/products")
@Validated
@Slf4j
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> getAllProducts(@PageableDefault(size = 50, page = 0) Pageable pageable) {
        log.info("GET /products page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        if (pageable.getPageSize() < 200) {
            return productService.getAllProducts(pageable);
        }
        log.warn("Bad request: page size must be less than 200 (requested={})", pageable.getPageSize());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be less than 200");
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable @Positive Long id) {
        log.info("GET /products/{}", id);
        return productService.getProductById(id).orElseThrow(() -> {
            log.warn("Product not found id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id);
        });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid ProductDTO productDTO) {
        log.info("POST /products - payload name={}, code={}", productDTO.getName(), productDTO.getCode());
        Product saved = productService.save(ProductMapper.toEntity(productDTO));
        return ProductMapper.toDto(saved);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable @Positive Long id, @RequestBody @Valid ProductDTO productDTO) {
        log.info("PUT /products/{} - payload name={}, code={}", id, productDTO.getName(), productDTO.getCode());
        Product updated = productService.update(id, ProductMapper.toEntity(productDTO));
        return ProductMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable @Positive Long id) {
        log.info("DELETE /products/{}", id);
        productService.delete(id);
    }
}
