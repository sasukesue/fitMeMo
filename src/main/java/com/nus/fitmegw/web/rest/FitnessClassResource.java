package com.nus.fitmegw.web.rest;

import com.nus.fitmegw.repository.FitnessClassRepository;
import com.nus.fitmegw.service.FitnessClassService;
import com.nus.fitmegw.service.dto.FitnessClassDTO;
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
 * REST controller for managing {@link com.nus.fitmegw.domain.FitnessClass}.
 */
@RestController
@RequestMapping("/api")
public class FitnessClassResource {

    private final Logger log = LoggerFactory.getLogger(FitnessClassResource.class);

    private static final String ENTITY_NAME = "fitnessClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FitnessClassService fitnessClassService;

    private final FitnessClassRepository fitnessClassRepository;

    public FitnessClassResource(FitnessClassService fitnessClassService, FitnessClassRepository fitnessClassRepository) {
        this.fitnessClassService = fitnessClassService;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    /**
     * {@code POST  /fitness-classes} : Create a new fitnessClass.
     *
     * @param fitnessClassDTO the fitnessClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fitnessClassDTO, or with status {@code 400 (Bad Request)} if the fitnessClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fitness-classes")
    public Mono<ResponseEntity<FitnessClassDTO>> createFitnessClass(@Valid @RequestBody FitnessClassDTO fitnessClassDTO)
        throws URISyntaxException {
        log.debug("REST request to save FitnessClass : {}", fitnessClassDTO);
        if (fitnessClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new fitnessClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return fitnessClassService
            .save(fitnessClassDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/fitness-classes/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /fitness-classes/:id} : Updates an existing fitnessClass.
     *
     * @param id the id of the fitnessClassDTO to save.
     * @param fitnessClassDTO the fitnessClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fitnessClassDTO,
     * or with status {@code 400 (Bad Request)} if the fitnessClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fitnessClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fitness-classes/{id}")
    public Mono<ResponseEntity<FitnessClassDTO>> updateFitnessClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FitnessClassDTO fitnessClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FitnessClass : {}, {}", id, fitnessClassDTO);
        if (fitnessClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fitnessClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fitnessClassRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return fitnessClassService
                        .save(fitnessClassDTO)
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
     * {@code PATCH  /fitness-classes/:id} : Partial updates given fields of an existing fitnessClass, field will ignore if it is null
     *
     * @param id the id of the fitnessClassDTO to save.
     * @param fitnessClassDTO the fitnessClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fitnessClassDTO,
     * or with status {@code 400 (Bad Request)} if the fitnessClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fitnessClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fitnessClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fitness-classes/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<FitnessClassDTO>> partialUpdateFitnessClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FitnessClassDTO fitnessClassDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FitnessClass partially : {}, {}", id, fitnessClassDTO);
        if (fitnessClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fitnessClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fitnessClassRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<FitnessClassDTO> result = fitnessClassService.partialUpdate(fitnessClassDTO);

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
     * {@code GET  /fitness-classes} : get all the fitnessClasses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fitnessClasses in body.
     */
    @GetMapping("/fitness-classes")
    public Mono<ResponseEntity<List<FitnessClassDTO>>> getAllFitnessClasses(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of FitnessClasses");
        return fitnessClassService
            .countAll()
            .zipWith(fitnessClassService.findAll(pageable).collectList())
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
     * {@code GET  /fitness-classes/:id} : get the "id" fitnessClass.
     *
     * @param id the id of the fitnessClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fitnessClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fitness-classes/{id}")
    public Mono<ResponseEntity<FitnessClassDTO>> getFitnessClass(@PathVariable Long id) {
        log.debug("REST request to get FitnessClass : {}", id);
        Mono<FitnessClassDTO> fitnessClassDTO = fitnessClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fitnessClassDTO);
    }

    /**
     * {@code DELETE  /fitness-classes/:id} : delete the "id" fitnessClass.
     *
     * @param id the id of the fitnessClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fitness-classes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFitnessClass(@PathVariable Long id) {
        log.debug("REST request to delete FitnessClass : {}", id);
        return fitnessClassService
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
