package com.nus.fitmegw.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.nus.fitmegw.domain.Schedule;
import com.nus.fitmegw.repository.rowmapper.FitnessClassRowMapper;
import com.nus.fitmegw.repository.rowmapper.LocationRowMapper;
import com.nus.fitmegw.repository.rowmapper.ScheduleRowMapper;
import com.nus.fitmegw.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Schedule entity.
 */
@SuppressWarnings("unused")
class ScheduleRepositoryInternalImpl implements ScheduleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LocationRowMapper locationMapper;
    private final FitnessClassRowMapper fitnessclassMapper;
    private final ScheduleRowMapper scheduleMapper;

    private static final Table entityTable = Table.aliased("schedule", EntityManager.ENTITY_ALIAS);
    private static final Table schLocTable = Table.aliased("location", "schLoc");
    private static final Table schClassTable = Table.aliased("fitness_class", "schClass");

    public ScheduleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        LocationRowMapper locationMapper,
        FitnessClassRowMapper fitnessclassMapper,
        ScheduleRowMapper scheduleMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.locationMapper = locationMapper;
        this.fitnessclassMapper = fitnessclassMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public Flux<Schedule> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Schedule> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Schedule> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ScheduleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(LocationSqlHelper.getColumns(schLocTable, "schLoc"));
        columns.addAll(FitnessClassSqlHelper.getColumns(schClassTable, "schClass"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(schLocTable)
            .on(Column.create("sch_loc_id", entityTable))
            .equals(Column.create("id", schLocTable))
            .leftOuterJoin(schClassTable)
            .on(Column.create("sch_class_id", entityTable))
            .equals(Column.create("id", schClassTable));

        String select = entityManager.createSelect(selectFrom, Schedule.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Schedule> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Schedule> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Schedule process(Row row, RowMetadata metadata) {
        Schedule entity = scheduleMapper.apply(row, "e");
        entity.setSchLoc(locationMapper.apply(row, "schLoc"));
        entity.setSchClass(fitnessclassMapper.apply(row, "schClass"));
        return entity;
    }

    @Override
    public <S extends Schedule> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Schedule> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Schedule with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Schedule entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ScheduleSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date_time", table, columnPrefix + "_date_time"));
        columns.add(Column.aliased("available_slots", table, columnPrefix + "_available_slots"));

        columns.add(Column.aliased("sch_loc_id", table, columnPrefix + "_sch_loc_id"));
        columns.add(Column.aliased("sch_class_id", table, columnPrefix + "_sch_class_id"));
        return columns;
    }
}
