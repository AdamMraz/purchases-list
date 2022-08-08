package ru.adamdev.purchases.list.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyerDto {

    @JsonIgnore
    private Long id;
    private String firstName;
    private String secondName;
}
