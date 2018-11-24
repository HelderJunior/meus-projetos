package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.ConfiguracaoEmail;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;

@Named
public class ConfiguracaoEmailService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<ConfiguracaoEmail> findAll() {
		List<ConfiguracaoEmail> retorno = (List<ConfiguracaoEmail>) this.em.createNamedQuery(
				"ConfiguracaoEmail.findAll").getResultList();
		return retorno;
	}

	public List<Usuario> getUsuarioConfiguracaoEmail(Integer id) {
		return this.em
				.createQuery("select distinct u from ConfiguracaoEmail cfm join cfm.usuarios u where cfm.id = :id")
				.setParameter("id", id).getResultList();
	}

	public List<Unidade> getUnidadesConfiguracaoEmail(Integer id) {
		return this.em.createQuery("select distinct un from ConfiguracaoEmail cfm join cfm.unidades un where cfm.id = :id")
				.setParameter("id", id).getResultList();
	}

}
