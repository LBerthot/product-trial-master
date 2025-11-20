package com.producttrial.back.controller;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.service.IProductService;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/products")
@Validated
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> getAllProducts(@PageableDefault(size = 50, page = 0) Pageable pageable) {
        if (pageable.getPageSize() < 200) {
            return productService.getAllProducts(pageable);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be less than 200");
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable @Positive Long id) {
        return productService.getProductById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Product not found with id ".concat(id.toString())));
    }
}
