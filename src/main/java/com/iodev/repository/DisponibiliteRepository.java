package com.iodev.repository;

import com.iodev.domain.Disponibilite;
import com.iodev.domain.User;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Disponibilite entity.
 */
@SuppressWarnings("unused")
public interface DisponibiliteRepository extends JpaRepository<Disponibilite,Long> {

    public Disponibilite findOneByUser(User user);

}
