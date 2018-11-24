package br.com.sebrae.sgm.producer;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import br.com.sebrae.sgm.listener.EMF;
import br.com.sebrae.sgm.producer.qualifier.Aplicacao;

public class EntityManagerProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	private EntityManager entityManager;

	@Produces
	@SessionScoped
	@Default
	protected EntityManager createEntityManager() {
		if (entityManager == null || !entityManager.isOpen())
			entityManager = EMF.createEntityManager();
		return entityManager;
	}

	@Produces
	@ApplicationScoped
	@Aplicacao
	protected EntityManager createEntityManager2() {
		if (entityManager == null || !entityManager.isOpen())
			entityManager = EMF.createEntityManager();
		return entityManager;
	}

	protected void closeEntityManager(@Disposes @Default EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
		entityManager = null;
	}
	
	protected void closeEntityManager2(@Disposes @Aplicacao EntityManager entityManager) {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
		entityManager = null;
	}
}
