package ru.adamdev.purchases.list.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.model.dto.BuyerDto;

@Mapper(unmappedSourcePolicy = ReportingPolicy.ERROR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DtoMapper {

    BuyerDto getBuyerDto(BuyerEntity entity);
}
