package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Unidade;

@Named
public class FluxoAprovacaoMetasService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<Unidade> getUnidadesFluxoAprovacao(Integer id) {
		return this.em
				.createQuery("select distinct un from FluxoAprovacaoMetas fam join fam.unidades un where fam.id = :id")
				.setParameter("id", id).getResultList();
	}

}
