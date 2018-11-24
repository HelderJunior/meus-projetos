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
import br.com.sebrae.sgm.model.Validacao;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class ValidacaoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(Validacao v) throws ValidateException {
		ValidateUtils.validateThrows(v);
		if (v.getId() == null) {
			em.persist(v);
		} else {
			em.merge(v);
		}
	}

	public List<Validacao> findAll() {
		List<Validacao> retorno = (List<Validacao>) this.em.createNamedQuery("Validacao.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(Validacao validacao) throws ValidateException {
		em.remove(validacao);
	}

}
