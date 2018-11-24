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
public class RMLogEspOcupPK implements Serializable {
   
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
    @Column(name = "LogEspOcupDataMudanca")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logEspOcupDataMudanca;
    
	@Basic(optional = false)
    @Column(name = "ColaboradorCpf")
    private String colaboradorCpf;
    
	@Basic(optional = false)
    @Column(name = "ColaboradorMatricula")
    private String colaboradorMatricula;
    
	@Basic(optional = false)
    @Column(name = "UnidadeUf")
    private String unidadeUf;
    
	@Basic(optional = false)
    @Column(name = "EspOcupEspacoOcupacionalCodEspOcup")
    private String espOcupEspacoOcupacionalCodEspOcup;
    
	@Basic(optional = false)
    @Column(name = "EspOcupEspacoOcupacionalUf")
    private String espOcupEspacoOcupacionalUf;

    public RMLogEspOcupPK() {
    }

    public RMLogEspOcupPK(Date logEspOcupDataMudanca, String colaboradorCpf, String colaboradorMatricula, String unidadeUf, String espOcupEspacoOcupacionalCodEspOcup, String espOcupEspacoOcupacionalUf) {
        this.logEspOcupDataMudanca = logEspOcupDataMudanca;
        this.colaboradorCpf = colaboradorCpf;
        this.colaboradorMatricula = colaboradorMatricula;
        this.unidadeUf = unidadeUf;
        this.espOcupEspacoOcupacionalCodEspOcup = espOcupEspacoOcupacionalCodEspOcup;
        this.espOcupEspacoOcupacionalUf = espOcupEspacoOcupacionalUf;
    }

    public Date getLogEspOcupDataMudanca() {
        return logEspOcupDataMudanca;
    }

    public void setLogEspOcupDataMudanca(Date logEspOcupDataMudanca) {
        this.logEspOcupDataMudanca = logEspOcupDataMudanca;
    }

    public String getColaboradorCpf() {
        return colaboradorCpf;
    }

    public void setColaboradorCpf(String colaboradorCpf) {
        this.colaboradorCpf = colaboradorCpf;
    }

    public String getColaboradorMatricula() {
        return colaboradorMatricula;
    }

    public void setColaboradorMatricula(String colaboradorMatricula) {
        this.colaboradorMatricula = colaboradorMatricula;
    }

    public String getUnidadeUf() {
        return unidadeUf;
    }

    public void setUnidadeUf(String unidadeUf) {
        this.unidadeUf = unidadeUf;
    }

    public String getEspOcupEspacoOcupacionalCodEspOcup() {
        return espOcupEspacoOcupacionalCodEspOcup;
    }

    public void setEspOcupEspacoOcupacionalCodEspOcup(String espOcupEspacoOcupacionalCodEspOcup) {
        this.espOcupEspacoOcupacionalCodEspOcup = espOcupEspacoOcupacionalCodEspOcup;
    }

    public String getEspOcupEspacoOcupacionalUf() {
        return espOcupEspacoOcupacionalUf;
    }

    public void setEspOcupEspacoOcupacionalUf(String espOcupEspacoOcupacionalUf) {
        this.espOcupEspacoOcupacionalUf = espOcupEspacoOcupacionalUf;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (logEspOcupDataMudanca != null ? logEspOcupDataMudanca.hashCode() : 0);
        hash += (colaboradorCpf != null ? colaboradorCpf.hashCode() : 0);
        hash += (colaboradorMatricula != null ? colaboradorMatricula.hashCode() : 0);
        hash += (unidadeUf != null ? unidadeUf.hashCode() : 0);
        hash += (espOcupEspacoOcupacionalCodEspOcup != null ? espOcupEspacoOcupacionalCodEspOcup.hashCode() : 0);
        hash += (espOcupEspacoOcupacionalUf != null ? espOcupEspacoOcupacionalUf.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RMLogEspOcupPK)) {
            return false;
        }
        RMLogEspOcupPK other = (RMLogEspOcupPK) object;
        if ((this.logEspOcupDataMudanca == null && other.logEspOcupDataMudanca != null) || (this.logEspOcupDataMudanca != null && !this.logEspOcupDataMudanca.equals(other.logEspOcupDataMudanca))) {
            return false;
        }
        if ((this.colaboradorCpf == null && other.colaboradorCpf != null) || (this.colaboradorCpf != null && !this.colaboradorCpf.equals(other.colaboradorCpf))) {
            return false;
        }
        if ((this.colaboradorMatricula == null && other.colaboradorMatricula != null) || (this.colaboradorMatricula != null && !this.colaboradorMatricula.equals(other.colaboradorMatricula))) {
            return false;
        }
        if ((this.unidadeUf == null && other.unidadeUf != null) || (this.unidadeUf != null && !this.unidadeUf.equals(other.unidadeUf))) {
            return false;
        }
        if ((this.espOcupEspacoOcupacionalCodEspOcup == null && other.espOcupEspacoOcupacionalCodEspOcup != null) || (this.espOcupEspacoOcupacionalCodEspOcup != null && !this.espOcupEspacoOcupacionalCodEspOcup.equals(other.espOcupEspacoOcupacionalCodEspOcup))) {
            return false;
        }
        if ((this.espOcupEspacoOcupacionalUf == null && other.espOcupEspacoOcupacionalUf != null) || (this.espOcupEspacoOcupacionalUf != null && !this.espOcupEspacoOcupacionalUf.equals(other.espOcupEspacoOcupacionalUf))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMLogEspOcupPK[ logEspOcupDataMudanca=" + logEspOcupDataMudanca + ", colaboradorCpf=" + colaboradorCpf + ", colaboradorMatricula=" + colaboradorMatricula + ", unidadeUf=" + unidadeUf + ", espOcupEspacoOcupacionalCodEspOcup=" + espOcupEspacoOcupacionalCodEspOcup + ", espOcupEspacoOcupacionalUf=" + espOcupEspacoOcupacionalUf + " ]";
    }
    
}
