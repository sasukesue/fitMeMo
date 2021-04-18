package com.nus.fitmegw.repository;

import com.nus.fitmegw.domain.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Schedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleRepository extends R2dbcRepository<Schedule, Long>, ScheduleRepositoryInternal {
    Flux<Schedule> findAllBy(Pageable pageable);

    @Query("SELECT * FROM schedule entity WHERE entity.sch_loc_id = :id")
    Flux<Schedule> findBySchLoc(Long id);

    @Query("SELECT * FROM schedule entity WHERE entity.sch_loc_id IS NULL")
    Flux<Schedule> findAllWhereSchLocIsNull();

    @Query("SELECT * FROM schedule entity WHERE entity.sch_class_id = :id")
    Flux<Schedule> findBySchClass(Long id);

    @Query("SELECT * FROM schedule entity WHERE entity.sch_class_id IS NULL")
    Flux<Schedule> findAllWhereSchClassIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Schedule> findAll();

    @Override
    Mono<Schedule> findById(Long id);

    @Override
    <S extends Schedule> Mono<S> save(S entity);
}

interface ScheduleRepositoryInternal {
    <S extends Schedule> Mono<S> insert(S entity);
    <S extends Schedule> Mono<S> save(S entity);
    Mono<Integer> update(Schedule entity);

    Flux<Schedule> findAll();
    Mono<Schedule> findById(Long id);
    Flux<Schedule> findAllBy(Pageable pageable);
    Flux<Schedule> findAllBy(Pageable pageable, Criteria criteria);
}
