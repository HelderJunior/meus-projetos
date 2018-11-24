package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.RMCompetencia;

@Named
public class RMCompetenciaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public List<RMCompetencia> findAll(){
		return this.em.createQuery("Select c from RMCompetencia c where c.competenciaSituacao = 'A' "
				+ " order by c.competenciaDescricao asc").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<RMCompetencia> findAllCompetenciaPorColaborador(RMColaborador colaborador, Integer ano) {
		return this.em
				.createQuery(
						"Select c from RMCompetencia c where c.rmColaborador =:colaborador  and c.competenciaSituacao = 'A' and c.competenciaAno =:ano"
								+ " order by c.competenciaDescricao asc").setParameter("colaborador", colaborador).setParameter("ano", ano).getResultList();
	}

}
