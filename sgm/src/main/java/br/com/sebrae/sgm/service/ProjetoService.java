package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Projeto;
import br.com.sebrae.sgm.model.enums.UF;

@Named
public class ProjetoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<Projeto> findAllByUf(UF uf) {
		List<Projeto> retorno = (List<Projeto>) this.em.createNamedQuery("Projeto.findAll").setParameter("uf", uf).getResultList();
		return retorno;
	}

	public List<Projeto> findByNameLike(String nome, UF uf) {
		List<Projeto> retorno = (List<Projeto>) this.em
				.createQuery("Select p from Projeto p where p.nome like :nome and p.uf = :uf order by p.nome")
				.setParameter("nome", "%" + nome + "%").getResultList();
		return retorno;
	}

	public List<Projeto> findByMeta(Meta m) {
		List<Projeto> retorno = this.em
				.createQuery("Select p from Projeto p join p.metas m where m.id = :idMeta order by p.nome asc")
				.setParameter("idMeta", m.getId()).getResultList();
		return retorno;
	}
}
