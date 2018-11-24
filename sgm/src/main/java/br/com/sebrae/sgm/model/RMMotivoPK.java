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
public class RMMotivoPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "MotivoUF")
    private String motivoUF;
    @Basic(optional = false)
    @Column(name = "MotivoCodigo")
    private Character motivoCodigo;

    public RMMotivoPK() {
    }

    public RMMotivoPK(String motivoUF, Character motivoCodigo) {
        this.motivoUF = motivoUF;
        this.motivoCodigo = motivoCodigo;
    }

    public String getMotivoUF() {
        return motivoUF;
    }

    public void setMotivoUF(String motivoUF) {
        this.motivoUF = motivoUF;
    }

    public Character getMotivoCodigo() {
        return motivoCodigo;
    }

    public void setMotivoCodigo(Character motivoCodigo) {
        this.motivoCodigo = motivoCodigo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (motivoUF != null ? motivoUF.hashCode() : 0);
        hash += (motivoCodigo != null ? motivoCodigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RMMotivoPK)) {
            return false;
        }
        RMMotivoPK other = (RMMotivoPK) object;
        if ((this.motivoUF == null && other.motivoUF != null) || (this.motivoUF != null && !this.motivoUF.equals(other.motivoUF))) {
            return false;
        }
        if ((this.motivoCodigo == null && other.motivoCodigo != null) || (this.motivoCodigo != null && !this.motivoCodigo.equals(other.motivoCodigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.sebrae.sgm.model.RMMotivoPK[ motivoUF=" + motivoUF + ", motivoCodigo=" + motivoCodigo + " ]";
    }
    
}
