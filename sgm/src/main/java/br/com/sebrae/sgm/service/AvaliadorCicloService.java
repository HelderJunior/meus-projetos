package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.sebrae.sgm.model.AvaliadorCiclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.TipoUsuario;

@Named
public class AvaliadorCicloService implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject
	private EntityManager em;

	@Transactional
	public void insert(AvaliadorCiclo up) {
		this.em.persist(up);
	}

	@Transactional
	public void update(AvaliadorCiclo up) {
		this.em.merge(up);
	}

	@Transactional
	public void delete(AvaliadorCiclo up) {
		this.em.remove(up);
	}

	@Transactional
	public void deleteByCicloConfiguracao(CicloConfiguracao cc) {
		this.em.createQuery("delete from AvaliadorCiclo ac where ac.cicloConfiguracao = :cicloConfiguracao")
				.setParameter("cicloConfiguracao", cc).executeUpdate();
	}

	public List<AvaliadorCiclo> findByParameters(CicloConfiguracao cicloConfiguracao, Perfil perfil, Unidade unidade,
			Usuario usuario) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(AvaliadorCiclo.class);

		c.add(Restrictions.eq("cicloConfiguracao", cicloConfiguracao));
		c.add(Restrictions.eq("perfil", perfil));
		c.add(Restrictions.eq("unidade", unidade));
		c.add(Restrictions.eq("usuario", usuario));

		return c.list();
	}

	public List<AvaliadorCiclo> findByParameters(CicloConfiguracao cicloConfiguracao, Perfil perfil, Usuario usuario) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(AvaliadorCiclo.class);

		c.add(Restrictions.eq("cicloConfiguracao", cicloConfiguracao));
		c.add(Restrictions.eq("perfil", perfil));
		c.add(Restrictions.eq("usuario", usuario));

		return c.list();
	}

	public List<AvaliadorCiclo> findAvaliadoresByCiclo(CicloConfiguracao cicloConfiguracao) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(AvaliadorCiclo.class);

		c.add(Restrictions.eq("cicloConfiguracao", cicloConfiguracao));
		c.createCriteria("usuario").add(Restrictions.eq("tipo", TipoUsuario.I));

		return c.list();
	}

	public List<AvaliadorCiclo> findAvaliadoresExternosByCiclo(CicloConfiguracao cicloConfiguracao) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(AvaliadorCiclo.class);

		c.add(Restrictions.eq("cicloConfiguracao", cicloConfiguracao));
		c.createCriteria("usuario").add(Restrictions.eq("tipo", TipoUsuario.E));

		return c.list();
	}

	public List<Unidade> findUnidadesRelacionadas(CicloConfiguracao cicloConfiguracao, Perfil perfil, Usuario usuario) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery(
						"Select av.unidade from AvaliadorCiclo av where av.perfil = :perfil "
								+ " and av.usuario = :usuario and av.cicloConfiguracao = :cicloConfiguracao")
				.setParameter("perfil", perfil).setParameter("usuario", usuario)
				.setParameter("cicloConfiguracao", cicloConfiguracao).getResultList();
		return retorno;
	}

}
