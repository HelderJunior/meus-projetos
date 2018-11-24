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
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class CicloService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public Ciclo load(Integer id) {
		return this.em.find(Ciclo.class, id);
	}

	@Transactional
	public void save(Ciclo p) throws ValidateException {
		ValidateUtils.validateThrows(p);
		if (p.getId() == null) {
			em.persist(p);
		} else {
			em.merge(p);
		}
	}

	public List<Ciclo> findAll() {
		List<Ciclo> retorno = (List<Ciclo>) this.em.createNamedQuery("Ciclo.findAll").getResultList();
		return retorno;
	}

	public List<Ciclo> findAllAndamento() {
		List<Ciclo> retorno = (List<Ciclo>) this.em.createNamedQuery("Ciclo.findAllAndamento").getResultList();
		return retorno;
	}

	public List<Ciclo> findByUf(UF uf) {
		List<Ciclo> retorno = (List<Ciclo>) this.em.createNamedQuery("Ciclo.findByUf").setParameter("uf", uf)
				.getResultList();
		return retorno;
	}

	public List<Ciclo> findByVigenciaUF(Integer ano, UF uf) {
		List<Ciclo> retorno = (List<Ciclo>) this.em.createNamedQuery("Ciclo.findByVigenciaUF")
				.setParameter("vigencia", ano).setParameter("uf", uf).getResultList();
		return retorno;
	}

	public List<Ciclo> findIniciadosByUf(UF uf) {
		List<Ciclo> retorno = (List<Ciclo>) this.em.createNamedQuery("Ciclo.findByUfStatusConfiguracao")
				.setParameter("uf", uf).getResultList();

		return retorno;
	}

	public List<Ciclo> findCiclosAbertos(UF uf) {
		List<Ciclo> retorno = (List<Ciclo>) this.em
				.createQuery("Select c from Ciclo c where c.status = 'I' and c.uf = :uf").setParameter("uf", uf)
				.getResultList();
		return retorno;
	}

	public List<Ciclo> findCiclosFinalizados(UF uf) {
		List<Ciclo> retorno = (List<Ciclo>) this.em
				.createQuery("Select c from Ciclo c where c.status = :status and c.uf = :uf")
				.setParameter("status", StatusCiclo.F).setParameter("uf", uf).getResultList();
		return retorno;
	}

	@Transactional
	public void delete(Ciclo ciclo) throws ValidateException {
		em.remove(ciclo);
	}

}
