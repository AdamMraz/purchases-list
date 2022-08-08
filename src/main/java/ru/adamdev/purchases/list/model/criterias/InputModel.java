package ru.adamdev.purchases.list.model.criterias;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputModel {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<Criteria> criterias;
}
