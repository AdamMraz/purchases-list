package ru.adamdev.purchases.list.model.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Purchase {

    private String name;
    private BigDecimal expenses;

    public Purchase(String name) {
        this.name = name;
        this.expenses = new BigDecimal(0);
    }
}
