package com.iodev.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Conge.
 */
@Entity
@Table(name = "conge")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "conge")
public class Conge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "libelle", length = 50, nullable = false)
    private String libelle;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    /*@NotNull*/
    @Column(name = "date_demande", nullable = false)
    private ZonedDateTime dateDemande;

    @NotNull
    @Column(name = "date_debut", nullable = false)
    private ZonedDateTime dateDebut;

    @NotNull
    @Column(name = "date_fin", nullable = false)
    private ZonedDateTime dateFin;

    @NotNull
    @Column(name = "val_rh", nullable = false)
    private Boolean valRH;

    @NotNull
    @Column(name = "val_dg", nullable = false)
    private Boolean valDG;

    @Column(name = "derniere_modification")
    private ZonedDateTime derniereModification;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(ZonedDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public ZonedDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(ZonedDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public ZonedDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(ZonedDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean isValRH() {
        return valRH;
    }

    public void setValRH(Boolean valRH) {
        this.valRH = valRH;
    }

    public Boolean isValDG() {
        return valDG;
    }

    public void setValDG(Boolean valDG) {
        this.valDG = valDG;
    }

    public ZonedDateTime getDerniereModification() {
        return derniereModification;
    }

    public void setDerniereModification(ZonedDateTime derniereModification) {
        this.derniereModification = derniereModification;
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
        Conge conge = (Conge) o;
        if(conge.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, conge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Conge{" +
            "id=" + id +
            ", libelle='" + libelle + "'" +
            ", type='" + type + "'" +
            ", dateDemande='" + dateDemande + "'" +
            ", dateDebut='" + dateDebut + "'" +
            ", dateFin='" + dateFin + "'" +
            ", valRH='" + valRH + "'" +
            ", valDG='" + valDG + "'" +
            ", derniereModification='" + derniereModification + "'" +
            '}';
    }
}
