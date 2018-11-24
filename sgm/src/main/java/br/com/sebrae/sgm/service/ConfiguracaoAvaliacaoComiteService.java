package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ItemAvaliadoComite;

@Named
public class ConfiguracaoAvaliacaoComiteService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<ItemAvaliadoComite> findItensAvaliacaoComite(Ciclo ciclo) {
		Session session = (Session) em.getDelegate();
		Criteria criteria = session.createCriteria(ItemAvaliadoComite.class, "itemAvaliadoComite");
		criteria.createCriteria("configuracaoComite").createCriteria("cicloConfiguracao").add(Restrictions.eq("ciclo", ciclo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<ItemAvaliadoComite> retorno = criteria.list();
		return retorno;
	}

}
