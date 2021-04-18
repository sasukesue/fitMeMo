package com.nus.fitmegw.repository;

import com.nus.fitmegw.domain.FitnessClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the FitnessClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FitnessClassRepository extends R2dbcRepository<FitnessClass, Long>, FitnessClassRepositoryInternal {
    Flux<FitnessClass> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<FitnessClass> findAll();

    @Override
    Mono<FitnessClass> findById(Long id);

    @Override
    <S extends FitnessClass> Mono<S> save(S entity);
}

interface FitnessClassRepositoryInternal {
    <S extends FitnessClass> Mono<S> insert(S entity);
    <S extends FitnessClass> Mono<S> save(S entity);
    Mono<Integer> update(FitnessClass entity);

    Flux<FitnessClass> findAll();
    Mono<FitnessClass> findById(Long id);
    Flux<FitnessClass> findAllBy(Pageable pageable);
    Flux<FitnessClass> findAllBy(Pageable pageable, Criteria criteria);
}
