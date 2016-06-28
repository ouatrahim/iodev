package com.iodev.repository.search;

import com.iodev.domain.Salaire;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Salaire entity.
 */
public interface SalaireSearchRepository extends ElasticsearchRepository<Salaire, Long> {
}
