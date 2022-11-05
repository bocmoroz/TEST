package org.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.entity.Product;
import org.test.exception.ProductValidationException;
import org.test.helpers.DeleteCountService;
import org.test.repository.ProductRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final DeleteCountService deleteCountService;

    @Autowired
    public ProductService(ProductRepository productRepository, DeleteCountService deleteCountService) {
        this.productRepository = productRepository;
        this.deleteCountService = deleteCountService;
    }

    public List<Product> getProducts() {
        List<Product> list = productRepository.findAllByDeleted(false);
        if (list.isEmpty()) {
            throw new ProductValidationException("В БД нет продуктов!");
        }
        return list;
    }

    public Product getProductByArticle(String articul) {
        List<Product> products = productRepository.findProductByArticul(articul);
        if (products.isEmpty()) {
            throw new ProductValidationException("Продукт с артикулом  " + articul + " не существует!");
        }

        if (products.size() > 1) {
            throw new ProductValidationException("БД содержит более одного продукта с артикулом  " + articul + "!");
        }

        return products.get(0);
    }

    public Product addNewProduct(Product product) {
        List<Product> products = productRepository.findProductByArticul(product.getArticul());
        if (!products.isEmpty()) {
            throw new ProductValidationException("Такой артикул уже существует!");
        }

        product.setDeleted(false);

        productRepository.save(product);

        return product;
    }

    @Transactional
    public Product updateProduct(String articul, String name) {
        List<Product> products = productRepository.findProductByArticul(articul);
        if (products.isEmpty()) {
            throw new ProductValidationException("Продукт с артикулом  " + articul + " не существует!");
        }

        if (products.size() > 1) {
            throw new ProductValidationException("БД содержит более одного продукта с артикулом  " + articul + "!");
        }

        Product product = products.get(0);

        if (product.getName().equals(name)) {
            throw new ProductValidationException("Необходимо ввести новое имя продукта!");
        }

        product.setName(name);

        productRepository.save(product);

        return product;

    }

    public Product deleteProduct(String articul) {
        List<Product> products = productRepository.findProductByArticul(articul);
        if (products.isEmpty()) {
            throw new ProductValidationException("Продукт с артикулом  " + articul + " не существует!");
        }

        if (products.size() > 1) {
            throw new ProductValidationException("БД содержит более одного продукта с артикулом  " + articul + "!");
        }

        Product product = products.get(0);

        String deletedArticul = deleteCountService.defineDeletedProductArticul(product);

        product.setArticul(deletedArticul);

        product.setDeleted(true);

        productRepository.save(product);

        return product;
    }
}
