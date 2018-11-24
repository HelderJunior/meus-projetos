package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.AppBean;
import br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO;
import br.com.sebrae.sgm.controller.dto.MetaDesenvolvimentoComiteDTO;
import br.com.sebrae.sgm.controller.dto.MetaSelecaoDTO;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Categoria;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.FluxoAprovacaoMetas;
import br.com.sebrae.sgm.model.FormaAquisicao;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.Observacao;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.audit.listener.TipoAlteracaoTheadLocal;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoObservacao;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.events.EnviarEmailAlteracaoMetaEvent;
import br.com.sebrae.sgm.utils.ValidateUtils;

import com.google.common.collect.Lists;

@Named
public class MetaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;

	@Inject
	private MailService mailService;

	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalMetaService;

	@Inject
	private AppBean appBean;

	@Inject
	private Event<EnviarEmailAlteracaoMetaEvent> events;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public Meta load(Integer id) {
		return this.em.find(Meta.class, id);
	}

	@Transactional
	public void save(Meta meta) throws ValidateException {
		ValidateUtils.validateThrows(meta);
		if (meta.getId() == null) {
			em.persist(meta);
		} else {
			em.merge(meta);
		}
	}

	public Long getQtdMetas(Ciclo ciclo, StatusMeta status) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status = :status and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("status", status).setParameter("ciclo", ciclo)
				.getSingleResult();
		return retorno;
	}

	public Long getQtdMetasPendentes(Ciclo ciclo) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status in('SA', 'OS', 'OC', 'OA', 'NA') and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo).getSingleResult();
		return retorno;
	}

	public Long getQtdMetasPendentesColaborador(Ciclo ciclo, Usuario colaborador) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status in('OS','OC','OU') and m.cicloConfiguracao.ciclo = :ciclo and m.colaborador = :colaborador")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getSingleResult();
		return retorno;
	}

	public Long getQtdMetasPendentesGerente(Ciclo ciclo, Usuario colaborador,
			List<String> listaUnidadesDoGerente) {

		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where m.unidade.unidadePK.codigo in (:listaUnidadesDoGerente) and s.status in('PA') and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo)
				//.setParameter("colaborador", colaborador)
				.setParameter("listaUnidadesDoGerente", listaUnidadesDoGerente)
				.getSingleResult();
		return retorno;
	}

	public Long getQtdMetasEquipe(Ciclo ciclo, Unidade unidade, StatusMeta... status) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status in(:status) and m.tipo = 'E' and m.unidade = :unidade and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("status", Arrays.asList(status))
				.setParameter("ciclo", ciclo).setParameter("unidade", unidade)
				.getSingleResult();
		return retorno;
	}

	public Long getQtdMetasIndividual(Ciclo ciclo, Usuario colaborador,
			StatusMeta... status) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status in(:status) and m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo and m.colaborador = :usuario")
				.setParameter("status", Arrays.asList(status))
				.setParameter("ciclo", ciclo)
				.setParameter("usuario", colaborador).getSingleResult();
		return retorno;
	}

	public Long getQtdMetasDesenvolvimento(Ciclo ciclo, Usuario colaborador,
			StatusMeta... status) {
		Long retorno = (Long) this.em
				.createQuery(
						"Select count(m.id) from Meta m join m.status s where s.status in(:status) and m.tipo = 'D' and m.colaborador = :usuario and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("status", Arrays.asList(status))
				.setParameter("ciclo", ciclo)
				.setParameter("usuario", colaborador).getSingleResult();
		return retorno;
	}

	public List<Meta> findAllByCiclo(Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select m from Meta m where m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo).getResultList();
		return retorno;
	}

	public List<Meta> findAllIndividuaisCiclo(Usuario colaborador, Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select m from Meta m where m.colaborador = :colaborador and m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getResultList();
		return retorno;
	}

	public List<Meta> findAllIndividuaisCicloAprovada(Usuario colaborador,
			Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select m from Meta m join m.status s where s.status in('AP') and m.colaborador = :colaborador and m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getResultList();
		return retorno;
	}

	public List<Meta> findAllDesenvolvimentoCiclo(Usuario colaborador,
			Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m where m.colaborador = :colaborador and m.tipo = 'D' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getResultList();
		return retorno;
	}

	public List<Meta> findAllDesenvolvimentoCicloAndStatus(Usuario colaborador, Ciclo ciclo, StatusMeta... status) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m join m.status s where m.colaborador = :colaborador and m.tipo = 'D' and m.cicloConfiguracao.ciclo = :ciclo and s.status in(:status)")
				.setParameter("ciclo", ciclo).setParameter("colaborador", colaborador).setParameter("status", Arrays.asList(status)).getResultList();
		return retorno;
	}

	public List<Meta> findAllIndividuaisCicloMonitoramento(Usuario colaborador,
			Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select m from Meta m join m.status s where s.status in('AP','OC','VC','PV') and m.colaborador = :colaborador and m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getResultList();
		return retorno;
	}

	public List<Meta> findMetasValidacaoComite(Ciclo ciclo, Fase faseCiclo,
			Unidade unidade, UF uf) {

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Meta.class, "meta");
		Criteria critCicloConfiguracao = null;
		// criteria.createAlias("colaborador", "colaborador");
		// criteria.createAlias("unidade", "unidade");

		if (ciclo != null) {
			critCicloConfiguracao = criteria
					.createCriteria("cicloConfiguracao");
			critCicloConfiguracao.add(Restrictions.eq("ciclo", ciclo));
		}

		if (faseCiclo != null) {
			if (critCicloConfiguracao == null) {
				critCicloConfiguracao = criteria
						.createCriteria("cicloConfiguracao");
			}
			Criteria critFaseCiclo = critCicloConfiguracao.createCriteria(
					"fasesCicloConfiguracao").createCriteria("fases");
			critFaseCiclo.add(Restrictions.eq("fase", faseCiclo)).add(
					Restrictions.eq("status", StatusCiclo.I));
		}

		if (unidade != null) {
			criteria.add(Restrictions.eq("unidade", unidade));
		} else {
			if (uf != null) {
				criteria.createCriteria("unidade").add(
						Restrictions.eq("unidadePK.uf", uf));
			}
		}

		criteria.add(Restrictions.ne("tipo", TipoMeta.D));
		criteria.createCriteria("status").add(
				Restrictions.in("status", Lists.newArrayList(StatusMeta.OC)));

		/*criteria.createCriteria("status").add(
				Restrictions.in("status", Lists.newArrayList(StatusMeta.AS,
						StatusMeta.OS, StatusMeta.OC, StatusMeta.OU,
						StatusMeta.PA, StatusMeta.PU, StatusMeta.AP,
						StatusMeta.AC, StatusMeta.AU)));*/

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Meta> metas = criteria.list();

		return metas;

	}

	public List<Meta> findMetasAuditor(Ciclo ciclo, Fase faseCiclo,
			Unidade unidade) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m join m.cicloConfiguracao.fasesCicloConfiguracao fcc join fcc.fases f "
								+ " join m.status s where s.status in('VC') and m.cicloConfiguracao.ciclo = :ciclo and f.fase = :fase and f.status = 'I' and m.unidade = :unidade")
				.setParameter("ciclo", ciclo).setParameter("fase", faseCiclo)
				.setParameter("unidade", unidade).getResultList();
		return retorno;
	}

	public List<Meta> findAllEquipeCiclo(Unidade unidade, Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m left join m.unidadesVinculadas uv where ((uv = :unidade) or (m.unidade = :unidade)) and m.tipo = 'E' "
								+ " and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("unidade", unidade).setParameter("ciclo", ciclo)
				.getResultList();
		return retorno;
	}

	public List<Meta> findAllEquipeCicloStatus(Unidade unidade, Ciclo ciclo,
			StatusMeta... status) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m left join m.unidadesVinculadas uv join m.status s where (uv = :unidade or m.unidade = :unidade) and m.tipo = 'E' "
								+ " and m.cicloConfiguracao.ciclo = :ciclo and s.status in(:status)")
				.setParameter("unidade", unidade).setParameter("ciclo", ciclo)
				.setParameter("status", Arrays.asList(status)).getResultList();
		return retorno;
	}

	public List<Meta> findAllEquipeCicloMonitoramento(Unidade unidade,
			Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m join m.status s where s.status in('AP','OC','VC','PV') and m.unidade = :unidade and m.tipo = 'E' "
								+ " and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("unidade", unidade).setParameter("ciclo", ciclo)
				.getResultList();
		return retorno;
	}

	public List<Meta> findAllIndividualByCiclo(Ciclo ciclo, Usuario colaborador) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select m from Meta m where m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo and m.colaborador =:colaborador")
				.setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador).getResultList();
		return retorno;
	}

	public List<Meta> findAllIndividualCicloStatus(Unidade unidade, Usuario colaborador,
			Ciclo ciclo, StatusMeta... status) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m left join m.unidadesVinculadas uv join m.status s where (uv = :unidade or m.unidade = :unidade) and m.tipo = 'I' and m.colaborador = :colaborador"
								+ " and m.cicloConfiguracao.ciclo = :ciclo and s.status in(:status)")
				.setParameter("unidade", unidade).setParameter("ciclo", ciclo)
				.setParameter("colaborador", colaborador)
				.setParameter("status", Arrays.asList(status)).getResultList();
		return retorno;
	}

	public List<Meta> findAllEquipeByCiclo(Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m where m.tipo = 'E' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo).getResultList();
		return retorno;
	}

	public List<Meta> findAllEquipeByCicloMetaAprovada(Ciclo ciclo) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m join m.status s where s.status in('AP') and m.tipo = 'E' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", ciclo).getResultList();
		return retorno;
	}

	public List<Meta> findByUnidadeIndividualCiclo(Unidade unidade, Ciclo c) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m left join m.unidadesVinculadas uv where uv = :unidade and m.tipo = 'I' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", c).setParameter("unidade", unidade)
				.getResultList();
		return retorno;
	}

	public List<Meta> findByUnidadeEquipeCiclo(Unidade unidade, Ciclo c) {
		List<Meta> retorno = this.em
				.createQuery(
						"Select distinct m from Meta m left join m.unidadesVinculadas uv where uv = :unidade and m.tipo = 'E' and m.cicloConfiguracao.ciclo = :ciclo")
				.setParameter("ciclo", c).setParameter("unidade", unidade)
				.getResultList();
		return retorno;
	}

	public List<Meta> findPendentesColaborador(Unidade unidade,
			Usuario usuario, Ciclo ciclo, StatusMeta status, TipoMeta tipo) {
		List<Meta> retorno = new ArrayList<Meta>();

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Meta.class);

		if (unidade != null) {
			criteria.createCriteria("unidadesVinculadas").add(
					Restrictions.eq("unidadePK", unidade.getUnidadePK()));
		}

		if (usuario != null) {
			criteria.add(Restrictions.eq("colaborador", usuario));
		}

		if (status != null) {
			criteria.createCriteria("status").add(
					Restrictions.eq("status", status));
		} else {
			criteria.createCriteria("status").add(
					Restrictions.in("status", Lists.newArrayList(StatusMeta.PA,
							StatusMeta.OS, StatusMeta.OC, StatusMeta.OA,
							StatusMeta.NA, StatusMeta.PC, StatusMeta.AS,
							StatusMeta.PV, StatusMeta.VC, StatusMeta.LC,
							StatusMeta.AA)));
		}

		if (tipo != null) {
			criteria.add(Restrictions.eq("tipo", tipo));
		}

		criteria.createCriteria("cicloConfiguracao").add(
				Restrictions.eq("ciclo", ciclo));

		retorno = criteria.list();

		return retorno;
	}

	@SuppressWarnings("unchecked")
	public List<Meta> findPendentesColaborador(Unidade unidade,
			Usuario usuarioAutenticado, Usuario usuarioFiltro, Ciclo ciclo,
			StatusMeta status, TipoMeta tipo) {
		List<Meta> retorno = null;
		if (usuarioFiltro == null && unidade == null && status == null
				&& tipo == null) {
			retorno = this.em
					.createQuery(
							"Select m from Meta m join m.status s where s.status in('OS','OC','OU') and m.cicloConfiguracao.ciclo = :ciclo and m.colaborador = :colaborador")
					.setParameter("ciclo", ciclo)
					.setParameter("colaborador", usuarioAutenticado)
					.getResultList();
		} else {
			StringBuilder sql = new StringBuilder(
					"Select m from Meta m join m.status s where s.status in('PA') and  m.cicloConfiguracao.ciclo = :ciclo ");

			if (unidade != null) {
				sql.append(" and m.unidade.unidadePK = :unidade");
			}

			if (status != null) {
				sql.append(" and  s.status in(:statusMeta) ");
			}

			if (tipo != null) {
				sql.append(" and m.tipo = :tipoMeta ");
			}
			//if (usuarioFiltro != null) {
				sql.append(" and m.colaborador = :colaborador ");
			//}

			Query q = this.em.createQuery(sql.toString());

			q.setParameter("ciclo", ciclo);

			if(usuarioFiltro != null){
				q.setParameter("colaborador", usuarioFiltro);
			}else{
				q.setParameter("colaborador", usuarioAutenticado);
			}

			if(status != null){
				q.setParameter("statusMeta", status);
			}

			if(tipo != null){
				q.setParameter("tipoMeta", tipo);
			}

			if(unidade != null){
				q.setParameter("unidade", unidade.getUnidadePK());
			}

			retorno = q.getResultList();

		}

		return retorno;
	}

	public List<Meta> findPendentesGerente(Unidade unidade,
			Usuario usuarioAutenticado, Usuario usuarioFiltro, Ciclo ciclo, StatusMeta status,
			TipoMeta tipo, List<String> listaUnidadesDoGerente) {
		List<Meta> retorno = null;
		if (usuarioFiltro == null && unidade == null && status == null
				&& tipo == null) {
			retorno = this.em
					.createQuery(
							"Select m from Meta m join m.status s where  s.status in('PA') and m.cicloConfiguracao.ciclo = :ciclo and m.unidade.unidadePK.codigo in(:listaUnidadesGerenciaveis))")
					.setParameter("ciclo", ciclo)
					.setParameter("listaUnidadesGerenciaveis", listaUnidadesDoGerente).getResultList();
			//.setParameter("usuarioAutenticado", usuarioAutenticado)
		} else {
			StringBuilder sql = new StringBuilder(
					"Select m from Meta m join m.status s where s.status in('PA') and  m.cicloConfiguracao.ciclo = :ciclo and m.colaborador <> :usuarioAutenticado ");


			if (unidade != null) {
				sql.append(" and m.unidade.unidadePK = :unidade");
			}else{
				sql.append(" and m.unidade.unidadePK.codigo in(:listaUnidadesGerenciaveis)");
			}

			if (status != null) {
				sql.append(" and  s.status in(:statusMeta) ");
			}

			if (tipo != null) {
				sql.append(" and m.tipo = :tipoMeta ");
			}
			if (usuarioFiltro != null) {
				sql.append(" and m.colaborador = :colaborador ");
			}

			Query q = this.em.createQuery(sql.toString());

			q.setParameter("usuarioAutenticado", usuarioAutenticado);
			q.setParameter("ciclo", ciclo);

			if(usuarioFiltro != null){
				q.setParameter("colaborador", usuarioFiltro);
			}

			if(status != null){
				q.setParameter("statusMeta", status);
			}

			if(tipo != null){
				q.setParameter("tipoMeta", tipo);
			}

			if(unidade != null){
				q.setParameter("unidade", unidade.getUnidadePK());
			}else{
				q.setParameter("listaUnidadesGerenciaveis", listaUnidadesDoGerente);
			}

			retorno = q.getResultList();

		}

		return retorno;
	}

	public List<Meta> buscarPeloCicloFaseCicloUnidade(Ciclo ciclo, Fase fase,
			Unidade unidade) {
		List<Meta> retorno = new ArrayList<Meta>();

		String qry = "Select m from Meta m join m.cicloConfiguracao.fasesCicloConfiguracao fcc join fcc.fases f"
				+ " where m.cicloConfiguracao.tipoConfiguracao = 'DESEMP' "
				+ " and m.cicloConfiguracao.ciclo = :ciclo "
				+ " and m.unidade = :unidade "
				+ " and f.fase = :fase and m.cicloConfiguracao.ciclo.status = 'I'";

		retorno = this.em.createQuery(qry).setParameter("ciclo", ciclo)
				.setParameter("unidade", unidade).setParameter("fase", fase)
				.getResultList();

		return retorno;
	}

	@Transactional
	public void delete(Meta meta) throws ValidateException {
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.EXC);

		//Remove todos os vinculos.
		meta.setMetasDesenvolvimento(null);
		meta.setMetasInviduaisEquipeVinculadas(null);
		em.persist(meta);

		em.remove(meta);
	}


	public Long getQtdMetasByUsusuarioAndStatus(Usuario usuario,
			StatusMeta status) {
		String qry2 = "Select count(m.id) from Meta m join m.status s where m.colaborador = :colaborador and s.status = :status";
		return (Long) this.em.createQuery(qry2)
				.setParameter("colaborador", usuario)
				.setParameter("status", status).getSingleResult();
	}

	/*
	 * public List<Meta> getMetasByUsusuarioAndStatus(Usuario usuario,
	 * StatusMeta status, TipoMeta... tiposMeta) { String qry2 =
	 * "Select distinct m from Meta m join m.status s where m.colaborador = :colaborador and s.status = :status and m.tipo in (:tiposMeta)"
	 * ;
	 *
	 * return this.em.createQuery(qry2).setParameter("colaborador",
	 * usuario).setParameter("status", status) .setParameter("tiposMeta",
	 * Arrays.asList(tiposMeta)).getResultList(); }
	 */

	public List<Meta> getMetasByUsusuarioAndStatusAndCiclo(Usuario usuario,
			StatusMeta status, Ciclo ciclo, Unidade unidade,
			TipoMeta... tiposMeta) {
		StringBuilder qry2 = new StringBuilder("Select distinct m from Meta m ");

		if (status != null) {
			qry2.append(" join m.status s ");
		}

		if (unidade != null) {
			qry2.append(" join m.unidadesVinculadas uv ");
		}

		qry2.append(" where 1 = 1 ");

		if (usuario != null) {
			qry2.append(" and m.colaborador = :colaborador ");
		}

		if (status != null) {
			qry2.append(" and s.status = :status ");
		}

		if (ciclo != null) {
			qry2.append(" and m.cicloConfiguracao.ciclo = :ciclo ");
		}

		if (unidade != null) {
			qry2.append(" and uv = :unidade");
		}

		if (tiposMeta != null) {
			qry2.append(" and m.tipo in (:tiposMeta) ");
		}

		Query query = this.em.createQuery(qry2.toString());

		if (usuario != null) {
			query.setParameter("colaborador", usuario);
		}

		if (status != null) {
			query.setParameter("status", status);
		}

		if (ciclo != null) {
			query.setParameter("ciclo", ciclo);
		}

		if (unidade != null) {
			query.setParameter("unidade", unidade);
		}

		if (tiposMeta != null) {
			query.setParameter("tiposMeta", Arrays.asList(tiposMeta));
		}

		return query.getResultList();
	}

	public List<GerenciarMetaDTO> buscarMetasGerenciarDesempenho(Ciclo ciclo,
			Unidade unidade, Usuario usuarioAutenticado, Fase faseCiclo) {

		StringBuilder qry = new StringBuilder();
		qry.append("select distinct new br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO(u, 0) from Usuario u "
				+ " join u.metas m join m.status s left join m.unidadesVinculadas unv ");

		if (faseCiclo != null) {
			qry.append(" join m.cicloConfiguracao.fasesCicloConfiguracao fcc join fcc.fases f ");
		}

		qry.append(" where   ");

		qry.append(" s.status in(:status)  ");

		if (ciclo != null) {
			qry.append(" and m.cicloConfiguracao.ciclo = :ciclo ");
		}

		if (unidade != null) {
			qry.append(" and m.unidade.unidadePK.codigo in (:unidadeCod) ");
		} else {
			qry.append(" and m.unidade.unidadePK.codigo in (:listaUnidadesDoGerente) ");
		}

		if (faseCiclo != null) {
			qry.append(" and f.fase = :fase and f.status = 'I' ");
		}

		qry.append(" and m.tipo <> 'D' ");

		Query q = this.em.createQuery(qry.toString());

		if (ciclo != null) {
			q.setParameter("ciclo", ciclo);
		}

		if (unidade != null) {
			q.setParameter("unidadeCod", unidade.getUnidadePK().getCodigo());
		} else {
			q.setParameter("listaUnidadesDoGerente", usuarioPerfilService
					.findByUnidadesGerenciaveis(usuarioAutenticado));
		}

		q.setParameter(
				"status",
				Arrays.asList(new StatusMeta[] { StatusMeta.SA, StatusMeta.AS,
						StatusMeta.CS, StatusMeta.OC, StatusMeta.OS,
						StatusMeta.PA, StatusMeta.PC }));

		if (faseCiclo != null) {
			q.setParameter("fase", faseCiclo);
		}

		List<GerenciarMetaDTO> retorno = q.getResultList();

		for (GerenciarMetaDTO gmdto : retorno) {
			gmdto.setMetasGravadas(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.SA, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasAprovadas(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.AS, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasCanceladas(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.CS, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasObservacaoComite(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.OC, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasObservacaoSuperior(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.OS, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasPendenteAprovacao(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.PA, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
			gmdto.setMetasPendenteCancelamento(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.PC, ciclo, unidade,
					TipoMeta.I, TipoMeta.E));
		}

		return retorno;
	}

	public List<GerenciarMetaDTO> buscarMetasGerenciarDesenvolvimento(
			Ciclo ciclo, Unidade unidade, String nomeUsuario, Fase faseCiclo) {

		StringBuilder qry = new StringBuilder();
		qry.append("select distinct new br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO(u, 0) from Usuario u "
				+ " join u.metas m join m.status s join u.perfis p ");

		if (faseCiclo != null) {
			qry.append(" join m.cicloConfiguracao.fasesCicloConfiguracao fcc join fcc.fases f ");
		}

		qry.append(" where 1=1 ");

		if (ciclo != null) {
			qry.append(" and m.cicloConfiguracao.ciclo = :ciclo  ");
		}

		if (unidade != null) {
			qry.append(" and m.unidade.unidadePK.codigo in (:unidadeCod) ");
		} else {
			qry.append(" and m.unidade.unidadePK.codigo in (:listaUnidadesDoGerente) ");
		}

		if (unidade != null) {
			qry.append(" and p.unidade = :unidade ");
		}

		qry.append(" and s.status in(:status) ");

		if (faseCiclo != null) {
			qry.append(" and f.fase = :fase and f.status = 'I' ");
		}

		if (!StringUtils.isEmpty(nomeUsuario)) {
			qry.append(" and u.nome like :nome ");
		}

		qry.append(" and m.tipo = 'D'");

		Query q = this.em.createQuery(qry.toString());

		if (ciclo != null) {
			q.setParameter("ciclo", ciclo);
		}

		if (unidade != null) {
			q.setParameter("unidadeCod", unidade.getUnidadePK().getCodigo());
		} else {
			q.setParameter("listaUnidadesDoGerente",
					usuarioPerfilService.findByUnidadesGerenciaveis(appBean
							.getUsuarioAutenticado()));
		}

		if (unidade != null) {
			q.setParameter("unidade", unidade);
		}
		q.setParameter(
				"status",
				Arrays.asList(new StatusMeta[] { StatusMeta.SA, StatusMeta.CS,
						StatusMeta.OC, StatusMeta.OS, StatusMeta.PA,
						StatusMeta.PC }));

		if (!StringUtils.isBlank(nomeUsuario)) {
			q.setParameter("nome", "%" + nomeUsuario + "%");
		}

		if (faseCiclo != null) {
			q.setParameter("fase", faseCiclo);
		}

		List<GerenciarMetaDTO> retorno = q.getResultList();

		for (GerenciarMetaDTO gmdto : retorno) {
			gmdto.setMetasAprovadas(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.AS, ciclo, unidade,
					TipoMeta.D));
			gmdto.setMetasPendenteAprovacao(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.PA, ciclo, unidade,
					TipoMeta.D));
			gmdto.setMetasObservacaoComite(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.OC, ciclo, unidade,
					TipoMeta.D));
			gmdto.setMetasObservacaoSuperior(getMetasByUsusuarioAndStatusAndCiclo(
					gmdto.getUsuario(), StatusMeta.OS, ciclo, unidade,
					TipoMeta.D));
			
			gmdto.setSolucoesAprovadas(solucaoEducacionalMetaService
					.findByUsuarioAndStatusSolucao(gmdto.getUsuario(),
							StatusSolucaoEducacional.V));
			gmdto.setSolucoesPendenteAprovacao(solucaoEducacionalMetaService
					.findByUsuarioAndStatusSolucao(gmdto.getUsuario(),
							StatusSolucaoEducacional.N));
			gmdto.setSolucoesRealizadas(solucaoEducacionalMetaService
					.findByUsuarioAndStatusSolucao(gmdto.getUsuario(),
							StatusSolucaoEducacional.V));
		}

		return retorno;
	}

	public List<MetaSelecaoDTO> findMetasByParameters(Ciclo ciclo,
			Unidade unidade, String nomeUsuario, Fase fase,
			StatusMeta statusMeta, Usuario usuario) {

		StringBuilder qry = new StringBuilder();

		qry.append("select distinct new br.com.sebrae.sgm.controller.dto.MetaSelecaoDTO(m) from Meta m join m.status s left join m.unidadesVinculadas unv ");

		if (fase != null) {
			qry.append("join m.cicloConfiguracao.fasesCicloConfiguracao fcc join fcc.fases f ");
		}

		qry.append(" where 1=1 ");

		if (ciclo != null) {
			qry.append(" and m.cicloConfiguracao.ciclo = :ciclo ");

		}

		if (unidade != null) {
			qry.append(" and m.unidade.unidadePK.codigo in (:unidadeCod) ");
		} else {
			qry.append(" and m.unidade.unidadePK.codigo in (:listaUnidadesDoGerente) ");
		}
		// if (unidade != null) {
		// qry.append(" and unv.unidadePK = :unidadePK ");
		// }

		if (statusMeta != null) {
			qry.append(" and s.status = :status ");
		}

		// if (usuario != null) {
		// qry.append(" and m.colaborador = :usuario ");
		// }

		if (fase != null) {
			qry.append(" and f.fase = :fase and f.status = 'I' ");
		}

		Query q = this.em.createQuery(qry.toString());

		if (ciclo != null) {
			q.setParameter("ciclo", ciclo);
		}

		if (unidade != null) {
			q.setParameter("unidadeCod", unidade.getUnidadePK().getCodigo());
		} else {
			q.setParameter("listaUnidadesDoGerente",
					usuarioPerfilService.findByUnidadesGerenciaveis(usuario));
		}

		// if (unidade != null) {
		// q.setParameter("unidadePK", unidade.getUnidadePK());
		//
		// }

		if (statusMeta != null) {
			q.setParameter("status", statusMeta);
		}

		if (fase != null) {
			q.setParameter("fase", fase);
		}

		// if (usuario != null) {
		// q.setParameter("usuario", usuario);
		// }

		List<MetaSelecaoDTO> retorno = q.getResultList();

		return retorno;

	}

	@Transactional
	public void enviarMetaAprovacao(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {

		validarEnvioAprovacao(meta, tipoConfiguracao, tipoCalendario);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo()
				.enviarAprovacao(meta, tipoConfiguracao, tipoCalendario);

		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"enviada para aprova\u00E7\u00E3o", meta, tipoConfiguracao,
				tipoCalendario);
		events.fire(event);
	}

	private void validarEnvioAprovacao(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		try {
			fluxo.getFluxo()
					.getFluxo()
					.isPodeEnviarAprovacao(meta, tipoConfiguracao,
							tipoCalendario);
		} catch (ValidateException e) {
			msg.addAll(e.getMsgErrors());
		}

		if (!msg.isEmpty()) {

			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void aprovarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao,
			TipoGrupo tipoGrupo) throws ValidateException {
		meta = load(meta.getId());
		validarAprovacaoMeta(meta, tipoConfiguracao, tipoGrupo);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo().aprovar(meta, tipoConfiguracao, tipoGrupo);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"aprovada", meta, tipoConfiguracao, tipoGrupo);
		events.fire(event);
	}

	private void validarAprovacaoMeta(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo()
						.isPodeAprovar(meta, tipoConfiguracao, tipoGrupo);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void reprovarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao,
			TipoGrupo tipoGrupo) throws ValidateException {
		validarReprovacaoMeta(meta, tipoConfiguracao, tipoGrupo);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo().reprovar(meta, tipoConfiguracao, tipoGrupo);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"reprovada", meta, tipoConfiguracao, tipoGrupo);
		events.fire(event);
	}

	private void validarReprovacaoMeta(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo()
						.isPodeReprovar(meta, tipoConfiguracao, tipoGrupo);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void cancelarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao,
			TipoGrupo tipoGrupo) throws ValidateException {
		meta = load(meta.getId());
		validarCancelamentoSuperiorMeta(meta, tipoConfiguracao, tipoGrupo);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo()
				.cancelarSuperior(meta, tipoConfiguracao, tipoGrupo);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"cancelada", meta, tipoConfiguracao, tipoGrupo);
		events.fire(event);
	}

	private void validarCancelamentoSuperiorMeta(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo()
						.getFluxo()
						.isPodeCancelarSuperior(meta, tipoConfiguracao,
								tipoGrupo);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}

	}

	@Transactional
	public void enviarObservacaoSuperiorMeta(Meta meta, String observacao)
			throws ValidateException {
		meta = this.load(meta.getId());
		validarEnvioObservacaoSuperior(meta);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo().enviarObservacaoSuperior(meta, observacao);
		this.em.merge(meta);

	}

	@Transactional
	public void enviarObservacaoColaboradorMeta(Meta meta, String observacao) throws ValidateException {
		meta = this.load(meta.getId());

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao().getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo().enviarObservacaoColaborador(meta, appBean.getUsuarioAutenticado(), observacao);
		this.em.merge(meta);

	}

	private void validarEnvioObservacaoSuperior(Meta meta)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo()
						.isPodeEnviarObservacaoSuperior(meta);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}

	}

	@Transactional
	public void enviarMetaValidacao(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		validarEnvioValidacao(meta, tipoConfiguracao, tipoCalendario);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo()
				.enviarValidacao(meta, tipoConfiguracao, tipoCalendario);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"enviada para valida\u00E7\u00E3o", meta, tipoConfiguracao,
				tipoCalendario);
		events.fire(event);
	}

	private void validarEnvioValidacao(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo()
						.getFluxo()
						.isPodeEnviarValidacao(meta, tipoConfiguracao,
								tipoCalendario);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}

		if (meta.getStatusExecucaoMeta() == null) {
			msg.add("Informe o status da execu\u00E7\u00E3o da meta");
		}

		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void enviarMetaComiteSGPAuditor(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		validarEnvioComiteSGPAuditor(meta, tipoConfiguracao, tipoCalendario);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo()
				.enviarComiteAuditor(meta, tipoConfiguracao, tipoCalendario);
		this.em.merge(meta);

		if (meta.getCicloConfiguracao().getEnviarEmailComPendencia()) {
			EnviarEmailAlteracaoMetaEvent event = new
					EnviarEmailAlteracaoMetaEvent("enviada para comit\u00EA pelo auditor", meta, tipoConfiguracao, tipoCalendario);
			events.fire(event);
		}
	}

	private void validarEnvioComiteSGPAuditor(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo()
						.getFluxo()
						.isPodeEnviarComiteAuditor(meta, tipoConfiguracao,
								tipoCalendario);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}

		if (meta.getStatusExecucaoMeta() == null) {
			msg.add("Informe o status da execu\u00E7\u00E3o da meta");
		}

		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void enviarObservacaoComiteMeta(Meta meta, String observacao)
			throws ValidateException {
		validarEnvioObservacaoComite(meta);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo()
				.getFluxo()
				.enviarObservacaoComite(meta, appBean.getUsuarioAutenticado(),
						observacao);
		this.em.merge(meta);
	}

	@Transactional
	public void gerenteEnviarObservacaoComiteMeta(Meta meta, String observacao) throws ValidateException {

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao().getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo().getenteEnviarObservacaoComite(meta, appBean.getUsuarioAutenticado(), observacao);

		this.em.merge(meta);
	}

	private void validarEnvioObservacaoComite(Meta meta)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo().isPodeEnviarObservacaoComite(meta);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void validarMetaComite(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		validarValidacaoComite(meta, tipoConfiguracao, tipoCalendario);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo()
				.validarComite(meta, tipoConfiguracao, tipoCalendario);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"validada pelo comit\u00EA", meta, tipoConfiguracao,
				tipoCalendario);
		events.fire(event);
	}

	private void validarValidacaoComite(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo()
						.getFluxo()
						.isPodeValidarComite(meta, tipoConfiguracao,
								tipoCalendario);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}

		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void enviarObservacaoAuditor(Meta meta, String observacao)
			throws ValidateException {
		validarEnvioObservacaoAuditor(meta);

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		fluxo.getFluxo().getFluxo().enviarObservacaoAuditor(meta, observacao);
		this.em.merge(meta);

		if (meta.getCicloConfiguracao().getEnviarEmailComPendencia()) {
			EnviarEmailAlteracaoMetaEvent event = new
					EnviarEmailAlteracaoMetaEvent("enviada observa\u00E7\u00E3o para o colaborador pelo auditor", meta, null, null);
			events.fire(event);
		}
	}

	private void validarEnvioObservacaoAuditor(Meta meta)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo().isPodeEnviarObservacaoAuditor(meta);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void enviarObservacaoAuditorGerente(Meta meta, String observacao)
			throws ValidateException {
		validarEnvioObservacaoAuditorGerente(meta);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo()
				.enviarObservacaoAuditorGerente(meta, observacao);
		this.em.merge(meta);
		if (meta.getCicloConfiguracao().getEnviarEmailComPendencia()) {
			EnviarEmailAlteracaoMetaEvent event = new
					EnviarEmailAlteracaoMetaEvent("enviada observa\u00E7\u00E3o para o gerente pelo auditor", meta, null, null);
			events.fire(event);
		}
	}

	private void validarEnvioObservacaoAuditorGerente(Meta meta)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();

		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());

		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo()
						.isPodeEnviarObservacaoAuditorGerente(meta);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void atestarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao,
			TipoGrupo tipoCalendario) throws ValidateException {
		validarAtestacaoMeta(meta, tipoConfiguracao, tipoCalendario);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo()
				.atestarMeta(meta, tipoConfiguracao, tipoCalendario);
		this.em.merge(meta);

		if (meta.getCicloConfiguracao().getEnviarEmailComPendencia()) {
			EnviarEmailAlteracaoMetaEvent event = new
					EnviarEmailAlteracaoMetaEvent("atestada pelo auditor", meta, tipoConfiguracao, tipoCalendario);
			events.fire(event);
		}
	}

	private void validarAtestacaoMeta(Meta meta,
			TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> msg = new ArrayList<String>();
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo()
						.getFluxo()
						.isPodeAtestarMeta(meta, tipoConfiguracao,
								tipoCalendario);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

	@Transactional
	public void enviarResposta(Observacao observacaoSelecionada, Meta meta) {
		if (observacaoSelecionada.getTipo() == TipoObservacao.G || observacaoSelecionada.getTipo() == TipoObservacao.C
				|| observacaoSelecionada.getTipo() == TipoObservacao.U || observacaoSelecionada.getTipo() == TipoObservacao.M) {
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.PA);
		} /*else if (observacaoSelecionada.getTipo() == TipoObservacao.C) {
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.PV);
		} else if (observacaoSelecionada.getTipo() == TipoObservacao.U) {
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.VC);
		}  else if (observacaoSelecionada.getTipo() == TipoObservacao.M) {
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.VC);
		}*/
		observacaoSelecionada.setDataHoraResposta(new Date());
		this.em.merge(meta);
		this.em.merge(observacaoSelecionada);
	}

	public List<MetaDesenvolvimentoComiteDTO> findUsuariosComMetaDesenvolvimentoValidarComite(
			Ciclo ciclo, Unidade unidade, Fase fase, Usuario colaborador,
			Categoria categoria, FormaAquisicao formaAquisicao,
			VinculoOcupacional vinculoOcupacional,
			StatusSolucaoEducacional statusSolucao, UF uf) {

		List<MetaDesenvolvimentoComiteDTO> retorno = new ArrayList<MetaDesenvolvimentoComiteDTO>();

		Session session = (Session) em.getDelegate();

		Criteria criteria = session.createCriteria(Meta.class, "meta");
		Criteria critCicloConfiguracao = null;
		criteria.createAlias("colaborador", "colaborador");

		if (ciclo != null) {
			critCicloConfiguracao = criteria
					.createCriteria("cicloConfiguracao");
			critCicloConfiguracao.add(Restrictions.eq("ciclo", ciclo));
		}

		if (unidade != null) {
			criteria.createAlias("unidade", "unidade");
			criteria.add(Restrictions.eq("unidade", unidade));
		} else {
			if (uf != null) {
				criteria.createCriteria("unidade").add(
						Restrictions.eq("unidadePK.uf", uf));
			}
		}

		if (fase != null) {
			if (critCicloConfiguracao == null) {
				critCicloConfiguracao = criteria
						.createCriteria("cicloConfiguracao");
			}
			Criteria critFaseCiclo = critCicloConfiguracao.createCriteria(
					"fasesCicloConfiguracao").createCriteria("fases");
			critFaseCiclo.add(Restrictions.eq("fase", fase)).add(
					Restrictions.eq("status", StatusCiclo.I));
		}

		if (colaborador != null) {
			criteria.add(Restrictions.eq("colaborador", colaborador));
		}

		Criteria critSolucaoEducacional = null;
		Criteria critSolucaoEducacional2 = null;
		Criteria critFormaAquisicao = null;

		if (categoria != null) {
			critSolucaoEducacional = criteria
					.createCriteria("solucoesEducacionais");
			critSolucaoEducacional2 = critSolucaoEducacional
					.createCriteria("solucaoEducacional");
			critFormaAquisicao = critSolucaoEducacional2
					.createCriteria("formaAquisicao");
			critFormaAquisicao.add(Restrictions.eq("categoria", categoria));
		}

		if (formaAquisicao != null) {
			if (critSolucaoEducacional == null) {
				critSolucaoEducacional = criteria
						.createCriteria("solucoesEducacionais");
			}
			if (critSolucaoEducacional2 == null) {
				critSolucaoEducacional2 = critSolucaoEducacional
						.createCriteria("solucaoEducacional");
			}
			critSolucaoEducacional2.add(Restrictions.eq("formaAquisicao",
					formaAquisicao));
		}

		if (vinculoOcupacional != null) {
			if (critSolucaoEducacional == null) {
				critSolucaoEducacional = criteria
						.createCriteria("solucoesEducacionais");
			}
			critSolucaoEducacional.add(Restrictions.eq("vinculoOcupacional",
					vinculoOcupacional));
		}

		if (statusSolucao != null) {
			if (critSolucaoEducacional == null) {
				critSolucaoEducacional = criteria
						.createCriteria("solucoesEducacionais");
			}
			critSolucaoEducacional
					.add(Restrictions.eq("status", statusSolucao));
		}

		criteria.add(Restrictions.eq("tipo", TipoMeta.D));

		criteria.createCriteria("status").add(
				Restrictions.eq("status", StatusMeta.OC));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Meta> metas = criteria.list();

		if (metas != null && !metas.isEmpty()) {
			for (Meta meta : metas) {
				MetaDesenvolvimentoComiteDTO mdto = new MetaDesenvolvimentoComiteDTO(
						meta.getColaborador());
				if (!retorno.contains(mdto)) {
					mdto.getMetasComiteDesenvolvimento().add(meta);
					retorno.add(mdto);
				} else {
					retorno.get(retorno.indexOf(mdto))
							.getMetasComiteDesenvolvimento().add(meta);
				}
			}
		}
		return retorno;
	}

	@Transactional
	public void enviarMetaParaComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) 
			throws ValidateException {
		
		//validarEnviarMetaComite(meta, tipoConfiguracao, tipoGrupo);
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		fluxo.getFluxo().getFluxo().enviarComite(meta, tipoConfiguracao, tipoGrupo);
		this.em.merge(meta);

		EnviarEmailAlteracaoMetaEvent event = new EnviarEmailAlteracaoMetaEvent(
				"Meta enviada para o Comite", meta, tipoConfiguracao, tipoGrupo);
		events.fire(event);
		
	}

	private void validarEnviarMetaComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoGrupo) throws ValidateException {
		List<String> msg = new ArrayList<String>();
		FluxoAprovacaoMetas fluxo = meta.getCicloConfiguracao()
				.getFluxoByUnidade(meta.getUnidade());
		if (fluxo == null) {
			msg.add("\u00C9 necess\u00E1rio configurar um fluxo para o ciclo da meta para a unidade "
					+ meta.getUnidade().getDescricao());
		} else {
			try {
				fluxo.getFluxo().getFluxo()
						.isPodeEnviarComite(meta, tipoConfiguracao, tipoGrupo);
			} catch (ValidateException e) {
				msg.addAll(e.getMsgErrors());
			}
		}
		if (!msg.isEmpty()) {
			throw new ValidateException(msg);
		}
	}

}
