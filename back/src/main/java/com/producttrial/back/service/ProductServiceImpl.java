package com.producttrial.back.service;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import com.producttrial.back.mapper.ProductMapper;
import com.producttrial.back.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
    private final ProductRepository productRepository;

    // Méthodes de Product
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        product.setCreatedAt(System.currentTimeMillis());
        product.setUpdatedAt(System.currentTimeMillis());
        return productRepository.save(product);
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
                    return productRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
    }

    // Méthodes de ProductDTO

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository
                .findAll(pageable)
                .map(ProductMapper::toDto);
    }

    @Override
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDto);
    }
}
