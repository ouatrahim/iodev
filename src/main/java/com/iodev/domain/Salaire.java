package com.iodev.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Salaire.
 */
@Entity
@Table(name = "salaire")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "salaire")
public class Salaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "date_attribution", nullable = false)
    private LocalDate dateAttribution;

    @NotNull
    @Column(name = "montant_salaire", nullable = false)
    private Long montantSalaire;

    @NotNull
    @Column(name = "montant_salaire_actuel", nullable = false)
    private Long montantSalaireActuel;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(LocalDate dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public Long getMontantSalaire() {
        return montantSalaire;
    }

    public void setMontantSalaire(Long montantSalaire) {
        this.montantSalaire = montantSalaire;
    }

    public Long getMontantSalaireActuel() {
        return montantSalaireActuel;
    }

    public void setMontantSalaireActuel(Long montantSalaireActuel) {
        this.montantSalaireActuel = montantSalaireActuel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Salaire salaire = (Salaire) o;
        if(salaire.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, salaire.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Salaire{" +
            "id=" + id +
            ", dateAttribution='" + dateAttribution + "'" +
            ", montantSalaire='" + montantSalaire + "'" +
            ", montantSalaireActuel='" + montantSalaireActuel + "'" +
            '}';
    }
}
