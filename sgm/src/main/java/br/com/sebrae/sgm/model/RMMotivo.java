/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sebrae.sgm.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "RM_Motivo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RMMotivo.findAll", query = "SELECT r FROM RMMotivo r"),
    @NamedQuery(name = "RMMotivo.findByMotivoUF", query = "SELECT r FROM RMMotivo r WHERE r.rMMotivoPK.motivoUF = :motivoUF"),
    @NamedQuery(name = "RMMotivo.findByMotivoCodigo", query = "SELECT r FROM RMMotivo r WHERE r.rMMotivoPK.motivoCodigo = :motivoCodigo"),
    @NamedQuery(name = "RMMotivo.findByMotivoDescricao", query = "SELECT r FROM RMMotivo r WHERE r.motivoDescricao = :motivoDescricao"),
    @NamedQuery(name = "RMMotivo.findByMotivoSituacao", query = "SELECT r FROM RMMotivo r WHERE r.motivoSituacao = :motivoSituacao")})
public class RMMotivo implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RMMotivoPK rMMotivoPK;
    @Basic(optional = false)
    @Column(name = "MotivoDescricao")
    private String motivoDescricao;
    @Basic(optional = false)
    @Column(name = "MotivoSituacao")
    private Character motivoSituacao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rMMotivo", fetch = FetchType.LAZY)
    private List<RMLogLicenca> rMLogLicencaList;

    public RMMotivo() {
    }

    public RMMotivo(RMMotivoPK rMMotivoPK) {
        this.rMMotivoPK = rMMotivoPK;
    }

    public RMMotivo(RMMotivoPK rMMotivoPK, String motivoDescricao, Character motivoSituacao) {
        this.rMMotivoPK = rMMotivoPK;
        this.motivoDescricao = motivoDescricao;
        this.motivoSituacao = motivoSituacao;
    }

    public RMMotivo(String motivoUF, Character motivoCodigo) {
        this.rMMotivoPK = new RMMotivoPK(motivoUF, motivoCodigo);
    }

    public RMMotivoPK getRMMotivoPK() {
        return rMMotivoPK;
    }

    public void setRMMotivoPK(RMMotivoPK rMMotivoPK) {
        this.rMMotivoPK = rMMotivoPK;
    }

    public String getMotivoDescricao() {
        return motivoDescricao;
    }

    public void setMotivoDescricao(String motivoDescricao) {
        this.motivoDescricao = motivoDescricao;
    }

    public Character getMotivoSituacao() {
        return motivoSituacao;
    }

    public void setMotivoSituacao(Character motivoSituacao) {
        this.motivoSituacao = motivoSituacao;
    }

    @XmlTransient
    public List<RMLogLicenca> getRMLogLicencaList() {
        return rMLogLicencaList;
    }

    public void setRMLogLicencaList(List<RMLogLicenca> rMLogLicencaList) {
        this.rMLogLicencaList = rMLogLicencaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rMMotivoPK != null ? rMMotivoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMMotivo)) {
            return false;
        }
        RMMotivo other = (RMMotivo) object;
        if ((this.rMMotivoPK == null && other.rMMotivoPK != null) || (this.rMMotivoPK != null && !this.rMMotivoPK.equals(other.rMMotivoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMMotivo[ rMMotivoPK=" + rMMotivoPK + " ]";
    }
    
}
