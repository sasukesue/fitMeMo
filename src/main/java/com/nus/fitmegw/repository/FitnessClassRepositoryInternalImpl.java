package com.nus.fitmegw.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.nus.fitmegw.domain.FitnessClass;
import com.nus.fitmegw.repository.rowmapper.FitnessClassRowMapper;
import com.nus.fitmegw.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the FitnessClass entity.
 */
@SuppressWarnings("unused")
class FitnessClassRepositoryInternalImpl implements FitnessClassRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FitnessClassRowMapper fitnessclassMapper;

    private static final Table entityTable = Table.aliased("fitness_class", EntityManager.ENTITY_ALIAS);

    public FitnessClassRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FitnessClassRowMapper fitnessclassMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.fitnessclassMapper = fitnessclassMapper;
    }

    @Override
    public Flux<FitnessClass> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<FitnessClass> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<FitnessClass> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = FitnessClassSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, FitnessClass.class, pageable, criteria);
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
    public Flux<FitnessClass> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<FitnessClass> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private FitnessClass process(Row row, RowMetadata metadata) {
        FitnessClass entity = fitnessclassMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends FitnessClass> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends FitnessClass> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update FitnessClass with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(FitnessClass entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class FitnessClassSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("class_name", table, columnPrefix + "_class_name"));
        columns.add(Column.aliased("duration", table, columnPrefix + "_duration"));
        columns.add(Column.aliased("level", table, columnPrefix + "_level"));
        columns.add(Column.aliased("instructor_name", table, columnPrefix + "_instructor_name"));

        return columns;
    }
}
