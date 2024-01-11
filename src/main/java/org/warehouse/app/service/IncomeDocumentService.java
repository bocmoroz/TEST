package org.warehouse.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.warehouse.app.dao.ProductRepository;
import org.warehouse.app.dao.TransportationDocumentRepository;
import org.warehouse.app.dao.WarehouseRepository;
import org.warehouse.app.dto.income.IncomeDocumentBuilderProductDto;
import org.warehouse.app.dto.income.IncomeDocumentDto;
import org.warehouse.app.exception.IncomeDocumentValidationException;
import org.warehouse.app.model.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.warehouse.app.enums.TransportationDocumentTypeEnum.INCOME;

@Service
public class IncomeDocumentService {

    private final TransportationDocumentRepository transportationDocumentRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Autowired
    public IncomeDocumentService(
            TransportationDocumentRepository transportationDocumentRepository, WarehouseRepository warehouseRepository,
            ProductRepository productRepository, MessageSource messageSource) {
        this.transportationDocumentRepository = transportationDocumentRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.messageSource = messageSource;
    }

    public List<IncomeDocumentDto> getIncomeDocuments() {
        return transportationDocumentRepository.findTransportationDocumentsByType(INCOME).stream()
                .map(IncomeDocumentDto::new)
                .collect(Collectors.toList());
    }

    public IncomeDocumentDto getIncomeDocument(Long id) {
        TransportationDocumentEntity incomeDocument = transportationDocumentRepository.findTransportationDocumentById(id)
                .orElseThrow(() -> new IncomeDocumentValidationException(messageSource.getMessage(
                        "warehouse.income.service.not.exists", new Object[]{id}, LocaleContextHolder.getLocale())));
        return new IncomeDocumentDto(incomeDocument);
    }

    @Transactional
    public IncomeDocumentDto addIncomeDocument(String documentName, String warehouseName,
                                               List<IncomeDocumentBuilderProductDto> products) {

        Optional<TransportationDocumentEntity> existingTransportationDocument = transportationDocumentRepository
                .findTransportationDocumentByName(documentName);
        if (existingTransportationDocument.isPresent()) {
            throw new IncomeDocumentValidationException(messageSource.getMessage(
                    "warehouse.income.service.document.name.exists", new Object[]{documentName}, LocaleContextHolder.getLocale()));
        }

        WarehouseEntity warehouseEntity = warehouseRepository.findWarehouseByName(warehouseName)
                .orElseThrow(() -> new IncomeDocumentValidationException(messageSource.getMessage(
                        "warehouse.service.not.exists", new Object[]{warehouseName}, LocaleContextHolder.getLocale())));

        List<ProductEntity> existingProducts = productRepository.findAllByDeleted(false);

        List<TransportationDocumentProduct> incomeDocumentProducts = new ArrayList<>();

        products.forEach(incomeProductDto -> {
            //check income product and add if needed
            ProductEntity productEntity = existingProducts.stream()
                    .filter(existingProduct -> existingProduct.getArticle().equals(incomeProductDto.getArticle()))
                    .findFirst().orElse(null);

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

            //add income of product
            TransportationDocumentProduct transportationDocumentProduct = new TransportationDocumentProduct();
            transportationDocumentProduct.setCount(incomeProductDto.getCount());
            transportationDocumentProduct.setProduct(productEntity);
            transportationDocumentProduct.setPrice(incomeProductDto.getPrice());
            incomeDocumentProducts.add(transportationDocumentProduct);
        });

        //save income
        TransportationDocumentEntity documentEntity = new TransportationDocumentEntity();
        documentEntity.setName(documentName);
        documentEntity.setWarehouseTo(warehouseEntity);
        documentEntity.setProducts(incomeDocumentProducts);
        documentEntity.setType(INCOME);
        transportationDocumentRepository.save(documentEntity);

        return new IncomeDocumentDto(documentEntity);
    }

}
