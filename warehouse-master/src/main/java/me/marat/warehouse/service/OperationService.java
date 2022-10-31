package me.marat.warehouse.service;

import me.marat.warehouse.domain.*;
import me.marat.warehouse.exception.InsufficientItemException;
import me.marat.warehouse.model.dto.AdmissionDto;
import me.marat.warehouse.model.dto.MovingDto;
import me.marat.warehouse.model.dto.SaleDto;
import me.marat.warehouse.repository.OperationRepository;
import me.marat.warehouse.repository.ProductRepository;
import me.marat.warehouse.repository.WarehouseRepository;
import me.marat.warehouse.util.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.marat.warehouse.domain.OperationType.*;

@Service
public class OperationService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;



    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    public List<Operation> getAllByProduct(Long article) {
        Product product = productService.searchByArticle(article);
        return operationRepository.getAllByProductId(product.getId());
    }

    public List<Operation> getAllByWarehouse(Long warehouseId) {
        return operationRepository.getAllByWarehouse(warehouseId);

    }

    public List<Operation> getAllByWarehouseAndProduct(Long warehouseId, Long article) {
        Product product = productService.searchByArticle(article);
        return operationRepository.getAllByWarehouseAndProduct(product.getId(), warehouseId);
    }

    public void admission(AdmissionDto dto) {
        InputValidator.validateAdmissionDto(dto);
        dto.getProductList().forEach(item -> {
            Product product = productService.searchByArticle(item.getArticle());
            Warehouse warehouse = warehouseRepository.getReferenceById(dto.getWarehouseId());

            product.setLastAdmissionPrice(item.getPrice());

            Operation operation = new Operation();
            operation.setOperationNumber(dto.getNumber());
            operation.setCount(item.getCount());
            operation.setPrice(item.getPrice());
            operation.setOperationType(ADMISSION);
            operation.setWarehouseTo(dto.getWarehouseId());
            operation.setProductId(product.getId());

            changeItemCountInWarehouse(ADMISSION, product, warehouse, item.getCount());

            operationRepository.save(operation);
            productService.update(product);
        });
    }

    public void sale(SaleDto dto) {
        InputValidator.validateSaleDto(dto);
        dto.getProductList().forEach(item -> {
            Product product = productService.searchByArticle(item.getArticle());
            Warehouse warehouse = warehouseRepository.getReferenceById(dto.getWarehouseId());

            Integer count = count(product.getId(), dto.getWarehouseId());
            if (count < item.getCount()) {
                throw new InsufficientItemException(product.getName(), count);
            }
            product.setLastSellingPrice(item.getPrice());

            Operation operation = new Operation();
            operation.setOperationNumber(dto.getNumber());
            operation.setCount(item.getCount());
            operation.setPrice(item.getPrice());
            operation.setOperationType(SALE);
            operation.setWarehouseFrom(dto.getWarehouseId());
            operation.setProductId(product.getId());

            changeItemCountInWarehouse(SALE, product, warehouse, item.getCount());

            operationRepository.save(operation);
            productService.update(product);
        });
    }

    public void moving(MovingDto dto) {
        Warehouse warehouseTo = warehouseRepository.getReferenceById(dto.getWarehouseTo());
        Warehouse warehouseFrom = warehouseRepository.getReferenceById(dto.getWarehouseFrom());

        dto.getProductList().forEach(item -> {
            Product product = productService.searchByArticle(item.getArticle());

            product.setLastAdmissionPrice(item.getPrice());

            Operation operation = new Operation();
            operation.setOperationNumber(dto.getNumber());
            operation.setCount(item.getCount());
            operation.setPrice(item.getPrice());
            operation.setOperationType(MOVING);
            operation.setWarehouseFrom(dto.getWarehouseFrom());
            operation.setWarehouseTo(dto.getWarehouseTo());
            operation.setProductId(product.getId());

            changeItemCountInWarehouse(ADMISSION, product, warehouseTo, item.getCount());
            changeItemCountInWarehouse(SALE, product, warehouseFrom, item.getCount());

            operationRepository.save(operation);
            productService.update(product);
        });

    }

    protected Integer count(Long productId, Long warehouseId) {
        List<Operation> operationList = operationRepository.getAllByProductId(productId);
        Integer count = 0;
        for (Operation op:operationList) {
            switch (op.getOperationType()) {
                case ADMISSION: {
                    count += op.getCount();
                    break;
                }
                case SALE: {
                    count -= op.getCount();
                    break;
                }
                case MOVING: {
                    if (warehouseId.equals(op.getWarehouseTo())) {
                        count += op.getCount();
                    } else {
                        count -= op.getCount();
                    }
                }
            }
        }
        return count;
    }

    protected void changeItemCountInWarehouse(OperationType type, Product product, Warehouse warehouse, Integer count) {
        if (product.getWarehouses().isEmpty()) {
            WarehouseProduct warehouseProduct = new WarehouseProduct();
            warehouseProduct.setWarehouse(warehouse);
            warehouseProduct.setProduct(product);
            warehouseProduct.setCount(count);
            product.getWarehouses().add(warehouseProduct);
        } else {
            product.getWarehouses().forEach(wp -> {
                if (wp.getId().getWarehouseId().equals(warehouse.getId())) {
                    Integer existingCount = wp.getCount();
                    if (type == ADMISSION) {
                        wp.setCount(existingCount + count);
                    } else {
                        if (existingCount < count) {
                            throw new InsufficientItemException(product.getName(), existingCount);
                        }
                        wp.setCount(existingCount - count);
                    }
                }
            });
        }
    }
    public Map<String, Integer> getProductsInWarehouse(Long warehouseId) {
        List<Product> products = productRepository.findAll();
        Map<String, Integer> productMap = new HashMap<>();
        for (Product product : products) {
            Integer count = 0;
            List<Operation> operations = operationRepository.getAllByWarehouseAndProduct(warehouseId,product.getId());
            for (Operation operation : operations) {
                if (operation.getOperationType() == ADMISSION) {
                    count += operation.getCount();
                } else if (operation.getOperationType() == SALE) {
                    count -= operation.getCount();
                } else {
                    if (warehouseId.equals(operation.getWarehouseTo())) {
                        count += operation.getCount();
                    } else {
                        count -= operation.getCount();
                    }
                }
            }
            productMap.put(product.getName(),count);
        }
        return productMap;

    }
}
