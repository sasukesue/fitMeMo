package com.nus.fitmegw.service;

import com.nus.fitmegw.service.dto.FitnessClassDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.nus.fitmegw.domain.FitnessClass}.
 */
public interface FitnessClassService {
    /**
     * Save a fitnessClass.
     *
     * @param fitnessClassDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<FitnessClassDTO> save(FitnessClassDTO fitnessClassDTO);

    /**
     * Partially updates a fitnessClass.
     *
     * @param fitnessClassDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<FitnessClassDTO> partialUpdate(FitnessClassDTO fitnessClassDTO);

    /**
     * Get all the fitnessClasses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<FitnessClassDTO> findAll(Pageable pageable);

    /**
     * Returns the number of fitnessClasses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" fitnessClass.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<FitnessClassDTO> findOne(Long id);

    /**
     * Delete the "id" fitnessClass.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
