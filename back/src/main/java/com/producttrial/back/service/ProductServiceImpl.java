package com.producttrial.back.service;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.exception.ProductNotFoundException;
import com.producttrial.back.mapper.ProductMapper;
import com.producttrial.back.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService{
    private final ProductRepository productRepository;

    // Méthodes de Product
    @Override
    public List<Product> findAll() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        log.debug("Fetching product with id {}", id);
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        try {
            long now = System.currentTimeMillis();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            Product saved = productRepository.save(product);
            log.info("Saved product with id {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error saving product", e);
            throw e;
        }
    }

    @Override
    public Product update(Long id, Product product) {
        return productRepository.findById(id)
                .map(p -> {
                    p.setName(product.getName());
                    p.setCode(product.getCode());
                    p.setDescription(product.getDescription());
                    p.setPrice(product.getPrice());
                    p.setQuantity(product.getQuantity());
                    p.setCategory(product.getCategory());
                    p.setImage(product.getImage());
                    p.setInternalReference(product.getInternalReference());
                    p.setShellId(product.getShellId());
                    p.setInventoryStatus(product.getInventoryStatus());
                    p.setRating(product.getRating());
                    p.setUpdatedAt(System.currentTimeMillis());
                    Product saved = productRepository.save(p);
                    log.info("Updated product with id {}", p.getId());
                    return saved;
                })
                .orElseThrow(() -> {
                    log.warn("Product not found for update id={}", id);
                    return new ProductNotFoundException(id);
                });
    }

    @Override
    public void delete(Long id) {
        try {
            if (!productRepository.existsById(id)) {
                log.warn("Attempted to delete non-existing product id={}", id);
                throw new ProductNotFoundException(id);
            }
            productRepository.deleteById(id);
            log.info("Deleted product id={}", id);
        } catch (Exception ex) {
            log.error("Error deleting product id={}", id, ex);
            throw ex;
        }
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
        log.info("Deleted all products");
    }

    // Méthodes de ProductDTO

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductMapper::toDto);
    }

    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(ProductMapper::toDto);
    }
}
