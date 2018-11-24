/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "RM_LogLicenca")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RMLogLicenca.findAll", query = "SELECT r FROM RMLogLicenca r"),
    @NamedQuery(name = "RMLogLicenca.findByLogLicencaUf", query = "SELECT r FROM RMLogLicenca r WHERE r.rMLogLicencaPK.logLicencaUf = :logLicencaUf"),
    @NamedQuery(name = "RMLogLicenca.findByLogLicencaDataInicio", query = "SELECT r FROM RMLogLicenca r WHERE r.rMLogLicencaPK.logLicencaDataInicio = :logLicencaDataInicio"),
    @NamedQuery(name = "RMLogLicenca.findByColaboradorMatricula", query = "SELECT r FROM RMLogLicenca r WHERE r.rMLogLicencaPK.colaboradorMatricula = :colaboradorMatricula"),
    @NamedQuery(name = "RMLogLicenca.findByColaboradorCpf", query = "SELECT r FROM RMLogLicenca r WHERE r.rMLogLicencaPK.colaboradorCpf = :colaboradorCpf"),
    @NamedQuery(name = "RMLogLicenca.findByUnidadeUf", query = "SELECT r FROM RMLogLicenca r WHERE r.rMLogLicencaPK.unidadeUf = :unidadeUf"),
    @NamedQuery(name = "RMLogLicenca.findByLogLicencaDataFinal", query = "SELECT r FROM RMLogLicenca r WHERE r.logLicencaDataFinal = :logLicencaDataFinal"),
    @NamedQuery(name = "RMLogLicenca.findByLogLicencaObservacao", query = "SELECT r FROM RMLogLicenca r WHERE r.logLicencaObservacao = :logLicencaObservacao")})
public class RMLogLicenca implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RMLogLicencaPK rMLogLicencaPK;
    @Column(name = "LogLicencaDataFinal")
    @Temporal(TemporalType.TIMESTAMP)
    private Date logLicencaDataFinal;
    @Column(name = "LogLicencaObservacao")
    private String logLicencaObservacao;
    @JoinColumns({
        @JoinColumn(name = "ColaboradorCpf", referencedColumnName = "ColaboradorCpf", insertable = false, updatable = false),
        @JoinColumn(name = "ColaboradorMatricula", referencedColumnName = "ColaboradorMatricula", insertable = false, updatable = false),
        @JoinColumn(name = "UnidadeUf", referencedColumnName = "UnidadeUf", insertable = false, updatable = false)})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RMColaborador rMColaborador;
    @JoinColumns({
        @JoinColumn(name = "MotivoUF", referencedColumnName = "MotivoUF"),
        @JoinColumn(name = "MotivoCodigo", referencedColumnName = "MotivoCodigo")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RMMotivo rMMotivo;

    public RMLogLicenca() {
    }

    public RMLogLicenca(RMLogLicencaPK rMLogLicencaPK) {
        this.rMLogLicencaPK = rMLogLicencaPK;
    }

    public RMLogLicenca(String logLicencaUf, Date logLicencaDataInicio, String colaboradorMatricula, String colaboradorCpf, String unidadeUf) {
        this.rMLogLicencaPK = new RMLogLicencaPK(logLicencaUf, logLicencaDataInicio, colaboradorMatricula, colaboradorCpf, unidadeUf);
    }

    public RMLogLicencaPK getRMLogLicencaPK() {
        return rMLogLicencaPK;
    }

    public void setRMLogLicencaPK(RMLogLicencaPK rMLogLicencaPK) {
        this.rMLogLicencaPK = rMLogLicencaPK;
    }

    public Date getLogLicencaDataFinal() {
        return logLicencaDataFinal;
    }

    public void setLogLicencaDataFinal(Date logLicencaDataFinal) {
        this.logLicencaDataFinal = logLicencaDataFinal;
    }

    public String getLogLicencaObservacao() {
        return logLicencaObservacao;
    }

    public void setLogLicencaObservacao(String logLicencaObservacao) {
        this.logLicencaObservacao = logLicencaObservacao;
    }

    public RMColaborador getRMColaborador() {
        return rMColaborador;
    }

    public void setRMColaborador(RMColaborador rMColaborador) {
        this.rMColaborador = rMColaborador;
    }

    public RMMotivo getRMMotivo() {
        return rMMotivo;
    }

    public void setRMMotivo(RMMotivo rMMotivo) {
        this.rMMotivo = rMMotivo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rMLogLicencaPK != null ? rMLogLicencaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMLogLicenca)) {
            return false;
        }
        RMLogLicenca other = (RMLogLicenca) object;
        if ((this.rMLogLicencaPK == null && other.rMLogLicencaPK != null) || (this.rMLogLicencaPK != null && !this.rMLogLicencaPK.equals(other.rMLogLicencaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMLogLicenca[ rMLogLicencaPK=" + rMLogLicencaPK + " ]";
    }
    
}
