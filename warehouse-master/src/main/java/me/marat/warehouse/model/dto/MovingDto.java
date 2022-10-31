package me.marat.warehouse.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovingDto {
    private Long number;
    private Long article;
    private Long warehouseFrom;
    private Long warehouseTo;
    private Integer count;
    private List<OperationItemDto> productList;
}
