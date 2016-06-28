package com.iodev.repository.search;

import com.iodev.domain.Embauche;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Embauche entity.
 */
public interface EmbaucheSearchRepository extends ElasticsearchRepository<Embauche, Long> {
}
