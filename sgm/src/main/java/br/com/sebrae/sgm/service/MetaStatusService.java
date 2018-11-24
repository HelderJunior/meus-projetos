package br.com.sebrae.sgm.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.MetaStatus;

@Named
public class MetaStatusService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public MetaStatus load(Integer id) {
		return this.em.find(MetaStatus.class, id);
	}

}
