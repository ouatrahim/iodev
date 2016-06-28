package com.iodev.web.rest;

import com.iodev.IodevApp;
import com.iodev.domain.Conge;
import com.iodev.repository.CongeRepository;
import com.iodev.repository.search.CongeSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CongeResource REST controller.
 *
 * @see CongeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IodevApp.class)
@WebAppConfiguration
@IntegrationTest
public class CongeResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_DEMANDE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_DEMANDE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_DEMANDE_STR = dateTimeFormatter.format(DEFAULT_DATE_DEMANDE);

    private static final ZonedDateTime DEFAULT_DATE_DEBUT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_DEBUT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_DEBUT_STR = dateTimeFormatter.format(DEFAULT_DATE_DEBUT);

    private static final ZonedDateTime DEFAULT_DATE_FIN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_FIN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_FIN_STR = dateTimeFormatter.format(DEFAULT_DATE_FIN);

    private static final Boolean DEFAULT_VAL_RH = false;
    private static final Boolean UPDATED_VAL_RH = true;

    private static final Boolean DEFAULT_VAL_DG = false;
    private static final Boolean UPDATED_VAL_DG = true;

    private static final ZonedDateTime DEFAULT_DERNIERE_MODIFICATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DERNIERE_MODIFICATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DERNIERE_MODIFICATION_STR = dateTimeFormatter.format(DEFAULT_DERNIERE_MODIFICATION);

    @Inject
    private CongeRepository congeRepository;

    @Inject
    private CongeSearchRepository congeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCongeMockMvc;

    private Conge conge;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CongeResource congeResource = new CongeResource();
        ReflectionTestUtils.setField(congeResource, "congeSearchRepository", congeSearchRepository);
        ReflectionTestUtils.setField(congeResource, "congeRepository", congeRepository);
        this.restCongeMockMvc = MockMvcBuilders.standaloneSetup(congeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        congeSearchRepository.deleteAll();
        conge = new Conge();
        conge.setLibelle(DEFAULT_LIBELLE);
        conge.setType(DEFAULT_TYPE);
        conge.setDateDemande(DEFAULT_DATE_DEMANDE);
        conge.setDateDebut(DEFAULT_DATE_DEBUT);
        conge.setDateFin(DEFAULT_DATE_FIN);
        conge.setValRH(DEFAULT_VAL_RH);
        conge.setValDG(DEFAULT_VAL_DG);
        conge.setDerniereModification(DEFAULT_DERNIERE_MODIFICATION);
    }

    @Test
    @Transactional
    public void createConge() throws Exception {
        int databaseSizeBeforeCreate = congeRepository.findAll().size();

        // Create the Conge

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isCreated());

        // Validate the Conge in the database
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeCreate + 1);
        Conge testConge = conges.get(conges.size() - 1);
        assertThat(testConge.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testConge.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testConge.getDateDemande()).isEqualTo(DEFAULT_DATE_DEMANDE);
        assertThat(testConge.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testConge.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);
        assertThat(testConge.isValRH()).isEqualTo(DEFAULT_VAL_RH);
        assertThat(testConge.isValDG()).isEqualTo(DEFAULT_VAL_DG);
        assertThat(testConge.getDerniereModification()).isEqualTo(DEFAULT_DERNIERE_MODIFICATION);

        // Validate the Conge in ElasticSearch
        Conge congeEs = congeSearchRepository.findOne(testConge.getId());
        assertThat(congeEs).isEqualToComparingFieldByField(testConge);
    }

    @Test
    @Transactional
    public void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setLibelle(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setType(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateDemandeIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setDateDemande(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateDebutIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setDateDebut(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateFinIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setDateFin(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValRHIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setValRH(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValDGIsRequired() throws Exception {
        int databaseSizeBeforeTest = congeRepository.findAll().size();
        // set the field null
        conge.setValDG(null);

        // Create the Conge, which fails.

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isBadRequest());

        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllConges() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);

        // Get all the conges
        restCongeMockMvc.perform(get("/api/conges?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId().intValue())))
                .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].dateDemande").value(hasItem(DEFAULT_DATE_DEMANDE_STR)))
                .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT_STR)))
                .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN_STR)))
                .andExpect(jsonPath("$.[*].valRH").value(hasItem(DEFAULT_VAL_RH.booleanValue())))
                .andExpect(jsonPath("$.[*].valDG").value(hasItem(DEFAULT_VAL_DG.booleanValue())))
                .andExpect(jsonPath("$.[*].derniereModification").value(hasItem(DEFAULT_DERNIERE_MODIFICATION_STR)));
    }

    @Test
    @Transactional
    public void getConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);

        // Get the conge
        restCongeMockMvc.perform(get("/api/conges/{id}", conge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(conge.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.dateDemande").value(DEFAULT_DATE_DEMANDE_STR))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT_STR))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN_STR))
            .andExpect(jsonPath("$.valRH").value(DEFAULT_VAL_RH.booleanValue()))
            .andExpect(jsonPath("$.valDG").value(DEFAULT_VAL_DG.booleanValue()))
            .andExpect(jsonPath("$.derniereModification").value(DEFAULT_DERNIERE_MODIFICATION_STR));
    }

    @Test
    @Transactional
    public void getNonExistingConge() throws Exception {
        // Get the conge
        restCongeMockMvc.perform(get("/api/conges/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);
        int databaseSizeBeforeUpdate = congeRepository.findAll().size();

        // Update the conge
        Conge updatedConge = new Conge();
        updatedConge.setId(conge.getId());
        updatedConge.setLibelle(UPDATED_LIBELLE);
        updatedConge.setType(UPDATED_TYPE);
        updatedConge.setDateDemande(UPDATED_DATE_DEMANDE);
        updatedConge.setDateDebut(UPDATED_DATE_DEBUT);
        updatedConge.setDateFin(UPDATED_DATE_FIN);
        updatedConge.setValRH(UPDATED_VAL_RH);
        updatedConge.setValDG(UPDATED_VAL_DG);
        updatedConge.setDerniereModification(UPDATED_DERNIERE_MODIFICATION);

        restCongeMockMvc.perform(put("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedConge)))
                .andExpect(status().isOk());

        // Validate the Conge in the database
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeUpdate);
        Conge testConge = conges.get(conges.size() - 1);
        assertThat(testConge.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testConge.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testConge.getDateDemande()).isEqualTo(UPDATED_DATE_DEMANDE);
        assertThat(testConge.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testConge.getDateFin()).isEqualTo(UPDATED_DATE_FIN);
        assertThat(testConge.isValRH()).isEqualTo(UPDATED_VAL_RH);
        assertThat(testConge.isValDG()).isEqualTo(UPDATED_VAL_DG);
        assertThat(testConge.getDerniereModification()).isEqualTo(UPDATED_DERNIERE_MODIFICATION);

        // Validate the Conge in ElasticSearch
        Conge congeEs = congeSearchRepository.findOne(testConge.getId());
        assertThat(congeEs).isEqualToComparingFieldByField(testConge);
    }

    @Test
    @Transactional
    public void deleteConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);
        int databaseSizeBeforeDelete = congeRepository.findAll().size();

        // Get the conge
        restCongeMockMvc.perform(delete("/api/conges/{id}", conge.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean congeExistsInEs = congeSearchRepository.exists(conge.getId());
        assertThat(congeExistsInEs).isFalse();

        // Validate the database is empty
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);

        // Search the conge
        restCongeMockMvc.perform(get("/api/_search/conges?query=id:" + conge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].dateDemande").value(hasItem(DEFAULT_DATE_DEMANDE_STR)))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT_STR)))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN_STR)))
            .andExpect(jsonPath("$.[*].valRH").value(hasItem(DEFAULT_VAL_RH.booleanValue())))
            .andExpect(jsonPath("$.[*].valDG").value(hasItem(DEFAULT_VAL_DG.booleanValue())))
            .andExpect(jsonPath("$.[*].derniereModification").value(hasItem(DEFAULT_DERNIERE_MODIFICATION_STR)));
    }
}
