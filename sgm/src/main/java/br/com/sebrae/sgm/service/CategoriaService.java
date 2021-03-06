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
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class CategoriaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void save(Categoria c) throws ValidateException {
		ValidateUtils.validateThrows(c);
		if (c.getId() == null) {
			em.persist(c);
		} else {
			em.merge(c);
		}
	}

	public List<Categoria> findAll() {
		List<Categoria> retorno = (List<Categoria>) this.em.createNamedQuery("Categoria.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(Categoria categoria) throws ValidateException {
		em.remove(categoria);
	}

}
