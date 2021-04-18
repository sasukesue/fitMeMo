package com.nus.fitmegw.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FitnessClassMapperTest {

    private FitnessClassMapper fitnessClassMapper;

    @BeforeEach
    public void setUp() {
        fitnessClassMapper = new FitnessClassMapperImpl();
    }
}
