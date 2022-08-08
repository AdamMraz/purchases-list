package ru.adamdev.purchases.list.model.results;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.adamdev.purchases.list.model.criterias.Criteria;
import ru.adamdev.purchases.list.model.dto.BuyerDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Results {

    private Criteria criteria;
    private List<BuyerDto> results;
}
