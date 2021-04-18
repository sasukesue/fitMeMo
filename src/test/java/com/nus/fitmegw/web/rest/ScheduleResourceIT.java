package com.nus.fitmegw.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.nus.fitmegw.IntegrationTest;
import com.nus.fitmegw.domain.Schedule;
import com.nus.fitmegw.repository.ScheduleRepository;
import com.nus.fitmegw.service.EntityManager;
import com.nus.fitmegw.service.dto.ScheduleDTO;
import com.nus.fitmegw.service.mapper.ScheduleMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ScheduleResourceIT {

    private static final LocalDate DEFAULT_DATE_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_AVAILABLE_SLOTS = 1;
    private static final Integer UPDATED_AVAILABLE_SLOTS = 2;

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Schedule schedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule().dateTime(DEFAULT_DATE_TIME).availableSlots(DEFAULT_AVAILABLE_SLOTS);
        return schedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity(EntityManager em) {
        Schedule schedule = new Schedule().dateTime(UPDATED_DATE_TIME).availableSlots(UPDATED_AVAILABLE_SLOTS);
        return schedule;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Schedule.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        schedule = createEntity(em);
    }

    @Test
    void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().collectList().block().size();
        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testSchedule.getAvailableSlots()).isEqualTo(DEFAULT_AVAILABLE_SLOTS);
    }

    @Test
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        int databaseSizeBeforeCreate = scheduleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().collectList().block().size();
        // set the field null
        schedule.setDateTime(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAvailableSlotsIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().collectList().block().size();
        // set the field null
        schedule.setAvailableSlots(null);

        // Create the Schedule, which fails.
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSchedules() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        // Get all the scheduleList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(schedule.getId().intValue()))
            .jsonPath("$.[*].dateTime")
            .value(hasItem(DEFAULT_DATE_TIME.toString()))
            .jsonPath("$.[*].availableSlots")
            .value(hasItem(DEFAULT_AVAILABLE_SLOTS));
    }

    @Test
    void getSchedule() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        // Get the schedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(schedule.getId().intValue()))
            .jsonPath("$.dateTime")
            .value(is(DEFAULT_DATE_TIME.toString()))
            .jsonPath("$.availableSlots")
            .value(is(DEFAULT_AVAILABLE_SLOTS));
    }

    @Test
    void getNonExistingSchedule() {
        // Get the schedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).block();
        updatedSchedule.dateTime(UPDATED_DATE_TIME).availableSlots(UPDATED_AVAILABLE_SLOTS);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(updatedSchedule);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, scheduleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testSchedule.getAvailableSlots()).isEqualTo(UPDATED_AVAILABLE_SLOTS);
    }

    @Test
    void putNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, scheduleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getDateTime()).isEqualTo(DEFAULT_DATE_TIME);
        assertThat(testSchedule.getAvailableSlots()).isEqualTo(DEFAULT_AVAILABLE_SLOTS);
    }

    @Test
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.dateTime(UPDATED_DATE_TIME).availableSlots(UPDATED_AVAILABLE_SLOTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getDateTime()).isEqualTo(UPDATED_DATE_TIME);
        assertThat(testSchedule.getAvailableSlots()).isEqualTo(UPDATED_AVAILABLE_SLOTS);
    }

    @Test
    void patchNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, scheduleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSchedule() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeDelete = scheduleRepository.findAll().collectList().block().size();

        // Delete the schedule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
