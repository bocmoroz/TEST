package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dto.moving.MovingDocumentBuilderProductDto;
import org.warehouse.app.dto.moving.MovingDocumentDto;
import org.warehouse.app.exception.MovingDocumentValidationException;
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

import static org.warehouse.app.enums.TransportationDocumentTypeEnum.MOVING;

@Service
public class MovingDocumentService {

    private final TransportationDocumentRepository transportationDocumentRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public MovingDocumentService(TransportationDocumentRepository transportationDocumentRepository,
                                 WarehouseRepository warehouseRepository, ProductRepository productRepository,
                                 MessageSource messageSource) {
        this.transportationDocumentRepository = transportationDocumentRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<MovingDocumentDto> getMovingDocuments() {
        return transportationDocumentRepository.findTransportationDocumentsByType(MOVING).stream()
                .map(MovingDocumentDto::new)
                .collect(Collectors.toList());
    }

    public MovingDocumentDto getMovingDocument(Long id) {
        TransportationDocumentEntity movingDocument = transportationDocumentRepository.findTransportationDocumentById(id)
                .orElseThrow(() -> new MovingDocumentValidationException(messageSource.getMessage(
                        "warehouse.moving.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return new MovingDocumentDto(movingDocument);
    }

    @Transactional
    public MovingDocumentDto addMovingDocument(String documentName, String warehouseNameFrom, String warehouseNameTo,
                                               List<MovingDocumentBuilderProductDto> products) {

        Optional<TransportationDocumentEntity> existingTransportationDocument = transportationDocumentRepository
                .findTransportationDocumentByName(documentName);
        if (existingTransportationDocument.isPresent()) {
            throw new MovingDocumentValidationException(messageSource.getMessage(
                    "warehouse.moving.service.document.name.exists", new Object[]{documentName}, LocaleContextHolder.getLocale()));
        }

        WarehouseEntity warehouseEntityFrom = warehouseRepository.findWarehouseByName(warehouseNameFrom)
                .orElseThrow(() -> new MovingDocumentValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseNameFrom}, LocaleContextHolder.getLocale())));

        WarehouseEntity warehouseEntityTo = warehouseRepository.findWarehouseByName(warehouseNameTo)
                .orElseThrow(() -> new MovingDocumentValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseNameTo}, LocaleContextHolder.getLocale())));

        List<ProductEntity> existingProducts = productRepository.findAllByDeleted(false);

        List<TransportationDocumentProduct> movingDocumentProducts = new ArrayList<>();

        products.forEach(movingProductDto -> {
            //check transportation product
            ProductEntity productEntity = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(movingProductDto.getArticle()))
                    .findFirst().orElse(null);

            if (productEntity == null) {
                throw new MovingDocumentValidationException(messageSource.getMessage(
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
                    throw new MovingDocumentValidationException(messageSource.getMessage(
                            "warehouse.moving.service.invalid.count.product",
                            new Object[]{movingProductDto.getArticle(), warehouseEntityFrom.getName(), currentProductCountInWarehouseFrom},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                throw new MovingDocumentValidationException(messageSource.getMessage(
                        "warehouse.moving.service.invalid.product",
                        new Object[]{movingProductDto.getArticle(), warehouseEntityFrom.getName()},
                        LocaleContextHolder.getLocale()));
            }

            //add moving of product
            TransportationDocumentProduct transportationDocumentProduct = new TransportationDocumentProduct();
            transportationDocumentProduct.setCount(movingProductDto.getCount());
            transportationDocumentProduct.setProduct(productEntity);
            movingDocumentProducts.add(transportationDocumentProduct);
        });

        //save moving
        TransportationDocumentEntity documentEntity = new TransportationDocumentEntity();
        documentEntity.setName(documentName);
        documentEntity.setWarehouseFrom(warehouseEntityFrom);
        documentEntity.setWarehouseTo(warehouseEntityTo);
        documentEntity.setProducts(movingDocumentProducts);
        documentEntity.setType(MOVING);
        transportationDocumentRepository.save(documentEntity);

        return new MovingDocumentDto(documentEntity);
    }

}
