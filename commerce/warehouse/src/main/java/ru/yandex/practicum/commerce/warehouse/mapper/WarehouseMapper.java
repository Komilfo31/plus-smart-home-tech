package ru.yandex.practicum.commerce.warehouse.mapper;

import interaction.model.warehouse.DimensionDto;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;


@Component
public class WarehouseMapper {

    public Dimension dtoToDimension(DimensionDto dto) {
        if (dto == null) {
            return null;
        }

        return new Dimension(
                dto.getWidth(),
                dto.getHeight(),
                dto.getDepth()
        );
    }

    public DimensionDto dimensionToDto(Dimension dimension) {
        if (dimension == null) {
            return null;
        }

        return DimensionDto.builder()
                .width(dimension.getWidth())
                .height(dimension.getHeight())
                .depth(dimension.getDepth())
                .build();
    }
}
