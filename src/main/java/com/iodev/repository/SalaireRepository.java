package com.iodev.repository;

import com.iodev.domain.Salaire;
import com.iodev.domain.User;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Salaire entity.
 */
@SuppressWarnings("unused")
public interface SalaireRepository extends JpaRepository<Salaire,Long> {

    public Salaire findOneByUser(User user);

}
