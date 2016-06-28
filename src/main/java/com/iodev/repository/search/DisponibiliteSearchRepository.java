package com.iodev.repository.search;

import com.iodev.domain.Disponibilite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Disponibilite entity.
 */
public interface DisponibiliteSearchRepository extends ElasticsearchRepository<Disponibilite, Long> {
}
