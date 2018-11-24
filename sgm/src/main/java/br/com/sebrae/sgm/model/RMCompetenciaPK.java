/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sebrae.sgm.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Diego
 */
@Embeddable
public class RMCompetenciaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "CompetenciaID")
    private String competenciaID;
    @Basic(optional = false)
    @Column(name = "ColaboradorCPF")
    private String colaboradorCPF;
    @Basic(optional = false)
    @Column(name = "ColaboradorMatricula")
    private String colaboradorMatricula;
    @Basic(optional = false)
    @Column(name = "UnidadeUF")
    private String unidadeUF;

    public RMCompetenciaPK() {
    }

    public RMCompetenciaPK(String competenciaID, String colaboradorCPF, String colaboradorMatricula, String unidadeUF) {
        this.competenciaID = competenciaID;
        this.colaboradorCPF = colaboradorCPF;
        this.colaboradorMatricula = colaboradorMatricula;
        this.unidadeUF = unidadeUF;
    }

    public String getCompetenciaID() {
        return competenciaID;
    }

    public void setCompetenciaID(String competenciaID) {
        this.competenciaID = competenciaID;
    }

    public String getColaboradorCPF() {
        return colaboradorCPF;
    }

    public void setColaboradorCPF(String colaboradorCPF) {
        this.colaboradorCPF = colaboradorCPF;
    }

    public String getColaboradorMatricula() {
        return colaboradorMatricula;
    }

    public void setColaboradorMatricula(String colaboradorMatricula) {
        this.colaboradorMatricula = colaboradorMatricula;
    }

    public String getUnidadeUF() {
        return unidadeUF;
    }

    public void setUnidadeUF(String unidadeUF) {
        this.unidadeUF = unidadeUF;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (competenciaID != null ? competenciaID.hashCode() : 0);
        hash += (colaboradorCPF != null ? colaboradorCPF.hashCode() : 0);
        hash += (colaboradorMatricula != null ? colaboradorMatricula.hashCode() : 0);
        hash += (unidadeUF != null ? unidadeUF.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMCompetenciaPK)) {
            return false;
        }
        RMCompetenciaPK other = (RMCompetenciaPK) object;
        if ((this.competenciaID == null && other.competenciaID != null) || (this.competenciaID != null && !this.competenciaID.equals(other.competenciaID))) {
            return false;
        }
        if ((this.colaboradorCPF == null && other.colaboradorCPF != null) || (this.colaboradorCPF != null && !this.colaboradorCPF.equals(other.colaboradorCPF))) {
            return false;
        }
        if ((this.colaboradorMatricula == null && other.colaboradorMatricula != null) || (this.colaboradorMatricula != null && !this.colaboradorMatricula.equals(other.colaboradorMatricula))) {
            return false;
        }
        if ((this.unidadeUF == null && other.unidadeUF != null) || (this.unidadeUF != null && !this.unidadeUF.equals(other.unidadeUF))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMCompetenciaPK[ competenciaID=" + competenciaID + ", colaboradorCPF=" + colaboradorCPF + ", colaboradorMatricula=" + colaboradorMatricula + ", unidadeUF=" + unidadeUF + " ]";
    }
    
}
