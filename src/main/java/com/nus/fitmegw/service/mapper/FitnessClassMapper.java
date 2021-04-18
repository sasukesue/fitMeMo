package com.nus.fitmegw.service.mapper;

import com.nus.fitmegw.domain.*;
import com.nus.fitmegw.service.dto.FitnessClassDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FitnessClass} and its DTO {@link FitnessClassDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FitnessClassMapper extends EntityMapper<FitnessClassDTO, FitnessClass> {
    @Named("className")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "className", source = "className")
    FitnessClassDTO toDtoClassName(FitnessClass fitnessClass);
}
