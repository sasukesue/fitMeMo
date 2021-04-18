package com.nus.fitmegw.service.impl;

import com.nus.fitmegw.domain.FitnessClass;
import com.nus.fitmegw.repository.FitnessClassRepository;
import com.nus.fitmegw.service.FitnessClassService;
import com.nus.fitmegw.service.dto.FitnessClassDTO;
import com.nus.fitmegw.service.mapper.FitnessClassMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link FitnessClass}.
 */
@Service
@Transactional
public class FitnessClassServiceImpl implements FitnessClassService {

    private final Logger log = LoggerFactory.getLogger(FitnessClassServiceImpl.class);

    private final FitnessClassRepository fitnessClassRepository;

    private final FitnessClassMapper fitnessClassMapper;

    public FitnessClassServiceImpl(FitnessClassRepository fitnessClassRepository, FitnessClassMapper fitnessClassMapper) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.fitnessClassMapper = fitnessClassMapper;
    }

    @Override
    public Mono<FitnessClassDTO> save(FitnessClassDTO fitnessClassDTO) {
        log.debug("Request to save FitnessClass : {}", fitnessClassDTO);
        return fitnessClassRepository.save(fitnessClassMapper.toEntity(fitnessClassDTO)).map(fitnessClassMapper::toDto);
    }

    @Override
    public Mono<FitnessClassDTO> partialUpdate(FitnessClassDTO fitnessClassDTO) {
        log.debug("Request to partially update FitnessClass : {}", fitnessClassDTO);

        return fitnessClassRepository
            .findById(fitnessClassDTO.getId())
            .map(
                existingFitnessClass -> {
                    fitnessClassMapper.partialUpdate(existingFitnessClass, fitnessClassDTO);
                    return existingFitnessClass;
                }
            )
            .flatMap(fitnessClassRepository::save)
            .map(fitnessClassMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<FitnessClassDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FitnessClasses");
        return fitnessClassRepository.findAllBy(pageable).map(fitnessClassMapper::toDto);
    }

    public Mono<Long> countAll() {
        return fitnessClassRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<FitnessClassDTO> findOne(Long id) {
        log.debug("Request to get FitnessClass : {}", id);
        return fitnessClassRepository.findById(id).map(fitnessClassMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete FitnessClass : {}", id);
        return fitnessClassRepository.deleteById(id);
    }
}
