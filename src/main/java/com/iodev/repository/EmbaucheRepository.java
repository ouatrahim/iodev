package com.iodev.repository;

import com.iodev.domain.Embauche;
import com.iodev.domain.User;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Embauche entity.
 */
@SuppressWarnings("unused")
public interface EmbaucheRepository extends JpaRepository<Embauche,Long> {

    public Embauche findOneByUser(User user);

}
