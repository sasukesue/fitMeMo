package com.nus.fitmegw.repository;

import com.nus.fitmegw.domain.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Location entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocationRepository extends R2dbcRepository<Location, Long>, LocationRepositoryInternal {
    Flux<Location> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Location> findAll();

    @Override
    Mono<Location> findById(Long id);

    @Override
    <S extends Location> Mono<S> save(S entity);
}

interface LocationRepositoryInternal {
    <S extends Location> Mono<S> insert(S entity);
    <S extends Location> Mono<S> save(S entity);
    Mono<Integer> update(Location entity);

    Flux<Location> findAll();
    Mono<Location> findById(Long id);
    Flux<Location> findAllBy(Pageable pageable);
    Flux<Location> findAllBy(Pageable pageable, Criteria criteria);
}
