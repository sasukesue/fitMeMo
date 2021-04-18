package com.nus.fitmegw.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.nus.fitmegw.IntegrationTest;
import com.nus.fitmegw.domain.FitnessClass;
import com.nus.fitmegw.repository.FitnessClassRepository;
import com.nus.fitmegw.service.EntityManager;
import com.nus.fitmegw.service.dto.FitnessClassDTO;
import com.nus.fitmegw.service.mapper.FitnessClassMapper;
import java.time.Duration;
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
 * Integration tests for the {@link FitnessClassResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class FitnessClassResourceIT {

    private static final String DEFAULT_CLASS_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CLASS_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final String DEFAULT_LEVEL = "AAAAAAAAAA";
    private static final String UPDATED_LEVEL = "BBBBBBBBBB";

    private static final String DEFAULT_INSTRUCTOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INSTRUCTOR_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fitness-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private FitnessClassMapper fitnessClassMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FitnessClass fitnessClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FitnessClass createEntity(EntityManager em) {
        FitnessClass fitnessClass = new FitnessClass()
            .className(DEFAULT_CLASS_NAME)
            .duration(DEFAULT_DURATION)
            .level(DEFAULT_LEVEL)
            .instructorName(DEFAULT_INSTRUCTOR_NAME);
        return fitnessClass;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FitnessClass createUpdatedEntity(EntityManager em) {
        FitnessClass fitnessClass = new FitnessClass()
            .className(UPDATED_CLASS_NAME)
            .duration(UPDATED_DURATION)
            .level(UPDATED_LEVEL)
            .instructorName(UPDATED_INSTRUCTOR_NAME);
        return fitnessClass;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FitnessClass.class).block();
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
        fitnessClass = createEntity(em);
    }

    @Test
    void createFitnessClass() throws Exception {
        int databaseSizeBeforeCreate = fitnessClassRepository.findAll().collectList().block().size();
        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeCreate + 1);
        FitnessClass testFitnessClass = fitnessClassList.get(fitnessClassList.size() - 1);
        assertThat(testFitnessClass.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
        assertThat(testFitnessClass.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testFitnessClass.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testFitnessClass.getInstructorName()).isEqualTo(DEFAULT_INSTRUCTOR_NAME);
    }

    @Test
    void createFitnessClassWithExistingId() throws Exception {
        // Create the FitnessClass with an existing ID
        fitnessClass.setId(1L);
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        int databaseSizeBeforeCreate = fitnessClassRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkClassNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fitnessClassRepository.findAll().collectList().block().size();
        // set the field null
        fitnessClass.setClassName(null);

        // Create the FitnessClass, which fails.
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = fitnessClassRepository.findAll().collectList().block().size();
        // set the field null
        fitnessClass.setDuration(null);

        // Create the FitnessClass, which fails.
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = fitnessClassRepository.findAll().collectList().block().size();
        // set the field null
        fitnessClass.setLevel(null);

        // Create the FitnessClass, which fails.
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkInstructorNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fitnessClassRepository.findAll().collectList().block().size();
        // set the field null
        fitnessClass.setInstructorName(null);

        // Create the FitnessClass, which fails.
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFitnessClasses() {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        // Get all the fitnessClassList
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
            .value(hasItem(fitnessClass.getId().intValue()))
            .jsonPath("$.[*].className")
            .value(hasItem(DEFAULT_CLASS_NAME))
            .jsonPath("$.[*].duration")
            .value(hasItem(DEFAULT_DURATION))
            .jsonPath("$.[*].level")
            .value(hasItem(DEFAULT_LEVEL))
            .jsonPath("$.[*].instructorName")
            .value(hasItem(DEFAULT_INSTRUCTOR_NAME));
    }

    @Test
    void getFitnessClass() {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        // Get the fitnessClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, fitnessClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(fitnessClass.getId().intValue()))
            .jsonPath("$.className")
            .value(is(DEFAULT_CLASS_NAME))
            .jsonPath("$.duration")
            .value(is(DEFAULT_DURATION))
            .jsonPath("$.level")
            .value(is(DEFAULT_LEVEL))
            .jsonPath("$.instructorName")
            .value(is(DEFAULT_INSTRUCTOR_NAME));
    }

    @Test
    void getNonExistingFitnessClass() {
        // Get the fitnessClass
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFitnessClass() throws Exception {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();

        // Update the fitnessClass
        FitnessClass updatedFitnessClass = fitnessClassRepository.findById(fitnessClass.getId()).block();
        updatedFitnessClass
            .className(UPDATED_CLASS_NAME)
            .duration(UPDATED_DURATION)
            .level(UPDATED_LEVEL)
            .instructorName(UPDATED_INSTRUCTOR_NAME);
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(updatedFitnessClass);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fitnessClassDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
        FitnessClass testFitnessClass = fitnessClassList.get(fitnessClassList.size() - 1);
        assertThat(testFitnessClass.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
        assertThat(testFitnessClass.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testFitnessClass.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testFitnessClass.getInstructorName()).isEqualTo(UPDATED_INSTRUCTOR_NAME);
    }

    @Test
    void putNonExistingFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fitnessClassDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFitnessClassWithPatch() throws Exception {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();

        // Update the fitnessClass using partial update
        FitnessClass partialUpdatedFitnessClass = new FitnessClass();
        partialUpdatedFitnessClass.setId(fitnessClass.getId());

        partialUpdatedFitnessClass.duration(UPDATED_DURATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFitnessClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFitnessClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
        FitnessClass testFitnessClass = fitnessClassList.get(fitnessClassList.size() - 1);
        assertThat(testFitnessClass.getClassName()).isEqualTo(DEFAULT_CLASS_NAME);
        assertThat(testFitnessClass.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testFitnessClass.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testFitnessClass.getInstructorName()).isEqualTo(DEFAULT_INSTRUCTOR_NAME);
    }

    @Test
    void fullUpdateFitnessClassWithPatch() throws Exception {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();

        // Update the fitnessClass using partial update
        FitnessClass partialUpdatedFitnessClass = new FitnessClass();
        partialUpdatedFitnessClass.setId(fitnessClass.getId());

        partialUpdatedFitnessClass
            .className(UPDATED_CLASS_NAME)
            .duration(UPDATED_DURATION)
            .level(UPDATED_LEVEL)
            .instructorName(UPDATED_INSTRUCTOR_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFitnessClass.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFitnessClass))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
        FitnessClass testFitnessClass = fitnessClassList.get(fitnessClassList.size() - 1);
        assertThat(testFitnessClass.getClassName()).isEqualTo(UPDATED_CLASS_NAME);
        assertThat(testFitnessClass.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testFitnessClass.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testFitnessClass.getInstructorName()).isEqualTo(UPDATED_INSTRUCTOR_NAME);
    }

    @Test
    void patchNonExistingFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, fitnessClassDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFitnessClass() throws Exception {
        int databaseSizeBeforeUpdate = fitnessClassRepository.findAll().collectList().block().size();
        fitnessClass.setId(count.incrementAndGet());

        // Create the FitnessClass
        FitnessClassDTO fitnessClassDTO = fitnessClassMapper.toDto(fitnessClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fitnessClassDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FitnessClass in the database
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFitnessClass() {
        // Initialize the database
        fitnessClassRepository.save(fitnessClass).block();

        int databaseSizeBeforeDelete = fitnessClassRepository.findAll().collectList().block().size();

        // Delete the fitnessClass
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, fitnessClass.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<FitnessClass> fitnessClassList = fitnessClassRepository.findAll().collectList().block();
        assertThat(fitnessClassList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
