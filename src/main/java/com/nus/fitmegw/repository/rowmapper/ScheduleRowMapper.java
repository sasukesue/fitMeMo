package com.nus.fitmegw.repository.rowmapper;

import com.nus.fitmegw.domain.Schedule;
import com.nus.fitmegw.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Schedule}, with proper type conversions.
 */
@Service
public class ScheduleRowMapper implements BiFunction<Row, String, Schedule> {

    private final ColumnConverter converter;

    public ScheduleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Schedule} stored in the database.
     */
    @Override
    public Schedule apply(Row row, String prefix) {
        Schedule entity = new Schedule();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDateTime(converter.fromRow(row, prefix + "_date_time", LocalDate.class));
        entity.setAvailableSlots(converter.fromRow(row, prefix + "_available_slots", Integer.class));
        entity.setSchLocId(converter.fromRow(row, prefix + "_sch_loc_id", Long.class));
        entity.setSchClassId(converter.fromRow(row, prefix + "_sch_class_id", Long.class));
        return entity;
    }
}
