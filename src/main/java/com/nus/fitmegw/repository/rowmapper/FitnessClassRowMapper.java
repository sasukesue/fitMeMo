package com.nus.fitmegw.repository.rowmapper;

import com.nus.fitmegw.domain.FitnessClass;
import com.nus.fitmegw.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FitnessClass}, with proper type conversions.
 */
@Service
public class FitnessClassRowMapper implements BiFunction<Row, String, FitnessClass> {

    private final ColumnConverter converter;

    public FitnessClassRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FitnessClass} stored in the database.
     */
    @Override
    public FitnessClass apply(Row row, String prefix) {
        FitnessClass entity = new FitnessClass();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setClassName(converter.fromRow(row, prefix + "_class_name", String.class));
        entity.setDuration(converter.fromRow(row, prefix + "_duration", Integer.class));
        entity.setLevel(converter.fromRow(row, prefix + "_level", String.class));
        entity.setInstructorName(converter.fromRow(row, prefix + "_instructor_name", String.class));
        return entity;
    }
}
