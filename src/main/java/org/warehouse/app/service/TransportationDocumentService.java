package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dao.ProductRepository;
import org.warehouse.app.dao.TransportationDocumentRepository;
import org.warehouse.app.dao.WarehouseRepository;
import org.warehouse.app.dto.DocumentDto;
import org.warehouse.app.dto.transportation.*;
import org.warehouse.app.enums.TransportationDocumentTypeEnum;
import org.warehouse.app.exception.TransportationDocumentValidationException;
import org.warehouse.app.model.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.warehouse.app.enums.TransportationDocumentTypeEnum.*;

@Service
public class TransportationDocumentService {

    private final TransportationDocumentRepository transportationDocumentRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public TransportationDocumentService(
            TransportationDocumentRepository transportationDocumentRepository, WarehouseRepository warehouseRepository,
            ProductRepository productRepository, MessageSource messageSource) {
        this.transportationDocumentRepository = transportationDocumentRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<DocumentDto> getTransportationDocuments() {
        return transportationDocumentRepository.findAll().stream()
                .map(TransportationDocumentDto::new)
                .collect(Collectors.toList());
    }

    public List<DocumentDto> getTransportationDocumentsByType(TransportationDocumentTypeEnum type) {
        return transportationDocumentRepository.findTransportationDocumentsByType(type).stream()
                .map(TransportationDocumentDto::new)
                .collect(Collectors.toList());
    }

    public DocumentDto getTransportationDocument(Long id) {
        TransportationDocumentEntity transportationDocument = transportationDocumentRepository.findTransportationDocumentById(id)
                .orElseThrow(() -> new TransportationDocumentValidationException(messageSource.getMessage(
                        "transportation.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return new TransportationDocumentDto(transportationDocument);
    }

    @Transactional
    public DocumentDto addTransportationDocument(TransportationDocumentBuilderDto dto) {

        Optional<TransportationDocumentEntity> existingTransportationDocument = transportationDocumentRepository
                .findTransportationDocumentByName(dto.getDocumentName());
        if (existingTransportationDocument.isPresent()) {
            throw new TransportationDocumentValidationException(messageSource.getMessage(
                    "transportation.service.document.name.exists", new Object[]{dto.getDocumentName()}, LocaleContextHolder.getLocale()));
        }

        TransportationDocumentTypeEnum type = dto.getType();
        if (type == INCOME) {
            return addIncomeDocument(dto.getDocumentName(), dto.getWarehouseNameTo(), dto.getProducts());
        } else if (type == SALE) {
            return addSaleDocument(dto.getDocumentName(), dto.getWarehouseNameFrom(), dto.getProducts());
        } else {
            return addMovingDocument(dto.getDocumentName(), dto.getWarehouseNameFrom(), dto.getWarehouseNameTo(), dto.getProducts());
        }
    }

    private TransportationDocumentDto addIncomeDocument(String documentName, String warehouseName,
                                                                List<TransportationDocumentBuilderProductDto> products) {
        WarehouseEntity warehouseEntity = getWarehouseByName(warehouseName);
        List<ProductEntity> existingProducts = getExistingProducts();

        List<TransportationDocumentProduct> incomeDocumentProducts = new ArrayList<>();
        products.forEach(incomeProductDto -> {
            //check income product and add if needed
            ProductEntity productEntity = findProduct(existingProducts, incomeProductDto.getArticle());

            if (productEntity != null) {
                productEntity.setLastIncomePrice(incomeProductDto.getPrice());
            } else {
                productEntity = new ProductEntity(incomeProductDto.getArticle(), incomeProductDto.getName());
                productEntity.setLastIncomePrice(incomeProductDto.getPrice());
                productRepository.save(productEntity);
            }

            //add or update count of product for warehouse
            ProductEntity finalProduct = productEntity;
            List<WarehouseProduct> warehouseProducts = warehouseEntity.getProducts();

            Optional<WarehouseProduct> optionalWarehouseProduct = warehouseProducts.stream()
                    .filter(p -> p.getProduct().getArticle().equals(finalProduct.getArticle()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                WarehouseProduct warehouseProduct = optionalWarehouseProduct.get();
                warehouseProduct.setCount(warehouseProduct.getCount() + incomeProductDto.getCount());
            } else {
                WarehouseProduct newWarehouseProduct = new WarehouseProduct();
                newWarehouseProduct.setProduct(productEntity);
                newWarehouseProduct.setCount(incomeProductDto.getCount());
                warehouseProducts.add(newWarehouseProduct);
            }

            //add income products
            incomeDocumentProducts.add(createTransportationDocumentProduct(productEntity, incomeProductDto.getCount(),
                    incomeProductDto.getPrice()));
        });

        //save income
        TransportationDocumentEntity documentEntity = createTransportationDocument(documentName, null, warehouseEntity,
                incomeDocumentProducts, INCOME);
        transportationDocumentRepository.save(documentEntity);

        return new TransportationDocumentDto(documentEntity);
    }

    public TransportationDocumentDto addSaleDocument(String documentName, String warehouseName,
                                                             List<TransportationDocumentBuilderProductDto> products) {

        WarehouseEntity warehouseEntity = getWarehouseByName(warehouseName);
        List<ProductEntity> existingProducts = getExistingProducts();

        List<TransportationDocumentProduct> saleDocumentProducts = new ArrayList<>();

        products.forEach(saleProductDto -> {
            //check sale product
            ProductEntity productEntity = findProduct(existingProducts, saleProductDto.getArticle());

            if (productEntity != null) {
                productEntity.setLastSalePrice(saleProductDto.getPrice());
            } else {
                throw new TransportationDocumentValidationException(messageSource.getMessage(
                        "product.service.not.exists", new Object[]{saleProductDto.getArticle()},
                        LocaleContextHolder.getLocale()));
            }

            //update count of product for warehouse
            Optional<WarehouseProduct> optionalWarehouseProduct = warehouseEntity.getProducts().stream()
                    .filter(p -> p.getProduct().getArticle().equals(productEntity.getArticle()))
                    .findAny();

            if (optionalWarehouseProduct.isPresent()) {
                WarehouseProduct warehouseProduct = optionalWarehouseProduct.get();
                Integer currentProductCount = warehouseProduct.getCount();
                if (currentProductCount >= saleProductDto.getCount()) {
                    warehouseProduct.setCount(currentProductCount - saleProductDto.getCount());
                } else {
                    throw new TransportationDocumentValidationException(messageSource.getMessage(
                            "transportation.service.invalid.count.product",
                            new Object[]{saleProductDto.getArticle(), warehouseEntity.getName(), currentProductCount},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new TransportationDocumentValidationException(messageSource.getMessage(
                        "transportation.service.invalid.product",
                        new Object[]{saleProductDto.getArticle(), warehouseEntity.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add sale products
            saleDocumentProducts.add(createTransportationDocumentProduct(productEntity, saleProductDto.getCount(),
                    saleProductDto.getPrice()));
        });

        //save sale
        TransportationDocumentEntity documentEntity = createTransportationDocument(documentName, warehouseEntity, null,
                saleDocumentProducts, SALE);
        transportationDocumentRepository.save(documentEntity);

        return new TransportationDocumentDto(documentEntity);
    }

    public TransportationDocumentDto addMovingDocument(String documentName, String warehouseNameFrom, String warehouseNameTo,
                                                               List<TransportationDocumentBuilderProductDto> products) {

        WarehouseEntity warehouseEntityFrom = getWarehouseByName(warehouseNameFrom);
        WarehouseEntity warehouseEntityTo = getWarehouseByName(warehouseNameTo);

        List<ProductEntity> existingProducts = getExistingProducts();

        List<TransportationDocumentProduct> movingDocumentProducts = new ArrayList<>();

        products.forEach(movingProductDto -> {
            //check transportation product
            ProductEntity productEntity = findProduct(existingProducts, movingProductDto.getArticle());

            if (productEntity == null) {
                throw new TransportationDocumentValidationException(messageSource.getMessage(
                        "product.service.not.exists", new Object[]{movingProductDto.getArticle()},
                        LocaleContextHolder.getLocale()));
            }

            //update count of product for warehouses
            Optional<WarehouseProduct> optionalWarehouseFromProduct = warehouseEntityFrom.getProducts().stream()
                    .filter(p -> p.getProduct().getArticle().equals(productEntity.getArticle()))
                    .findAny();

            List<WarehouseProduct> warehouseProductsTo = warehouseEntityTo.getProducts();

            Optional<WarehouseProduct> optionalWarehouseToProduct = warehouseProductsTo.stream()
                    .filter(p -> p.getProduct().getArticle().equals(productEntity.getArticle()))
                    .findAny();

            if (optionalWarehouseFromProduct.isPresent()) {
                WarehouseProduct warehouseFromProduct = optionalWarehouseFromProduct.get();
                Integer currentProductCountInWarehouseFrom = warehouseFromProduct.getCount();
                if (currentProductCountInWarehouseFrom >= movingProductDto.getCount()) {
                    warehouseFromProduct.setCount(currentProductCountInWarehouseFrom - movingProductDto.getCount());

                    if (optionalWarehouseToProduct.isPresent()) {
                        WarehouseProduct warehouseToProduct = optionalWarehouseToProduct.get();
                        warehouseToProduct.setCount(warehouseToProduct.getCount() + movingProductDto.getCount());
                    } else {
                        WarehouseProduct newWarehouseProduct = new WarehouseProduct();
                        newWarehouseProduct.setProduct(productEntity);
                        newWarehouseProduct.setCount(movingProductDto.getCount());
                        warehouseProductsTo.add(newWarehouseProduct);
                    }
                } else {
                    throw new TransportationDocumentValidationException(messageSource.getMessage(
                            "transportation.service.invalid.count.product",
                            new Object[]{movingProductDto.getArticle(), warehouseEntityFrom.getName(), currentProductCountInWarehouseFrom},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new TransportationDocumentValidationException(messageSource.getMessage(
                        "transportation.service.invalid.product",
                        new Object[]{movingProductDto.getArticle(), warehouseEntityFrom.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add moving of product
            movingDocumentProducts.add(createTransportationDocumentProduct(productEntity, movingProductDto.getCount(),
                    null));
        });

        //save moving
        TransportationDocumentEntity documentEntity = createTransportationDocument(documentName, warehouseEntityFrom,
                warehouseEntityTo, movingDocumentProducts, MOVING);
        transportationDocumentRepository.save(documentEntity);

        return new TransportationDocumentDto(documentEntity);
    }

    private WarehouseEntity getWarehouseByName(String warehouseName) {
        return warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new TransportationDocumentValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseName}, LocaleContextHolder.getLocale())));
    }

    private List<ProductEntity> getExistingProducts() {
        return productRepository.findAllByDeleted(false);
    }

    private ProductEntity findProduct(List<ProductEntity> products, String article) {
        return products.stream()
                .filter(product -> product.getArticle().equals(article))
                .findFirst().orElse(null);
    }

    private TransportationDocumentProduct createTransportationDocumentProduct(ProductEntity product, Integer count, BigDecimal price) {
        TransportationDocumentProduct transportationDocumentProduct = new TransportationDocumentProduct();
        transportationDocumentProduct.setProduct(product);
        transportationDocumentProduct.setCount(count);
        transportationDocumentProduct.setPrice(price);
        return transportationDocumentProduct;
    }

    private TransportationDocumentEntity createTransportationDocument(String documentName, WarehouseEntity warehouseFrom,
                                                                      WarehouseEntity warehouseTo, List<TransportationDocumentProduct> products,
                                                                      TransportationDocumentTypeEnum type) {
        TransportationDocumentEntity documentEntity = new TransportationDocumentEntity();
        documentEntity.setName(documentName);
        documentEntity.setWarehouseFrom(warehouseFrom);
        documentEntity.setWarehouseTo(warehouseTo);
        documentEntity.setProducts(products);
        documentEntity.setType(type);
        return documentEntity;
    }

}
