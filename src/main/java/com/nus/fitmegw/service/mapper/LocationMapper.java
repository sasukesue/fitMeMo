package com.nus.fitmegw.service.mapper;

import com.nus.fitmegw.domain.*;
import com.nus.fitmegw.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {
    @Named("branchName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "branchName", source = "branchName")
    LocationDTO toDtoBranchName(Location location);
}
