package br.com.sebrae.sgm.controller.dto;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.TreeNode;

import br.com.sebrae.sgm.model.Unidade;

public class ArvoreUnidadesDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private TreeNode root;
	private List<Unidade> unidades;

	public ArvoreUnidadesDTO() {
	}

	public ArvoreUnidadesDTO(TreeNode root, List<Unidade> unidades) {
		super();
		this.root = root;
		this.unidades = unidades;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public List<Unidade> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public void limparCache() {
		if (this.unidades != null)
			for (Unidade un : this.unidades) {
				un.setDiretorTemp(null);
				un.setGerenteAdjuntoTemp(null);
				un.setGerenteTemp(null);
				un.setAssessorTemp(null);
				un.setAuditorTemp(null);
				un.setAvaliadorExternoTemp(null);
				un.setComiteTemp(null);
				un.setChefeGabineteTemp(null);
			}
	}

}
