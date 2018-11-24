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
import br.com.sebrae.sgm.model.ObjetivoEstrategico;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class ObjetivoEstrategicoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(ObjetivoEstrategico obj) throws ValidateException {
		ValidateUtils.validateThrows(obj);
		if (obj.getId() == null) {
			em.persist(obj);
		} else {
			em.merge(obj);
		}
	}

	public List<ObjetivoEstrategico> findAll() {
		List<ObjetivoEstrategico> retorno = (List<ObjetivoEstrategico>) this.em.createNamedQuery("ObjetivoEstrategico.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(ObjetivoEstrategico objetivoEstrategico) throws ValidateException {
		em.remove(objetivoEstrategico);
	}

}
