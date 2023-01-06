package uz.momoit.makesense_dbridge.service.mapper;

import org.mapstruct.Mapper;
import uz.momoit.makesense_dbridge.domain.Label;
import uz.momoit.makesense_dbridge.domain.LabelHistory;
import uz.momoit.makesense_dbridge.service.dto.LabelDTO;

/**
 * Mapper for the entity {@link LabelHistory} and its DTO {@link LabelDTO}.
 */
@Mapper(componentModel = "spring")
public interface LabelHistoryMapper extends EntityMapper<LabelDTO, LabelHistory>  {
}
