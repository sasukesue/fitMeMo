package com.nus.fitmegw.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nus.fitmegw.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FitnessClassDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FitnessClassDTO.class);
        FitnessClassDTO fitnessClassDTO1 = new FitnessClassDTO();
        fitnessClassDTO1.setId(1L);
        FitnessClassDTO fitnessClassDTO2 = new FitnessClassDTO();
        assertThat(fitnessClassDTO1).isNotEqualTo(fitnessClassDTO2);
        fitnessClassDTO2.setId(fitnessClassDTO1.getId());
        assertThat(fitnessClassDTO1).isEqualTo(fitnessClassDTO2);
        fitnessClassDTO2.setId(2L);
        assertThat(fitnessClassDTO1).isNotEqualTo(fitnessClassDTO2);
        fitnessClassDTO1.setId(null);
        assertThat(fitnessClassDTO1).isNotEqualTo(fitnessClassDTO2);
    }
}
