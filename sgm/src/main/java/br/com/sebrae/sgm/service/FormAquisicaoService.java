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
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class FormAquisicaoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(FormaAquisicao fa) throws ValidateException {
		ValidateUtils.validateThrows(fa);
		if (fa.getId() == null) {
			em.persist(fa);
		} else {
			em.merge(fa);
		}
	}

	public List<FormaAquisicao> findAll() {
		List<FormaAquisicao> retorno = (List<FormaAquisicao>) this.em.createNamedQuery("FormaAquisicao.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(FormaAquisicao formaAquisicao) throws ValidateException {
		em.remove(formaAquisicao);
	}

}
