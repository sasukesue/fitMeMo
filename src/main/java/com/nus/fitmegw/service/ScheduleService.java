package com.nus.fitmegw.service;

import com.nus.fitmegw.service.dto.ScheduleDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.nus.fitmegw.domain.Schedule}.
 */
public interface ScheduleService {
    /**
     * Save a schedule.
     *
     * @param scheduleDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ScheduleDTO> save(ScheduleDTO scheduleDTO);

    /**
     * Partially updates a schedule.
     *
     * @param scheduleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ScheduleDTO> partialUpdate(ScheduleDTO scheduleDTO);

    /**
     * Get all the schedules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ScheduleDTO> findAll(Pageable pageable);

    /**
     * Returns the number of schedules available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" schedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ScheduleDTO> findOne(Long id);

    /**
     * Delete the "id" schedule.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
