package com.nus.fitmegw.web.rest;

import com.nus.fitmegw.repository.ScheduleRepository;
import com.nus.fitmegw.service.ScheduleService;
import com.nus.fitmegw.service.dto.ScheduleDTO;
import com.nus.fitmegw.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.nus.fitmegw.domain.Schedule}.
 */
@RestController
@RequestMapping("/api")
public class ScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    private static final String ENTITY_NAME = "schedule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleService scheduleService;

    private final ScheduleRepository scheduleRepository;

    public ScheduleResource(ScheduleService scheduleService, ScheduleRepository scheduleRepository) {
        this.scheduleService = scheduleService;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * {@code POST  /schedules} : Create a new schedule.
     *
     * @param scheduleDTO the scheduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleDTO, or with status {@code 400 (Bad Request)} if the schedule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/schedules")
    public Mono<ResponseEntity<ScheduleDTO>> createSchedule(@Valid @RequestBody ScheduleDTO scheduleDTO) throws URISyntaxException {
        log.debug("REST request to save Schedule : {}", scheduleDTO);
        if (scheduleDTO.getId() != null) {
            throw new BadRequestAlertException("A new schedule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return scheduleService
            .save(scheduleDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/schedules/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /schedules/:id} : Updates an existing schedule.
     *
     * @param id the id of the scheduleDTO to save.
     * @param scheduleDTO the scheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/schedules/{id}")
    public Mono<ResponseEntity<ScheduleDTO>> updateSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleDTO scheduleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Schedule : {}, {}", id, scheduleDTO);
        if (scheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return scheduleService
                        .save(scheduleDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /schedules/:id} : Partial updates given fields of an existing schedule, field will ignore if it is null
     *
     * @param id the id of the scheduleDTO to save.
     * @param scheduleDTO the scheduleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleDTO,
     * or with status {@code 400 (Bad Request)} if the scheduleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/schedules/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ScheduleDTO>> partialUpdateSchedule(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleDTO scheduleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Schedule partially : {}, {}", id, scheduleDTO);
        if (scheduleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ScheduleDTO> result = scheduleService.partialUpdate(scheduleDTO);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /schedules} : get all the schedules.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of schedules in body.
     */
    @GetMapping("/schedules")
    public Mono<ResponseEntity<List<ScheduleDTO>>> getAllSchedules(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Schedules");
        return scheduleService
            .countAll()
            .zipWith(scheduleService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /schedules/:id} : get the "id" schedule.
     *
     * @param id the id of the scheduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/schedules/{id}")
    public Mono<ResponseEntity<ScheduleDTO>> getSchedule(@PathVariable Long id) {
        log.debug("REST request to get Schedule : {}", id);
        Mono<ScheduleDTO> scheduleDTO = scheduleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleDTO);
    }

    /**
     * {@code DELETE  /schedules/:id} : delete the "id" schedule.
     *
     * @param id the id of the scheduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/schedules/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSchedule(@PathVariable Long id) {
        log.debug("REST request to delete Schedule : {}", id);
        return scheduleService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
