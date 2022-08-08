package ru.adamdev.purchases.list.model.criterias;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Criteria {

    private String lastName;
    private String productName;
    private Integer minTimes;
    private BigDecimal minExpenses;
    private BigDecimal maxExpenses;
    private Integer badCustomers;
}
