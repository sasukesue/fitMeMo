package com.nus.fitmegw.service.impl;

import com.nus.fitmegw.domain.Location;
import com.nus.fitmegw.repository.LocationRepository;
import com.nus.fitmegw.service.LocationService;
import com.nus.fitmegw.service.dto.LocationDTO;
import com.nus.fitmegw.service.mapper.LocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public Mono<LocationDTO> save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        return locationRepository.save(locationMapper.toEntity(locationDTO)).map(locationMapper::toDto);
    }

    @Override
    public Mono<LocationDTO> partialUpdate(LocationDTO locationDTO) {
        log.debug("Request to partially update Location : {}", locationDTO);

        return locationRepository
            .findById(locationDTO.getId())
            .map(
                existingLocation -> {
                    locationMapper.partialUpdate(existingLocation, locationDTO);
                    return existingLocation;
                }
            )
            .flatMap(locationRepository::save)
            .map(locationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationRepository.findAllBy(pageable).map(locationMapper::toDto);
    }

    public Mono<Long> countAll() {
        return locationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id).map(locationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        return locationRepository.deleteById(id);
    }
}
