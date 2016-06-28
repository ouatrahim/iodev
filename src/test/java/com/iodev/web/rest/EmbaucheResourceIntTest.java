package com.iodev.web.rest;

import com.iodev.IodevApp;
import com.iodev.domain.Embauche;
import com.iodev.repository.EmbaucheRepository;
import com.iodev.repository.search.EmbaucheSearchRepository;

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
 * Test class for the EmbaucheResource REST controller.
 *
 * @see EmbaucheResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IodevApp.class)
@WebAppConfiguration
@IntegrationTest
public class EmbaucheResourceIntTest {


    private static final LocalDate DEFAULT_DATE_EMBAUCHE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_EMBAUCHE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private EmbaucheRepository embaucheRepository;

    @Inject
    private EmbaucheSearchRepository embaucheSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEmbaucheMockMvc;

    private Embauche embauche;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmbaucheResource embaucheResource = new EmbaucheResource();
        ReflectionTestUtils.setField(embaucheResource, "embaucheSearchRepository", embaucheSearchRepository);
        ReflectionTestUtils.setField(embaucheResource, "embaucheRepository", embaucheRepository);
        this.restEmbaucheMockMvc = MockMvcBuilders.standaloneSetup(embaucheResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        embaucheSearchRepository.deleteAll();
        embauche = new Embauche();
        embauche.setDateEmbauche(DEFAULT_DATE_EMBAUCHE);
    }

    @Test
    @Transactional
    public void createEmbauche() throws Exception {
        int databaseSizeBeforeCreate = embaucheRepository.findAll().size();

        // Create the Embauche

        restEmbaucheMockMvc.perform(post("/api/embauches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(embauche)))
                .andExpect(status().isCreated());

        // Validate the Embauche in the database
        List<Embauche> embauches = embaucheRepository.findAll();
        assertThat(embauches).hasSize(databaseSizeBeforeCreate + 1);
        Embauche testEmbauche = embauches.get(embauches.size() - 1);
        assertThat(testEmbauche.getDateEmbauche()).isEqualTo(DEFAULT_DATE_EMBAUCHE);

        // Validate the Embauche in ElasticSearch
        Embauche embaucheEs = embaucheSearchRepository.findOne(testEmbauche.getId());
        assertThat(embaucheEs).isEqualToComparingFieldByField(testEmbauche);
    }

    @Test
    @Transactional
    public void checkDateEmbaucheIsRequired() throws Exception {
        int databaseSizeBeforeTest = embaucheRepository.findAll().size();
        // set the field null
        embauche.setDateEmbauche(null);

        // Create the Embauche, which fails.

        restEmbaucheMockMvc.perform(post("/api/embauches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(embauche)))
                .andExpect(status().isBadRequest());

        List<Embauche> embauches = embaucheRepository.findAll();
        assertThat(embauches).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmbauches() throws Exception {
        // Initialize the database
        embaucheRepository.saveAndFlush(embauche);

        // Get all the embauches
        restEmbaucheMockMvc.perform(get("/api/embauches?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(embauche.getId().intValue())))
                .andExpect(jsonPath("$.[*].dateEmbauche").value(hasItem(DEFAULT_DATE_EMBAUCHE.toString())));
    }

    @Test
    @Transactional
    public void getEmbauche() throws Exception {
        // Initialize the database
        embaucheRepository.saveAndFlush(embauche);

        // Get the embauche
        restEmbaucheMockMvc.perform(get("/api/embauches/{id}", embauche.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(embauche.getId().intValue()))
            .andExpect(jsonPath("$.dateEmbauche").value(DEFAULT_DATE_EMBAUCHE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmbauche() throws Exception {
        // Get the embauche
        restEmbaucheMockMvc.perform(get("/api/embauches/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmbauche() throws Exception {
        // Initialize the database
        embaucheRepository.saveAndFlush(embauche);
        embaucheSearchRepository.save(embauche);
        int databaseSizeBeforeUpdate = embaucheRepository.findAll().size();

        // Update the embauche
        Embauche updatedEmbauche = new Embauche();
        updatedEmbauche.setId(embauche.getId());
        updatedEmbauche.setDateEmbauche(UPDATED_DATE_EMBAUCHE);

        restEmbaucheMockMvc.perform(put("/api/embauches")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEmbauche)))
                .andExpect(status().isOk());

        // Validate the Embauche in the database
        List<Embauche> embauches = embaucheRepository.findAll();
        assertThat(embauches).hasSize(databaseSizeBeforeUpdate);
        Embauche testEmbauche = embauches.get(embauches.size() - 1);
        assertThat(testEmbauche.getDateEmbauche()).isEqualTo(UPDATED_DATE_EMBAUCHE);

        // Validate the Embauche in ElasticSearch
        Embauche embaucheEs = embaucheSearchRepository.findOne(testEmbauche.getId());
        assertThat(embaucheEs).isEqualToComparingFieldByField(testEmbauche);
    }

    @Test
    @Transactional
    public void deleteEmbauche() throws Exception {
        // Initialize the database
        embaucheRepository.saveAndFlush(embauche);
        embaucheSearchRepository.save(embauche);
        int databaseSizeBeforeDelete = embaucheRepository.findAll().size();

        // Get the embauche
        restEmbaucheMockMvc.perform(delete("/api/embauches/{id}", embauche.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean embaucheExistsInEs = embaucheSearchRepository.exists(embauche.getId());
        assertThat(embaucheExistsInEs).isFalse();

        // Validate the database is empty
        List<Embauche> embauches = embaucheRepository.findAll();
        assertThat(embauches).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEmbauche() throws Exception {
        // Initialize the database
        embaucheRepository.saveAndFlush(embauche);
        embaucheSearchRepository.save(embauche);

        // Search the embauche
        restEmbaucheMockMvc.perform(get("/api/_search/embauches?query=id:" + embauche.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(embauche.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateEmbauche").value(hasItem(DEFAULT_DATE_EMBAUCHE.toString())));
    }
}
