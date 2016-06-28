package com.iodev.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.iodev.domain.Disponibilite;
import com.iodev.domain.Embauche;
import com.iodev.repository.DisponibiliteRepository;
import com.iodev.repository.EmbaucheRepository;
import com.iodev.repository.search.DisponibiliteSearchRepository;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * REST controller for managing Disponibilite.
 */
@RestController
@RequestMapping("/api")
public class DisponibiliteResource {

    private final Logger log = LoggerFactory.getLogger(DisponibiliteResource.class);
    
    @Inject
    private EmbaucheRepository embaucheRepository;
        
    @Inject
    private DisponibiliteRepository disponibiliteRepository;
    
    @Inject
    private DisponibiliteSearchRepository disponibiliteSearchRepository;
    
    /**
     * POST  /disponibilites : Create a new disponibilite.
     *
     * @param disponibilite the disponibilite to create
     * @return the ResponseEntity with status 201 (Created) and with body the new disponibilite, or with status 400 (Bad Request) if the disponibilite has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/disponibilites",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Disponibilite> createDisponibilite(@Valid @RequestBody Disponibilite disponibilite) throws URISyntaxException {
        log.debug("REST request to save Disponibilite : {}", disponibilite);
        if (disponibilite.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("disponibilite", "idexists", "A new disponibilite cannot already have an ID")).body(null);
        }
        Embauche emb = embaucheRepository.findOneByUser(disponibilite.getUser());
        disponibilite.setLastUpdate(emb.getDateEmbauche());
        Disponibilite result = disponibiliteRepository.save(disponibilite);
        disponibiliteSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/disponibilites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("disponibilite", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /disponibilites : Updates an existing disponibilite.
     *
     * @param disponibilite the disponibilite to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated disponibilite,
     * or with status 400 (Bad Request) if the disponibilite is not valid,
     * or with status 500 (Internal Server Error) if the disponibilite couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/disponibilites",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Disponibilite> updateDisponibilite(@Valid @RequestBody Disponibilite disponibilite) throws URISyntaxException {
        log.debug("REST request to update Disponibilite : {}", disponibilite);
        if (disponibilite.getId() == null) {
            return createDisponibilite(disponibilite);
        }
        Disponibilite result = disponibiliteRepository.save(disponibilite);
        disponibiliteSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("disponibilite", disponibilite.getId().toString()))
            .body(result);
    }

    /**
     * GET  /disponibilites : get all the disponibilites.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of disponibilites in body
     */
    @RequestMapping(value = "/disponibilites",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Disponibilite> getAllDisponibilites() {
        log.debug("REST request to get all Disponibilites");
        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        return disponibilites;
    }

    /**
     * GET  /disponibilites/:id : get the "id" disponibilite.
     *
     * @param id the id of the disponibilite to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the disponibilite, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/disponibilites/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Disponibilite> getDisponibilite(@PathVariable Long id) {
        log.debug("REST request to get Disponibilite : {}", id);
        Disponibilite disponibilite = disponibiliteRepository.findOne(id);
        return Optional.ofNullable(disponibilite)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /disponibilites/:id : delete the "id" disponibilite.
     *
     * @param id the id of the disponibilite to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/disponibilites/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        log.debug("REST request to delete Disponibilite : {}", id);
        disponibiliteRepository.delete(id);
        disponibiliteSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("disponibilite", id.toString())).build();
    }
    
    @Scheduled(cron = "0 0 6 * * *")
    public void majDispo()
    {
        log.info("maj Auto des disponibilités");
        List<Disponibilite> disponibilites;
        Embauche embauche;
        Calendar now = Calendar.getInstance();
        disponibilites = disponibiliteRepository.findAll();
        for(Disponibilite dispo : disponibilites)
        {
            embauche = embaucheRepository.findOneByUser(dispo.getUser());
            if((dispo.getLastUpdate().getDayOfMonth()==now.get(Calendar.DAY_OF_MONTH))||(now.get(Calendar.MONTH)>dispo.getLastUpdate().getMonthValue()))
            {
                dispo.setCongeDispo(dispo.getCongeDispo());
                dispo.setLastUpdate(dispo.getLastUpdate().plusMonths(1));
                disponibiliteRepository.save(dispo);
            }
            
        }
    }

    /**
     * SEARCH  /_search/disponibilites?query=:query : search for the disponibilite corresponding
     * to the query.
     *
     * @param query the query of the disponibilite search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/disponibilites",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Disponibilite> searchDisponibilites(@RequestParam String query) {
        log.debug("REST request to search Disponibilites for query {}", query);
        return StreamSupport
            .stream(disponibiliteSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
