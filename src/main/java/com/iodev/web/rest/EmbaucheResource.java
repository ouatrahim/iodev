package com.iodev.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.iodev.domain.Embauche;
import com.iodev.repository.EmbaucheRepository;
import com.iodev.repository.search.EmbaucheSearchRepository;
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

/**
 * REST controller for managing Embauche.
 */
@RestController
@RequestMapping("/api")
public class EmbaucheResource {

    private final Logger log = LoggerFactory.getLogger(EmbaucheResource.class);
        
    @Inject
    private EmbaucheRepository embaucheRepository;
    
    @Inject
    private EmbaucheSearchRepository embaucheSearchRepository;
    
    /**
     * POST  /embauches : Create a new embauche.
     *
     * @param embauche the embauche to create
     * @return the ResponseEntity with status 201 (Created) and with body the new embauche, or with status 400 (Bad Request) if the embauche has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/embauches",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Embauche> createEmbauche(@Valid @RequestBody Embauche embauche) throws URISyntaxException {
        log.debug("REST request to save Embauche : {}", embauche);
        if (embauche.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("embauche", "idexists", "A new embauche cannot already have an ID")).body(null);
        }
        Embauche result = embaucheRepository.save(embauche);
        embaucheSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/embauches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("embauche", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /embauches : Updates an existing embauche.
     *
     * @param embauche the embauche to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated embauche,
     * or with status 400 (Bad Request) if the embauche is not valid,
     * or with status 500 (Internal Server Error) if the embauche couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/embauches",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Embauche> updateEmbauche(@Valid @RequestBody Embauche embauche) throws URISyntaxException {
        log.debug("REST request to update Embauche : {}", embauche);
        if (embauche.getId() == null) {
            return createEmbauche(embauche);
        }
        Embauche result = embaucheRepository.save(embauche);
        embaucheSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("embauche", embauche.getId().toString()))
            .body(result);
    }

    /**
     * GET  /embauches : get all the embauches.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of embauches in body
     */
    @RequestMapping(value = "/embauches",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Embauche> getAllEmbauches() {
        log.debug("REST request to get all Embauches");
        List<Embauche> embauches = embaucheRepository.findAll();
        return embauches;
    }

    /**
     * GET  /embauches/:id : get the "id" embauche.
     *
     * @param id the id of the embauche to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the embauche, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/embauches/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Embauche> getEmbauche(@PathVariable Long id) {
        log.debug("REST request to get Embauche : {}", id);
        Embauche embauche = embaucheRepository.findOne(id);
        return Optional.ofNullable(embauche)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /embauches/:id : delete the "id" embauche.
     *
     * @param id the id of the embauche to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/embauches/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEmbauche(@PathVariable Long id) {
        log.debug("REST request to delete Embauche : {}", id);
        embaucheRepository.delete(id);
        embaucheSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("embauche", id.toString())).build();
    }

    /**
     * SEARCH  /_search/embauches?query=:query : search for the embauche corresponding
     * to the query.
     *
     * @param query the query of the embauche search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/embauches",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Embauche> searchEmbauches(@RequestParam String query) {
        log.debug("REST request to search Embauches for query {}", query);
        return StreamSupport
            .stream(embaucheSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
