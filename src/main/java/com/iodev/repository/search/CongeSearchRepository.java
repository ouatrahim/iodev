package com.iodev.repository.search;

import com.iodev.domain.Conge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Conge entity.
 */
public interface CongeSearchRepository extends ElasticsearchRepository<Conge, Long> {
}
