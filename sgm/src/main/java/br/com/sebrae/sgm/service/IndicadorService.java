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
import br.com.sebrae.sgm.model.Indicador;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.utils.ValidateUtils;

@SuppressWarnings("unchecked")
@Named
public class IndicadorService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(Indicador i) throws ValidateException {
		ValidateUtils.validateThrows(i);
		if (i.getId() == null) {
			em.persist(i);
		} else {
			em.merge(i);
		}
	}

	public List<Indicador> findAll() {
		List<Indicador> retorno = (List<Indicador>) this.em.createNamedQuery("Indicador.findAll").getResultList();
		return retorno;
	}
	
	public List<Indicador> findAllAtivos() {
		List<Indicador> retorno = (List<Indicador>) this.em.createNamedQuery("Indicador.findAllAtivos").getResultList();
		return retorno;
	}
	
	public List<Indicador> findAllAtivosAndUF(UF uf) {
		List<Indicador> retorno = this.em.createQuery("select i from Indicador i where i.ativo = true and i.uf =:uf order by i.nome")
				.setParameter("uf", uf).getResultList();
		return retorno;
	}
	
	@Transactional
	public void delete(Indicador indicador) throws ValidateException {
		em.remove(indicador);
	} 
}
