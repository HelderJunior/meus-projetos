/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Diego
 */
@Embeddable
public class RMLogLicencaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "LogLicencaUf")
    private String logLicencaUf;
    @Basic(optional = false)
    @Column(name = "LogLicencaDataInicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logLicencaDataInicio;
    @Basic(optional = false)
    @Column(name = "ColaboradorMatricula")
    private String colaboradorMatricula;
    @Basic(optional = false)
    @Column(name = "ColaboradorCpf")
    private String colaboradorCpf;
    @Basic(optional = false)
    @Column(name = "UnidadeUf")
    private String unidadeUf;

    public RMLogLicencaPK() {
    }

    public RMLogLicencaPK(String logLicencaUf, Date logLicencaDataInicio, String colaboradorMatricula, String colaboradorCpf, String unidadeUf) {
        this.logLicencaUf = logLicencaUf;
        this.logLicencaDataInicio = logLicencaDataInicio;
        this.colaboradorMatricula = colaboradorMatricula;
        this.colaboradorCpf = colaboradorCpf;
        this.unidadeUf = unidadeUf;
    }

    public String getLogLicencaUf() {
        return logLicencaUf;
    }

    public void setLogLicencaUf(String logLicencaUf) {
        this.logLicencaUf = logLicencaUf;
    }

    public Date getLogLicencaDataInicio() {
        return logLicencaDataInicio;
    }

    public void setLogLicencaDataInicio(Date logLicencaDataInicio) {
        this.logLicencaDataInicio = logLicencaDataInicio;
    }

    public String getColaboradorMatricula() {
        return colaboradorMatricula;
    }

    public void setColaboradorMatricula(String colaboradorMatricula) {
        this.colaboradorMatricula = colaboradorMatricula;
    }

    public String getColaboradorCpf() {
        return colaboradorCpf;
    }

    public void setColaboradorCpf(String colaboradorCpf) {
        this.colaboradorCpf = colaboradorCpf;
    }

    public String getUnidadeUf() {
        return unidadeUf;
    }

    public void setUnidadeUf(String unidadeUf) {
        this.unidadeUf = unidadeUf;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logLicencaUf != null ? logLicencaUf.hashCode() : 0);
        hash += (logLicencaDataInicio != null ? logLicencaDataInicio.hashCode() : 0);
        hash += (colaboradorMatricula != null ? colaboradorMatricula.hashCode() : 0);
        hash += (colaboradorCpf != null ? colaboradorCpf.hashCode() : 0);
        hash += (unidadeUf != null ? unidadeUf.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMLogLicencaPK)) {
            return false;
        }
        RMLogLicencaPK other = (RMLogLicencaPK) object;
        if ((this.logLicencaUf == null && other.logLicencaUf != null) || (this.logLicencaUf != null && !this.logLicencaUf.equals(other.logLicencaUf))) {
            return false;
        }
        if ((this.logLicencaDataInicio == null && other.logLicencaDataInicio != null) || (this.logLicencaDataInicio != null && !this.logLicencaDataInicio.equals(other.logLicencaDataInicio))) {
            return false;
        }
        if ((this.colaboradorMatricula == null && other.colaboradorMatricula != null) || (this.colaboradorMatricula != null && !this.colaboradorMatricula.equals(other.colaboradorMatricula))) {
            return false;
        }
        if ((this.colaboradorCpf == null && other.colaboradorCpf != null) || (this.colaboradorCpf != null && !this.colaboradorCpf.equals(other.colaboradorCpf))) {
            return false;
        }
        if ((this.unidadeUf == null && other.unidadeUf != null) || (this.unidadeUf != null && !this.unidadeUf.equals(other.unidadeUf))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMLogLicencaPK[ logLicencaUf=" + logLicencaUf + ", logLicencaDataInicio=" + logLicencaDataInicio + ", colaboradorMatricula=" + colaboradorMatricula + ", colaboradorCpf=" + colaboradorCpf + ", unidadeUf=" + unidadeUf + " ]";
    }
    
}
