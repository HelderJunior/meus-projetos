package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.EspacoOcupacional;
import br.com.sebrae.sgm.model.enums.UF;

@Named
public class EspacoOcupacionalService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public List<EspacoOcupacional> findAll() {
		List<EspacoOcupacional> retorno = (List<EspacoOcupacional>) this.em.createNamedQuery("EspacoOcupacional.findAll").getResultList();
		return retorno;
	}
	
	public List<EspacoOcupacional> buscarPelaUfCicloSelecionado(UF uf) {
		List<EspacoOcupacional> retorno = (List<EspacoOcupacional>) this.em
				.createQuery("SELECT espo FROM EspacoOcupacional espo WHERE espo.espacoOcupacionalPK.uf = :uf and espo.situacao = '1'")
				.setParameter("uf", uf).getResultList();
		return retorno;
	}

	public List<EspacoOcupacional> buscarPelaDescricao(String descricao) {
		List<EspacoOcupacional> retorno = (List<EspacoOcupacional>) this.em
				.createQuery(
						"SELECT e FROM EspacoOcupacional e where e.descricao like :descricao and e.situacao = '1' order by e.descricao asc")
				.setParameter("descricao", "%" + descricao + "%").getResultList();
		return retorno;
	}

}
