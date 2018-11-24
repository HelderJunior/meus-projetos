package br.com.sebrae.sgm.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.MarcoCritico;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class MarcoCriticoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(MarcoCritico mc) throws ValidateException {
		ValidateUtils.validateThrows(mc);
		if (mc.getId() == null) {
			em.persist(mc);
		} else {
			em.merge(mc);
		}
	}

	@Transactional
	public void delete(MarcoCritico mc) throws ValidateException {
		em.remove(mc);
	}

}
