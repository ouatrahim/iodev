package com.iodev.repository;

import com.iodev.domain.Conge;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Conge entity.
 */
@SuppressWarnings("unused")
public interface CongeRepository extends JpaRepository<Conge,Long> {

    @Query("select conge from Conge conge where conge.user.login = ?#{principal.username}")
    List<Conge> findByUserIsCurrentUser();

}
