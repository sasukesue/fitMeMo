package com.nus.fitmegw.service.impl;

import com.nus.fitmegw.domain.Schedule;
import com.nus.fitmegw.repository.ScheduleRepository;
import com.nus.fitmegw.service.ScheduleService;
import com.nus.fitmegw.service.dto.ScheduleDTO;
import com.nus.fitmegw.service.mapper.ScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Schedule}.
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public Mono<ScheduleDTO> save(ScheduleDTO scheduleDTO) {
        log.debug("Request to save Schedule : {}", scheduleDTO);
        return scheduleRepository.save(scheduleMapper.toEntity(scheduleDTO)).map(scheduleMapper::toDto);
    }

    @Override
    public Mono<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO) {
        log.debug("Request to partially update Schedule : {}", scheduleDTO);

        return scheduleRepository
            .findById(scheduleDTO.getId())
            .map(
                existingSchedule -> {
                    scheduleMapper.partialUpdate(existingSchedule, scheduleDTO);
                    return existingSchedule;
                }
            )
            .flatMap(scheduleRepository::save)
            .map(scheduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ScheduleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Schedules");
        return scheduleRepository.findAllBy(pageable).map(scheduleMapper::toDto);
    }

    public Mono<Long> countAll() {
        return scheduleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ScheduleDTO> findOne(Long id) {
        log.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findById(id).map(scheduleMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Schedule : {}", id);
        return scheduleRepository.deleteById(id);
    }
}
