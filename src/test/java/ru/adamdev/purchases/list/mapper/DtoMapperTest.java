package ru.adamdev.purchases.list.mapper;

import org.junit.jupiter.api.Test;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.model.dto.BuyerDto;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    private final DtoMapper mapper = new DtoMapperImpl();

    @Test
    void getBuyerDto() {
        BuyerEntity entity = PODAM_FACTORY.manufacturePojo(BuyerEntity.class);
        BuyerDto dto = mapper.getBuyerDto(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getSecondName(), dto.getSecondName());
    }
}