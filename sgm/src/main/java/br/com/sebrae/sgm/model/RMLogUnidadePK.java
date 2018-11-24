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
public class RMLogUnidadePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "LogUnidadeDataMudanca")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logUnidadeDataMudanca;
    @Basic(optional = false)
    @Column(name = "ColaboradorColaboradorCpf")
    private String colaboradorColaboradorCpf;
    @Basic(optional = false)
    @Column(name = "ColaboradorColaboradorMatricula")
    private String colaboradorColaboradorMatricula;
    @Basic(optional = false)
    @Column(name = "ColaboradorUnidadeUf")
    private String colaboradorUnidadeUf;
    @Basic(optional = false)
    @Column(name = "UnidadeUnidadeCodUnidade")
    private String unidadeUnidadeCodUnidade;
    @Basic(optional = false)
    @Column(name = "UnidadeUnidadeUf")
    private String unidadeUnidadeUf;

    public RMLogUnidadePK() {
    }

    public RMLogUnidadePK(Date logUnidadeDataMudanca, String colaboradorColaboradorCpf, String colaboradorColaboradorMatricula, String colaboradorUnidadeUf, String unidadeUnidadeCodUnidade, String unidadeUnidadeUf) {
        this.logUnidadeDataMudanca = logUnidadeDataMudanca;
        this.colaboradorColaboradorCpf = colaboradorColaboradorCpf;
        this.colaboradorColaboradorMatricula = colaboradorColaboradorMatricula;
        this.colaboradorUnidadeUf = colaboradorUnidadeUf;
        this.unidadeUnidadeCodUnidade = unidadeUnidadeCodUnidade;
        this.unidadeUnidadeUf = unidadeUnidadeUf;
    }

    public Date getLogUnidadeDataMudanca() {
        return logUnidadeDataMudanca;
    }

    public void setLogUnidadeDataMudanca(Date logUnidadeDataMudanca) {
        this.logUnidadeDataMudanca = logUnidadeDataMudanca;
    }

    public String getColaboradorColaboradorCpf() {
        return colaboradorColaboradorCpf;
    }

    public void setColaboradorColaboradorCpf(String colaboradorColaboradorCpf) {
        this.colaboradorColaboradorCpf = colaboradorColaboradorCpf;
    }

    public String getColaboradorColaboradorMatricula() {
        return colaboradorColaboradorMatricula;
    }

    public void setColaboradorColaboradorMatricula(String colaboradorColaboradorMatricula) {
        this.colaboradorColaboradorMatricula = colaboradorColaboradorMatricula;
    }

    public String getColaboradorUnidadeUf() {
        return colaboradorUnidadeUf;
    }

    public void setColaboradorUnidadeUf(String colaboradorUnidadeUf) {
        this.colaboradorUnidadeUf = colaboradorUnidadeUf;
    }

    public String getUnidadeUnidadeCodUnidade() {
        return unidadeUnidadeCodUnidade;
    }

    public void setUnidadeUnidadeCodUnidade(String unidadeUnidadeCodUnidade) {
        this.unidadeUnidadeCodUnidade = unidadeUnidadeCodUnidade;
    }

    public String getUnidadeUnidadeUf() {
        return unidadeUnidadeUf;
    }

    public void setUnidadeUnidadeUf(String unidadeUnidadeUf) {
        this.unidadeUnidadeUf = unidadeUnidadeUf;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logUnidadeDataMudanca != null ? logUnidadeDataMudanca.hashCode() : 0);
        hash += (colaboradorColaboradorCpf != null ? colaboradorColaboradorCpf.hashCode() : 0);
        hash += (colaboradorColaboradorMatricula != null ? colaboradorColaboradorMatricula.hashCode() : 0);
        hash += (colaboradorUnidadeUf != null ? colaboradorUnidadeUf.hashCode() : 0);
        hash += (unidadeUnidadeCodUnidade != null ? unidadeUnidadeCodUnidade.hashCode() : 0);
        hash += (unidadeUnidadeUf != null ? unidadeUnidadeUf.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMLogUnidadePK)) {
            return false;
        }
        RMLogUnidadePK other = (RMLogUnidadePK) object;
        if ((this.logUnidadeDataMudanca == null && other.logUnidadeDataMudanca != null) || (this.logUnidadeDataMudanca != null && !this.logUnidadeDataMudanca.equals(other.logUnidadeDataMudanca))) {
            return false;
        }
        if ((this.colaboradorColaboradorCpf == null && other.colaboradorColaboradorCpf != null) || (this.colaboradorColaboradorCpf != null && !this.colaboradorColaboradorCpf.equals(other.colaboradorColaboradorCpf))) {
            return false;
        }
        if ((this.colaboradorColaboradorMatricula == null && other.colaboradorColaboradorMatricula != null) || (this.colaboradorColaboradorMatricula != null && !this.colaboradorColaboradorMatricula.equals(other.colaboradorColaboradorMatricula))) {
            return false;
        }
        if ((this.colaboradorUnidadeUf == null && other.colaboradorUnidadeUf != null) || (this.colaboradorUnidadeUf != null && !this.colaboradorUnidadeUf.equals(other.colaboradorUnidadeUf))) {
            return false;
        }
        if ((this.unidadeUnidadeCodUnidade == null && other.unidadeUnidadeCodUnidade != null) || (this.unidadeUnidadeCodUnidade != null && !this.unidadeUnidadeCodUnidade.equals(other.unidadeUnidadeCodUnidade))) {
            return false;
        }
        if ((this.unidadeUnidadeUf == null && other.unidadeUnidadeUf != null) || (this.unidadeUnidadeUf != null && !this.unidadeUnidadeUf.equals(other.unidadeUnidadeUf))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMLogUnidadePK[ logUnidadeDataMudanca=" + logUnidadeDataMudanca + ", colaboradorColaboradorCpf=" + colaboradorColaboradorCpf + ", colaboradorColaboradorMatricula=" + colaboradorColaboradorMatricula + ", colaboradorUnidadeUf=" + colaboradorUnidadeUf + ", unidadeUnidadeCodUnidade=" + unidadeUnidadeCodUnidade + ", unidadeUnidadeUf=" + unidadeUnidadeUf + " ]";
    }
    
}
