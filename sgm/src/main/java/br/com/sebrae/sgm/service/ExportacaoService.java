package br.com.sebrae.sgm.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Exportacao;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class ExportacaoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Transactional
	public void save(Exportacao obj) throws ValidateException {
		ValidateUtils.validateThrows(obj);
		if (obj.getId() == null) {
			em.persist(obj);
		} else {
			em.merge(obj);
		}
	}

}
