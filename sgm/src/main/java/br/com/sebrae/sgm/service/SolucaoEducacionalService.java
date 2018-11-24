package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.SolucaoEducacional;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class SolucaoEducacionalService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(SolucaoEducacional se) throws ValidateException {
		ValidateUtils.validateThrows(se);
		if (se.getId() == null) {
			em.persist(se);
		} else {
			em.merge(se);
		}
	}

	public List<SolucaoEducacional> findAll() {
		List<SolucaoEducacional> retorno = (List<SolucaoEducacional>) this.em.createNamedQuery(
				"SolucaoEducacional.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(SolucaoEducacional solucaoEducacional) throws ValidateException {
		em.remove(solucaoEducacional);
	}

	public List<SolucaoEducacional> findByFormaAquisicao(FormaAquisicao fa) {
		return this.em
				.createQuery(
						"SELECT se FROM SolucaoEducacional se where se.formaAquisicao = :formaAquisicao and se.status = 'A' order by se.nome asc")
				.setParameter("formaAquisicao", fa).getResultList();
	}

}
