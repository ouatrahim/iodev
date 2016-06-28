package com.iodev.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.iodev.domain.Salaire;
import com.iodev.repository.SalaireRepository;
import com.iodev.repository.search.SalaireSearchRepository;
import com.iodev.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * REST controller for managing Salaire.
 */
@RestController
@RequestMapping("/api")
public class SalaireResource {

    private final Logger log = LoggerFactory.getLogger(SalaireResource.class);
        
    @Inject
    private SalaireRepository salaireRepository;
    
    @Inject
    private SalaireSearchRepository salaireSearchRepository;
    
    /**
     * POST  /salaires : Create a new salaire.
     *
     * @param salaire the salaire to create
     * @return the ResponseEntity with status 201 (Created) and with body the new salaire, or with status 400 (Bad Request) if the salaire has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/salaires",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Salaire> createSalaire(@Valid @RequestBody Salaire salaire) throws URISyntaxException {
        log.debug("REST request to save Salaire : {}", salaire);
        if (salaire.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salaire", "idexists", "A new salaire cannot already have an ID")).body(null);
        }
        Salaire result = salaireRepository.save(salaire);
        salaireSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/salaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("salaire", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /salaires : Updates an existing salaire.
     *
     * @param salaire the salaire to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated salaire,
     * or with status 400 (Bad Request) if the salaire is not valid,
     * or with status 500 (Internal Server Error) if the salaire couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/salaires",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Salaire> updateSalaire(@Valid @RequestBody Salaire salaire) throws URISyntaxException {
        log.debug("REST request to update Salaire : {}", salaire);
        if (salaire.getId() == null) {
            return createSalaire(salaire);
        }
        Salaire result = salaireRepository.save(salaire);
        salaireSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("salaire", salaire.getId().toString()))
            .body(result);
    }

    /**
     * GET  /salaires : get all the salaires.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of salaires in body
     */
    @RequestMapping(value = "/salaires",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Salaire> getAllSalaires() {
        log.debug("REST request to get all Salaires");
        List<Salaire> salaires = salaireRepository.findAll();
        return salaires;
    }
    
    /*@Scheduled(cron = "0 0 8-10 * * *")
    public void resetSalaire()
    {
        log.info("Reset des salaires actuels");
        List<Salaire> salaires = salaireRepository.findAll();
        for(Salaire sal : salaires)
        {
            sal.setMontantSalaireActuel(sal.getMontantSalaire());
            salaireRepository.save(sal);
        }

    }*/
    
    

    /**
     * GET  /salaires/:id : get the "id" salaire.
     *
     * @param id the id of the salaire to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the salaire, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/salaires/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Salaire> getSalaire(@PathVariable Long id) {
        log.debug("REST request to get Salaire : {}", id);
        Salaire salaire = salaireRepository.findOne(id);
        return Optional.ofNullable(salaire)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /salaires/:id : delete the "id" salaire.
     *
     * @param id the id of the salaire to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/salaires/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSalaire(@PathVariable Long id) {
        log.debug("REST request to delete Salaire : {}", id);
        salaireRepository.delete(id);
        salaireSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("salaire", id.toString())).build();
    }

    /**
     * SEARCH  /_search/salaires?query=:query : search for the salaire corresponding
     * to the query.
     *
     * @param query the query of the salaire search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/salaires",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Salaire> searchSalaires(@RequestParam String query) {
        log.debug("REST request to search Salaires for query {}", query);
        return StreamSupport
            .stream(salaireSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
