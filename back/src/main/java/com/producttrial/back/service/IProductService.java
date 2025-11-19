package com.producttrial.back.service;

import com.producttrial.back.entity.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
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
     *
     */
    Product update(Long id, Product product);

    /**
     * Deletes the product identified by the specified ID.
     *
     * @param id the unique identifier of the product to be deleted
     */
    void delete(Long id);
}
