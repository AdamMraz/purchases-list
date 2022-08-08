package ru.adamdev.purchases.list.model.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    private String type;
    private Long totalDays;
    private List<Customers> customers;
    private BigDecimal totalExpenses;
    private BigDecimal avgExpenses;
    private List<Results> results;

    public Result(List<Results> results) {
        this.results = results;
    }
}
