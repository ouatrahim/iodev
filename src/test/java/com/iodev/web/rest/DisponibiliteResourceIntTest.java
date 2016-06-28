package com.iodev.web.rest;

import com.iodev.IodevApp;
import com.iodev.domain.Disponibilite;
import com.iodev.repository.DisponibiliteRepository;
import com.iodev.repository.search.DisponibiliteSearchRepository;

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
 * Test class for the DisponibiliteResource REST controller.
 *
 * @see DisponibiliteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IodevApp.class)
@WebAppConfiguration
@IntegrationTest
public class DisponibiliteResourceIntTest {


    private static final LocalDate DEFAULT_LAST_UPDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_UPDATE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_CONGE_DISPO = 1F;
    private static final Float UPDATED_CONGE_DISPO = 2F;

    @Inject
    private DisponibiliteRepository disponibiliteRepository;

    @Inject
    private DisponibiliteSearchRepository disponibiliteSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDisponibiliteMockMvc;

    private Disponibilite disponibilite;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DisponibiliteResource disponibiliteResource = new DisponibiliteResource();
        ReflectionTestUtils.setField(disponibiliteResource, "disponibiliteSearchRepository", disponibiliteSearchRepository);
        ReflectionTestUtils.setField(disponibiliteResource, "disponibiliteRepository", disponibiliteRepository);
        this.restDisponibiliteMockMvc = MockMvcBuilders.standaloneSetup(disponibiliteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        disponibiliteSearchRepository.deleteAll();
        disponibilite = new Disponibilite();
        disponibilite.setLastUpdate(DEFAULT_LAST_UPDATE);
        disponibilite.setCongeDispo(DEFAULT_CONGE_DISPO);
    }

    @Test
    @Transactional
    public void createDisponibilite() throws Exception {
        int databaseSizeBeforeCreate = disponibiliteRepository.findAll().size();

        // Create the Disponibilite

        restDisponibiliteMockMvc.perform(post("/api/disponibilites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(disponibilite)))
                .andExpect(status().isCreated());

        // Validate the Disponibilite in the database
        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        assertThat(disponibilites).hasSize(databaseSizeBeforeCreate + 1);
        Disponibilite testDisponibilite = disponibilites.get(disponibilites.size() - 1);
        assertThat(testDisponibilite.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
        assertThat(testDisponibilite.getCongeDispo()).isEqualTo(DEFAULT_CONGE_DISPO);

        // Validate the Disponibilite in ElasticSearch
        Disponibilite disponibiliteEs = disponibiliteSearchRepository.findOne(testDisponibilite.getId());
        assertThat(disponibiliteEs).isEqualToComparingFieldByField(testDisponibilite);
    }

    @Test
    @Transactional
    public void checkCongeDispoIsRequired() throws Exception {
        int databaseSizeBeforeTest = disponibiliteRepository.findAll().size();
        // set the field null
        disponibilite.setCongeDispo(null);

        // Create the Disponibilite, which fails.

        restDisponibiliteMockMvc.perform(post("/api/disponibilites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(disponibilite)))
                .andExpect(status().isBadRequest());

        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        assertThat(disponibilites).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDisponibilites() throws Exception {
        // Initialize the database
        disponibiliteRepository.saveAndFlush(disponibilite);

        // Get all the disponibilites
        restDisponibiliteMockMvc.perform(get("/api/disponibilites?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(disponibilite.getId().intValue())))
                .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE.toString())))
                .andExpect(jsonPath("$.[*].congeDispo").value(hasItem(DEFAULT_CONGE_DISPO.doubleValue())));
    }

    @Test
    @Transactional
    public void getDisponibilite() throws Exception {
        // Initialize the database
        disponibiliteRepository.saveAndFlush(disponibilite);

        // Get the disponibilite
        restDisponibiliteMockMvc.perform(get("/api/disponibilites/{id}", disponibilite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(disponibilite.getId().intValue()))
            .andExpect(jsonPath("$.lastUpdate").value(DEFAULT_LAST_UPDATE.toString()))
            .andExpect(jsonPath("$.congeDispo").value(DEFAULT_CONGE_DISPO.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDisponibilite() throws Exception {
        // Get the disponibilite
        restDisponibiliteMockMvc.perform(get("/api/disponibilites/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDisponibilite() throws Exception {
        // Initialize the database
        disponibiliteRepository.saveAndFlush(disponibilite);
        disponibiliteSearchRepository.save(disponibilite);
        int databaseSizeBeforeUpdate = disponibiliteRepository.findAll().size();

        // Update the disponibilite
        Disponibilite updatedDisponibilite = new Disponibilite();
        updatedDisponibilite.setId(disponibilite.getId());
        updatedDisponibilite.setLastUpdate(UPDATED_LAST_UPDATE);
        updatedDisponibilite.setCongeDispo(UPDATED_CONGE_DISPO);

        restDisponibiliteMockMvc.perform(put("/api/disponibilites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDisponibilite)))
                .andExpect(status().isOk());

        // Validate the Disponibilite in the database
        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        assertThat(disponibilites).hasSize(databaseSizeBeforeUpdate);
        Disponibilite testDisponibilite = disponibilites.get(disponibilites.size() - 1);
        assertThat(testDisponibilite.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testDisponibilite.getCongeDispo()).isEqualTo(UPDATED_CONGE_DISPO);

        // Validate the Disponibilite in ElasticSearch
        Disponibilite disponibiliteEs = disponibiliteSearchRepository.findOne(testDisponibilite.getId());
        assertThat(disponibiliteEs).isEqualToComparingFieldByField(testDisponibilite);
    }

    @Test
    @Transactional
    public void deleteDisponibilite() throws Exception {
        // Initialize the database
        disponibiliteRepository.saveAndFlush(disponibilite);
        disponibiliteSearchRepository.save(disponibilite);
        int databaseSizeBeforeDelete = disponibiliteRepository.findAll().size();

        // Get the disponibilite
        restDisponibiliteMockMvc.perform(delete("/api/disponibilites/{id}", disponibilite.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean disponibiliteExistsInEs = disponibiliteSearchRepository.exists(disponibilite.getId());
        assertThat(disponibiliteExistsInEs).isFalse();

        // Validate the database is empty
        List<Disponibilite> disponibilites = disponibiliteRepository.findAll();
        assertThat(disponibilites).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDisponibilite() throws Exception {
        // Initialize the database
        disponibiliteRepository.saveAndFlush(disponibilite);
        disponibiliteSearchRepository.save(disponibilite);

        // Search the disponibilite
        restDisponibiliteMockMvc.perform(get("/api/_search/disponibilites?query=id:" + disponibilite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(disponibilite.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].congeDispo").value(hasItem(DEFAULT_CONGE_DISPO.doubleValue())));
    }
}
