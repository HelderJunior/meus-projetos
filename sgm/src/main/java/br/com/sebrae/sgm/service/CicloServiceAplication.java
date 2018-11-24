package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Calendario;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.FaseCiclo;
import br.com.sebrae.sgm.model.FaseCicloConfiguracao;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.producer.qualifier.Aplicacao;
import br.com.sebrae.sgm.utils.DateUtils;

@Named
@ApplicationScoped
public class CicloServiceAplication implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@Aplicacao
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Transactional
	public void finalizarCiclos() {
		try {
			List<Ciclo> ciclosFinalizar = em
					.createQuery(
							"select c from Ciclo c join c.configuracoes cf "
									+ " where cf.configuracaoAuditoria.dtFim < :dataAtual")
					.setParameter("dataAtual",  DateUtils.zeroTime(new Date())).getResultList();

			if (ciclosFinalizar != null) {
				for (Ciclo ciclo : ciclosFinalizar) {

					// em.createNativeQuery("update Ciclo set status = 'F' where id = :id")
					// .setParameter("id", ciclo.getId()).executeUpdate();
					ciclo.setStatus(StatusCiclo.F);

					List<CicloConfiguracao> configuracoesCiclo = ciclo.getConfiguracoes();
					for (CicloConfiguracao cc : configuracoesCiclo) {
						List<FaseCicloConfiguracao> faseCiclosConfiguracoes = cc.getFasesCicloConfiguracao();
						if (faseCiclosConfiguracoes != null) {
							for (FaseCicloConfiguracao fcc : faseCiclosConfiguracoes) {
								List<FaseCiclo> fasesCiclo = fcc.getFases();
								if (fasesCiclo != null) {
									for (FaseCiclo fc : fasesCiclo) {
										if (fc.getStatus() != StatusCiclo.F) {
											fc.setStatus(StatusCiclo.F);
											// em.merge(fc);
										}
									}
								}
							}
						}
					}
					em.merge(ciclo);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional
	public void finalizarFases() {
		try {
			List<CicloConfiguracao> ciclosConfiguracao = em.createQuery(
					"select distinct cf from CicloConfiguracao cf "
							+ " where cf.ciclo.status = 'I' and cf.statusConfiguracao = 'C'").getResultList();

			if (ciclosConfiguracao != null) {
				for (CicloConfiguracao cc : ciclosConfiguracao) {
					FaseCicloConfiguracao fcColaborador = cc.getFaseCicloByTipo(TipoGrupo.C);
					Calendario calColaborador = cc.getCalendarioColaborador();
					if (fcColaborador != null && calColaborador != null) {
						FaseCiclo fc = fcColaborador.getFaseByTipo(Fase.P);
						if (fc != null && calColaborador.getDtFimPactuacao() != null) {
							if (DateUtils.getDaysDiff(calColaborador.getDtFimPactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fcColaborador.getFaseByTipo(Fase.R);
						if (fc != null && calColaborador.getDtFimRepactuacao() != null) {
							if (DateUtils.getDaysDiff(calColaborador.getDtFimRepactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fcColaborador.getFaseByTipo(Fase.M);
						if (fc != null && calColaborador.getDtFimMonitoramento() != null) {
							if (DateUtils.getDaysDiff(calColaborador.getDtFimMonitoramento(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fcColaborador.getFaseByTipo(Fase.V);
						if (fc != null && calColaborador.getDtFimValidacao() != null) {
							if (DateUtils.getDaysDiff(calColaborador.getDtFimValidacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fcColaborador.getFaseByTipo(Fase.A);
						if (fc != null && calColaborador.getDtFimAuditoria() != null) {
							if (DateUtils.getDaysDiff(calColaborador.getDtFimAuditoria(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

					}

					FaseCicloConfiguracao fccEquipe = cc.getFaseCicloByTipo(TipoGrupo.E);
					Calendario calEquipe = cc.getCalendarioEquipe();

					if (fccEquipe != null && calEquipe != null) {
						FaseCiclo fc = fccEquipe.getFaseByTipo(Fase.P);
						if (fc != null && calEquipe.getDtFimPactuacao() != null) {
							if (DateUtils.getDaysDiff(calEquipe.getDtFimPactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccEquipe.getFaseByTipo(Fase.R);
						if (fc != null && calEquipe.getDtFimRepactuacao() != null) {
							if (DateUtils.getDaysDiff(calEquipe.getDtFimRepactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccEquipe.getFaseByTipo(Fase.M);
						if (fc != null && calEquipe.getDtFimMonitoramento() != null) {
							if (DateUtils.getDaysDiff(calEquipe.getDtFimMonitoramento(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccEquipe.getFaseByTipo(Fase.V);
						if (fc != null && calEquipe.getDtFimValidacao() != null) {
							if (DateUtils.getDaysDiff(calEquipe.getDtFimValidacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccEquipe.getFaseByTipo(Fase.A);
						if (fc != null && calEquipe.getDtFimAuditoria() != null) {
							if (DateUtils.getDaysDiff(calEquipe.getDtFimAuditoria(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

					}

					FaseCicloConfiguracao fccGerente = cc.getFaseCicloByTipo(TipoGrupo.G);
					Calendario calGerente = cc.getCalendarioGerente();

					if (fccGerente != null && calGerente != null) {
						FaseCiclo fc = fccGerente.getFaseByTipo(Fase.P);
						if (fc != null && calGerente.getDtFimPactuacao() != null) {
							if (DateUtils.getDaysDiff(calGerente.getDtFimPactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccGerente.getFaseByTipo(Fase.R);
						if (fc != null && calGerente.getDtFimRepactuacao() != null) {
							if (DateUtils.getDaysDiff(calGerente.getDtFimRepactuacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccGerente.getFaseByTipo(Fase.M);
						if (fc != null && calGerente.getDtFimMonitoramento() != null) {
							if (DateUtils.getDaysDiff(calGerente.getDtFimMonitoramento(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccGerente.getFaseByTipo(Fase.V);
						if (fc != null && calGerente.getDtFimValidacao() != null) {
							if (DateUtils.getDaysDiff(calGerente.getDtFimValidacao(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

						fc = fccGerente.getFaseByTipo(Fase.A);
						if (fc != null && calGerente.getDtFimAuditoria() != null) {
							if (DateUtils.getDaysDiff(calGerente.getDtFimAuditoria(), new Date()) > 0) {
								fc.setStatus(StatusCiclo.F);
								fc.setDataTermino(new Date());
							}
						}

					}
					em.merge(cc);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
