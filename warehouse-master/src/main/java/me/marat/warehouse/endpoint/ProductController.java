package me.marat.warehouse.endpoint;

import me.marat.warehouse.model.api.ApiResponse;
import me.marat.warehouse.model.dto.ProductDto;
import me.marat.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/")
    public ApiResponse<ProductDto> create(@RequestBody ProductDto dto) {
        ProductDto product = productService.createProduct(dto);
        return ApiResponse.ok(product);
    }
}
