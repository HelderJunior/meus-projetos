package br.com.sebrae.sgm.controller.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Usuario;

public class MetaDesenvolvimentoComiteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;
	private List<Meta> metasComiteDesenvolvimento = new ArrayList<Meta>();

	public MetaDesenvolvimentoComiteDTO() {
	}

	public MetaDesenvolvimentoComiteDTO(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Meta> getMetasComiteDesenvolvimento() {
		return metasComiteDesenvolvimento;
	}

	public void setMetasComiteDesenvolvimento(List<Meta> metasComiteDesenvolvimento) {
		this.metasComiteDesenvolvimento = metasComiteDesenvolvimento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDesenvolvimentoComiteDTO other = (MetaDesenvolvimentoComiteDTO) obj;
		if (getUsuario() == null) {
			if (other.getUsuario() != null)
				return false;
		} else if (!getUsuario().equals(other.getUsuario()))
			return false;
		return true;
	}

}
