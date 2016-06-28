package com.iodev.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.iodev.domain.Conge;
import com.iodev.domain.Disponibilite;
import com.iodev.domain.Salaire;
import com.iodev.repository.CongeRepository;
import com.iodev.repository.DisponibiliteRepository;
import com.iodev.repository.UserRepository;
import com.iodev.repository.search.CongeSearchRepository;
import com.iodev.repository.SalaireRepository;
import com.iodev.security.AuthoritiesConstants;
import com.iodev.security.SecurityUtils;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Conge.
 */
@RestController
@RequestMapping("/api")
public class CongeResource {

    private final Logger log = LoggerFactory.getLogger(CongeResource.class);
        
    @Inject
    private CongeRepository congeRepository;
    
    @Inject
    private CongeSearchRepository congeSearchRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private DisponibiliteRepository disponibiliteRepository;
    
    @Inject
    private SalaireRepository salaireRepository;
    
    /**
     * POST  /conges : Create a new conge.
     *
     * @param conge the conge to create
     * @return the ResponseEntity with status 201 (Created) and with body the new conge, or with status 400 (Bad Request) if the conge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> createConge(@Valid @RequestBody Conge conge) throws URISyntaxException {
        log.debug("REST request to save Conge : {}", conge);
        Date now = new Date();
        ZonedDateTime t = now.toInstant().atZone(ZoneId.systemDefault());
        if (conge.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("conge", "idexists", "A new conge cannot already have an ID")).body(null);
        }
        log.debug("No user passed in, using current user : {}",SecurityUtils.getCurrentUserLogin());
        conge.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        log.debug("using the local date for date_demande and derniere modification");
        conge.setDateDemande(t);
        conge.setDerniereModification(t);
        Conge result = congeRepository.save(conge);
        congeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("conge", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /conges : Updates an existing conge.
     *
     * @param conge the conge to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated conge,
     * or with status 400 (Bad Request) if the conge is not valid,
     * or with status 500 (Internal Server Error) if the conge couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> updateConge(@Valid @RequestBody Conge conge) throws URISyntaxException {
        log.debug("REST request to update Conge : {}", conge);
        Date now = new Date();
        ZonedDateTime t = now.toInstant().atZone(ZoneId.systemDefault());
        Duration.between(t, t).getSeconds();
        if (conge.getId() == null) {
            return createConge(conge);
        }
        if (conge.getType().equals("Congé Payé"))
        {
                if (conge.isValDG()&&conge.isValRH())
                {
                    long days = differenceDate(conge.getDateDebut(), conge.getDateFin());
                    Disponibilite dispo = disponibiliteRepository.findOneByUser(conge.getUser());
                    if(dispo.getCongeDispo()<= days)
                    {
                        Conge test = null;
                        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("disponibilite", " nombre de conge insuffisant", "Cet employé n'a pas assez de jours de congé")).body(test);

                    }
                    dispo.setCongeDispo(dispo.getCongeDispo()- days);
                    disponibiliteRepository.save(dispo);
                }
        }
        else if (conge.getType().equals("Congé Non Payé"))
        {
            if (conge.isValDG()&&conge.isValRH())
            {
                long days = differenceDate(conge.getDateDebut(), conge.getDateFin());
                Salaire sal = salaireRepository.findOneByUser(conge.getUser());
                if ((days*(sal.getMontantSalaire()/22))>sal.getMontantSalaireActuel())
                {
                   return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("disponibilite", " Salaire actuel insuffisant", "Cet employé n'a pas assez un salaire suffisant pour ce conge non payé")).body(null);

                }
                sal.setMontantSalaireActuel(sal.getMontantSalaireActuel()-(days*(sal.getMontantSalaire()/22)));
                salaireRepository.save(sal);
                
            }
            
        }
        /*else if (conge.getType().equals("Congé Exceptionnel"))
        {
            
        }*/
        conge.setDerniereModification(t);
        Conge result = congeRepository.save(conge);
        congeSearchRepository.save(result);
        //return result;
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("conge", conge.getId().toString())).body(result);
    }
    
    public long differenceDate(ZonedDateTime fromDateTime, ZonedDateTime toDateTime)
    {
       

        ZonedDateTime tempDateTime = ZonedDateTime.from(fromDateTime);

        long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears( years );

        long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths( months );

        long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays( days );


        long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours( hours );

        long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes( minutes );

        long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS);

        
        return days;
    }

    /**
     * GET  /conges : get all the conges.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of conges in body
     */
    @RequestMapping(value = "/conges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Conge> getAllConges() {
        log.debug("REST request to get all Conges");
        List<Conge> conges;
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)|SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.DG))
        {
            conges = congeRepository.findAll();
            
        }
        else
        {
            conges = congeRepository.findByUserIsCurrentUser();
        }
        
        return conges;
    }

    /**
     * GET  /conges/:id : get the "id" conge.
     *
     * @param id the id of the conge to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the conge, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/conges/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conge> getConge(@PathVariable Long id) {
        log.debug("REST request to get Conge : {}", id);
        Conge conge = congeRepository.findOne(id);
        return Optional.ofNullable(conge)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /conges/:id : delete the "id" conge.
     *
     * @param id the id of the conge to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/conges/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConge(@PathVariable Long id) {
        log.debug("REST request to delete Conge : {}", id);
        congeRepository.delete(id);
        congeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("conge", id.toString())).build();
    }

    /**
     * SEARCH  /_search/conges?query=:query : search for the conge corresponding
     * to the query.
     *
     * @param query the query of the conge search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/conges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Conge> searchConges(@RequestParam String query) {
        log.debug("REST request to search Conges for query {}", query);
        return StreamSupport
            .stream(congeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
