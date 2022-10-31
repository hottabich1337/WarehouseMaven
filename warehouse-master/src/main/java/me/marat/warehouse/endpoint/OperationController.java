package me.marat.warehouse.endpoint;

import me.marat.warehouse.model.dto.AdmissionDto;
import me.marat.warehouse.model.api.ApiResponse;
import me.marat.warehouse.model.dto.MovingDto;
import me.marat.warehouse.model.dto.SaleDto;
import me.marat.warehouse.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operation")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @PostMapping("/admission")
    public ApiResponse admission(@RequestBody AdmissionDto admissionDto) {
        operationService.admission(admissionDto);
        return ApiResponse.ok();
    }

    @PostMapping("/sale")
    public ApiResponse sale(@RequestBody SaleDto saleDto) {
        operationService.sale(saleDto);
        return ApiResponse.ok();
    }

    @PostMapping("/moving")
    public ApiResponse moving(@RequestBody MovingDto movingDto) {
        operationService.moving(movingDto);
        return ApiResponse.ok();
    }

}
