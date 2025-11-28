package com.producttrial.back.service;

import com.producttrial.back.dto.ProductDTO;
import com.producttrial.back.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    // Méthodes de Product
    /**
     * Retrieves a list of all available products.
     *
     * @return a list containing all products. If no products are found, an empty list will be returned.
     */
    List<Product> findAll();

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique identifier of the product to retrieve
     * @return an Optional containing the product if found, or an empty Optional if no product exists with the given id
     */
    Optional<Product> findById(Long id);

    /**
     * Persists the given product in the database.
     *
     * @param product the product to be saved
     * @return the saved product
     */
    Product save(Product product);

    /**
     * Updates an existing product identified by its unique ID with the details provided.
     * If the product with the specified ID is not found, an exception is thrown.
     *
     * @param id the unique identifier of the product to update
     * @param product the product object containing updated details to be applied to the existing product
     * @return the updated product
     */
    Product update(Long id, Product product);

    /**
     * Deletes the product identified by the specified ID.
     *
     * @param id the unique identifier of the product to be deleted
     */
    void delete(Long id);

    /**
     * Deletes all products from the database.
     * This operation removes all records associated with products, leaving the database empty.
     * Typically used for cleanup or reset purposes.
     */
    void deleteAll();

    // Méthode de ProductDTO

    /**
     * Retrieves a paginated list of all available products and maps them to ProductDTO objects.
     *
     * @param pageable the pagination and sorting information
     * @return a list of ProductDTO objects representing the products. If no products are found, an empty list will be returned.
     */
    Page<ProductDTO> getAllProducts(Pageable pageable);

    /**
     * Retrieves a product by its unique identifier and maps it to a ProductDTO.
     *
     * @param id the unique identifier of the product to retrieve
     * @return a ProductDTO object representing the product details, or null if no product exists with the given id
     */
    Optional<ProductDTO> getProductById(Long id);

}
