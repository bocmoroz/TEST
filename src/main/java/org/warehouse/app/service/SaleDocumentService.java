package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.sale.SaleDocumentBuilderProductDto;
import org.warehouse.app.dto.sale.SaleDocumentDto;
import org.warehouse.app.exception.SaleDocumentValidationException;
import org.warehouse.app.model.*;
import org.warehouse.app.dao.ProductRepository;
import org.warehouse.app.dao.TransportationDocumentRepository;
import org.warehouse.app.dao.WarehouseRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.warehouse.app.enums.TransportationDocumentTypeEnum.SALE;

@Service
public class SaleDocumentService {

    private final TransportationDocumentRepository transportationDocumentRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public SaleDocumentService(TransportationDocumentRepository transportationDocumentRepository, WarehouseRepository warehouseRepository,
                               ProductRepository productRepository, MessageSource messageSource) {
        this.transportationDocumentRepository = transportationDocumentRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<SaleDocumentDto> getSaleDocuments() {
        return transportationDocumentRepository.findTransportationDocumentsByType(SALE).stream()
                .map(SaleDocumentDto::new)
                .collect(Collectors.toList());
    }

    public SaleDocumentDto getSaleDocument(Long id) {
        TransportationDocumentEntity saleDocument = transportationDocumentRepository.findTransportationDocumentById(id)
                .orElseThrow(() -> new SaleDocumentValidationException(messageSource.getMessage(
                        "warehouse.sale.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return new SaleDocumentDto(saleDocument);
    }

    @Transactional
    public SaleDocumentDto addSaleDocument(String documentName, String warehouseName,
                                           List<SaleDocumentBuilderProductDto> products) {

        Optional<TransportationDocumentEntity> existingTransportationDocument = transportationDocumentRepository
                .findTransportationDocumentByName(documentName);
        if (existingTransportationDocument.isPresent()) {
            throw new SaleDocumentValidationException(messageSource.getMessage(
                    "warehouse.sale.service.document.name.exists", new Object[]{documentName}, LocaleContextHolder.getLocale()));
        }

        WarehouseEntity warehouseEntity = warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new SaleDocumentValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseName}, LocaleContextHolder.getLocale())));

        List<ProductEntity> existingProducts = productRepository.findAllByDeleted(false);

        List<TransportationDocumentProduct> saleDocumentProducts = new ArrayList<>();

        products.forEach(saleProductDto -> {
            //check sale product
            ProductEntity productEntity = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(saleProductDto.getArticle()))
                    .findFirst().orElse(null);

            if (productEntity != null) {
                productEntity.setLastSalePrice(saleProductDto.getPrice());
            } else {
                throw new SaleDocumentValidationException(messageSource.getMessage(
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
                    throw new SaleDocumentValidationException(messageSource.getMessage(
                            "warehouse.sale.service.invalid.count.product",
                            new Object[]{saleProductDto.getArticle(), warehouseEntity.getName(), currentProductCount},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new SaleDocumentValidationException(messageSource.getMessage(
                        "warehouse.sale.service.invalid.product",
                        new Object[]{saleProductDto.getArticle(), warehouseEntity.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add sale of product
            TransportationDocumentProduct transportationDocumentProduct = new TransportationDocumentProduct();
            transportationDocumentProduct.setCount(saleProductDto.getCount());
            transportationDocumentProduct.setProduct(productEntity);
            transportationDocumentProduct.setPrice(saleProductDto.getPrice());
            saleDocumentProducts.add(transportationDocumentProduct);
        });

        //save sale
        TransportationDocumentEntity documentEntity = new TransportationDocumentEntity();
        documentEntity.setName(documentName);
        documentEntity.setWarehouseFrom(warehouseEntity);
        documentEntity.setProducts(saleDocumentProducts);
        transportationDocumentRepository.save(documentEntity);

        return new SaleDocumentDto(documentEntity);
    }
}
