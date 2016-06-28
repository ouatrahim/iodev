package com.iodev.web.rest;

import com.iodev.IodevApp;
import com.iodev.domain.Salaire;
import com.iodev.repository.SalaireRepository;
import com.iodev.repository.search.SalaireSearchRepository;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SalaireResource REST controller.
 *
 * @see SalaireResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IodevApp.class)
@WebAppConfiguration
@IntegrationTest
public class SalaireResourceIntTest {


    private static final LocalDate DEFAULT_DATE_ATTRIBUTION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ATTRIBUTION = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_MONTANT_SALAIRE = 1L;
    private static final Long UPDATED_MONTANT_SALAIRE = 2L;

    private static final Long DEFAULT_MONTANT_SALAIRE_ACTUEL = 1L;
    private static final Long UPDATED_MONTANT_SALAIRE_ACTUEL = 2L;

    @Inject
    private SalaireRepository salaireRepository;

    @Inject
    private SalaireSearchRepository salaireSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSalaireMockMvc;

    private Salaire salaire;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SalaireResource salaireResource = new SalaireResource();
        ReflectionTestUtils.setField(salaireResource, "salaireSearchRepository", salaireSearchRepository);
        ReflectionTestUtils.setField(salaireResource, "salaireRepository", salaireRepository);
        this.restSalaireMockMvc = MockMvcBuilders.standaloneSetup(salaireResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        salaireSearchRepository.deleteAll();
        salaire = new Salaire();
        salaire.setDateAttribution(DEFAULT_DATE_ATTRIBUTION);
        salaire.setMontantSalaire(DEFAULT_MONTANT_SALAIRE);
        salaire.setMontantSalaireActuel(DEFAULT_MONTANT_SALAIRE_ACTUEL);
    }

    @Test
    @Transactional
    public void createSalaire() throws Exception {
        int databaseSizeBeforeCreate = salaireRepository.findAll().size();

        // Create the Salaire

        restSalaireMockMvc.perform(post("/api/salaires")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salaire)))
                .andExpect(status().isCreated());

        // Validate the Salaire in the database
        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeCreate + 1);
        Salaire testSalaire = salaires.get(salaires.size() - 1);
        assertThat(testSalaire.getDateAttribution()).isEqualTo(DEFAULT_DATE_ATTRIBUTION);
        assertThat(testSalaire.getMontantSalaire()).isEqualTo(DEFAULT_MONTANT_SALAIRE);
        assertThat(testSalaire.getMontantSalaireActuel()).isEqualTo(DEFAULT_MONTANT_SALAIRE_ACTUEL);

        // Validate the Salaire in ElasticSearch
        Salaire salaireEs = salaireSearchRepository.findOne(testSalaire.getId());
        assertThat(salaireEs).isEqualToComparingFieldByField(testSalaire);
    }

    @Test
    @Transactional
    public void checkDateAttributionIsRequired() throws Exception {
        int databaseSizeBeforeTest = salaireRepository.findAll().size();
        // set the field null
        salaire.setDateAttribution(null);

        // Create the Salaire, which fails.

        restSalaireMockMvc.perform(post("/api/salaires")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salaire)))
                .andExpect(status().isBadRequest());

        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMontantSalaireIsRequired() throws Exception {
        int databaseSizeBeforeTest = salaireRepository.findAll().size();
        // set the field null
        salaire.setMontantSalaire(null);

        // Create the Salaire, which fails.

        restSalaireMockMvc.perform(post("/api/salaires")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salaire)))
                .andExpect(status().isBadRequest());

        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMontantSalaireActuelIsRequired() throws Exception {
        int databaseSizeBeforeTest = salaireRepository.findAll().size();
        // set the field null
        salaire.setMontantSalaireActuel(null);

        // Create the Salaire, which fails.

        restSalaireMockMvc.perform(post("/api/salaires")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(salaire)))
                .andExpect(status().isBadRequest());

        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSalaires() throws Exception {
        // Initialize the database
        salaireRepository.saveAndFlush(salaire);

        // Get all the salaires
        restSalaireMockMvc.perform(get("/api/salaires?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(salaire.getId().intValue())))
                .andExpect(jsonPath("$.[*].dateAttribution").value(hasItem(DEFAULT_DATE_ATTRIBUTION.toString())))
                .andExpect(jsonPath("$.[*].montantSalaire").value(hasItem(DEFAULT_MONTANT_SALAIRE.intValue())))
                .andExpect(jsonPath("$.[*].montantSalaireActuel").value(hasItem(DEFAULT_MONTANT_SALAIRE_ACTUEL.intValue())));
    }

    @Test
    @Transactional
    public void getSalaire() throws Exception {
        // Initialize the database
        salaireRepository.saveAndFlush(salaire);

        // Get the salaire
        restSalaireMockMvc.perform(get("/api/salaires/{id}", salaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(salaire.getId().intValue()))
            .andExpect(jsonPath("$.dateAttribution").value(DEFAULT_DATE_ATTRIBUTION.toString()))
            .andExpect(jsonPath("$.montantSalaire").value(DEFAULT_MONTANT_SALAIRE.intValue()))
            .andExpect(jsonPath("$.montantSalaireActuel").value(DEFAULT_MONTANT_SALAIRE_ACTUEL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSalaire() throws Exception {
        // Get the salaire
        restSalaireMockMvc.perform(get("/api/salaires/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSalaire() throws Exception {
        // Initialize the database
        salaireRepository.saveAndFlush(salaire);
        salaireSearchRepository.save(salaire);
        int databaseSizeBeforeUpdate = salaireRepository.findAll().size();

        // Update the salaire
        Salaire updatedSalaire = new Salaire();
        updatedSalaire.setId(salaire.getId());
        updatedSalaire.setDateAttribution(UPDATED_DATE_ATTRIBUTION);
        updatedSalaire.setMontantSalaire(UPDATED_MONTANT_SALAIRE);
        updatedSalaire.setMontantSalaireActuel(UPDATED_MONTANT_SALAIRE_ACTUEL);

        restSalaireMockMvc.perform(put("/api/salaires")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSalaire)))
                .andExpect(status().isOk());

        // Validate the Salaire in the database
        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeUpdate);
        Salaire testSalaire = salaires.get(salaires.size() - 1);
        assertThat(testSalaire.getDateAttribution()).isEqualTo(UPDATED_DATE_ATTRIBUTION);
        assertThat(testSalaire.getMontantSalaire()).isEqualTo(UPDATED_MONTANT_SALAIRE);
        assertThat(testSalaire.getMontantSalaireActuel()).isEqualTo(UPDATED_MONTANT_SALAIRE_ACTUEL);

        // Validate the Salaire in ElasticSearch
        Salaire salaireEs = salaireSearchRepository.findOne(testSalaire.getId());
        assertThat(salaireEs).isEqualToComparingFieldByField(testSalaire);
    }

    @Test
    @Transactional
    public void deleteSalaire() throws Exception {
        // Initialize the database
        salaireRepository.saveAndFlush(salaire);
        salaireSearchRepository.save(salaire);
        int databaseSizeBeforeDelete = salaireRepository.findAll().size();

        // Get the salaire
        restSalaireMockMvc.perform(delete("/api/salaires/{id}", salaire.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean salaireExistsInEs = salaireSearchRepository.exists(salaire.getId());
        assertThat(salaireExistsInEs).isFalse();

        // Validate the database is empty
        List<Salaire> salaires = salaireRepository.findAll();
        assertThat(salaires).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSalaire() throws Exception {
        // Initialize the database
        salaireRepository.saveAndFlush(salaire);
        salaireSearchRepository.save(salaire);

        // Search the salaire
        restSalaireMockMvc.perform(get("/api/_search/salaires?query=id:" + salaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateAttribution").value(hasItem(DEFAULT_DATE_ATTRIBUTION.toString())))
            .andExpect(jsonPath("$.[*].montantSalaire").value(hasItem(DEFAULT_MONTANT_SALAIRE.intValue())))
            .andExpect(jsonPath("$.[*].montantSalaireActuel").value(hasItem(DEFAULT_MONTANT_SALAIRE_ACTUEL.intValue())));
    }
}
