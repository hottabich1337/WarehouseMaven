package me.marat.warehouse.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseDto {
    private Long id;
    private String name;
    private Map<String,Integer> maps;

    public WarehouseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
