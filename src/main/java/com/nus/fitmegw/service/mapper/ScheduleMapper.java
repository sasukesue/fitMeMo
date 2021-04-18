package com.nus.fitmegw.service.mapper;

import com.nus.fitmegw.domain.*;
import com.nus.fitmegw.service.dto.ScheduleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring", uses = { LocationMapper.class, FitnessClassMapper.class })
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "schLoc", source = "schLoc", qualifiedByName = "branchName")
    @Mapping(target = "schClass", source = "schClass", qualifiedByName = "className")
    ScheduleDTO toDto(Schedule s);
}
