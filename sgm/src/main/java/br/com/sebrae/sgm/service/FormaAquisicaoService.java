package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.SolucaoEducacional;
import br.com.sebrae.sgm.model.Validacao;

@Named
public class FormaAquisicaoService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Transactional
	public void save(FormaAquisicao formaAquisicao) {
		em.persist(formaAquisicao);

	}

	@Transactional
	public void remove(FormaAquisicao formaAquisicao) {
		em.remove(formaAquisicao);

	}

	@Transactional
	public void update(FormaAquisicao FormaAquisicao) {
		em.merge(FormaAquisicao);

	}

	public FormaAquisicao getFormaAquisicao(Integer id) {
		FormaAquisicao formaAquisicao = em.find(FormaAquisicao.class, id);
		return formaAquisicao;
	}

	public List<Categoria> listCategoria() {
		List<Categoria> categorias = em.createNamedQuery("Categoria.findAll").getResultList();
		return categorias;
	}

	public List<Validacao> listValidacao() {
		List<Validacao> validacoes = em.createNamedQuery("Validacao.findAll").getResultList();
		return validacoes;
	}

	public List<FormaAquisicao> listFormaAquisicao() {
		List<FormaAquisicao> formaAquisicao = em.createNamedQuery("FormaAquisicao.findAll").getResultList();
		return formaAquisicao;
	}
	
	public List<FormaAquisicao> findAllAtivas() {
		List<FormaAquisicao> formaAquisicao = em.createNamedQuery("FormaAquisicao.findAllAtivas").getResultList();
		return formaAquisicao;
	}

	public List<FormaAquisicao> findByCategoria(Categoria categoria) {
		return this.em.createQuery("Select fa from FormaAquisicao fa where fa.categoria = :categoria and fa.status = 'A' order by fa.nome")
				.setParameter("categoria", categoria).getResultList();
	}
}
