package ru.adamdev.purchases.list.model.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customers {

    private String name;
    private List<Purchase> purchases;
    private BigDecimal totalExpenses;

    public Customers(String name) {
        this.name = name;
        this.purchases = new ArrayList<>();
        this.totalExpenses = new BigDecimal(0);
    }
}
