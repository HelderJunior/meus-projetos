package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.AppBean;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Calendario;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoAuditoria;
import br.com.sebrae.sgm.model.FaseCiclo;
import br.com.sebrae.sgm.model.FaseCicloConfiguracao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Prorrogacao;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.utils.DateUtils;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class CicloConfiguracaoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private PerfilService perfilService;

	public CicloConfiguracao load(Integer id) {
		return this.em.find(CicloConfiguracao.class, id);
	}

	@Transactional
	public void save(CicloConfiguracao p) throws ValidateException {
		ValidateUtils.validateThrows(p);
		if (p.getId() == null) {
			em.persist(p);
		} else {
			em.merge(p);
		}
	}
	
	@Transactional
	public void delete(Prorrogacao pr) throws ValidateException {
		em.remove(pr);
	}

	public List<CicloConfiguracao> findAll() {
		List<CicloConfiguracao> retorno = (List<CicloConfiguracao>) this.em.createNamedQuery(
				"CicloConfiguracao.findAll").getResultList();
		return retorno;
	}

	public List<CicloConfiguracao> findAllAndamento() {
		List<CicloConfiguracao> retorno = (List<CicloConfiguracao>) this.em.createNamedQuery(
				"CicloConfiguracao.findAllAndamento").getResultList();
		return retorno;
	}

	public CicloConfiguracao loadWithAvaliadores(Integer idCicloConfiguracao) {
		List<CicloConfiguracao> retorno = (List<CicloConfiguracao>) this.em
				.createQuery("Select cc from CicloConfiguracao cc left join cc.avaliadores where cc.id = :id")
				.setParameter("id", idCicloConfiguracao).getResultList();

		if (retorno != null && !retorno.isEmpty()) {
			return retorno.get(0);
		}
		return null;
	}

	@Transactional
	public void iniciarPactuacao(CicloConfiguracao cc) throws ValidateException {
		try {
			validarInicioPactuacao(cc);
			// gerente
			FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
			if (faseCicloConfiguracaoGerente == null) {
				faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
				faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
				faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
			}
			FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.P);
			if (fcGerente == null) {
				fcGerente = new FaseCiclo();
				fcGerente.setFase(Fase.P);
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
				faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
				fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
			} else {
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

			// colaborador
			FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
			if (faseCicloConfiguracaoColaborador == null) {
				faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
				faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
				faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
			}
			FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.P);
			if (fcColaborador == null) {
				fcColaborador = new FaseCiclo();
				fcColaborador.setFase(Fase.P);
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
				faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
				fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
			} else {
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
			}

			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

			// equipe
			FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
			if (faseCicloConfiguracaoEquipe == null) {
				faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
				faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
				faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
			}
			FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.P);
			if (fcEquipe == null) {
				fcEquipe = new FaseCiclo();
				fcEquipe.setFase(Fase.P);
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
				faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
				fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
			} else {
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
			}

			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);

			cc.getCiclo().setStatus(StatusCiclo.I);
			this.em.merge(cc);
			this.em.merge(cc.getCiclo());
		} catch (ValidateException e) {
			throw e;
		}
	}

	private void validarInicioPactuacao(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();

		/**
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de pactua\u00E7\u00E3o n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado "
		 * +cc.getCiclo().getDescricao()); }
		 **/
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		Calendario calColaborador = cc.getCalendarioColaborador();

		if (calColaborador == null || calColaborador.getDtInicioPactuacao() == null
				|| calColaborador.getDtFimPactuacao() == null) {
			errors.add("\u00C9 necess\u00E1rio configurar o calend\u00E1rio de pactua\u00E7\u00E3o do colaborador.");
		}

		if (calColaborador != null) {
			if (!DateUtils.isBetween(dataAtual, calColaborador.getDtInicioPactuacao(),
					calColaborador.getDtFimPactuacao())) {
				errors.add("Fase de pactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de pactua\u00E7\u00E3o do calend\u00E1rio do colaborador.");
			}
		}

		Calendario calGerente = cc.getCalendarioGerente();
		if (calGerente != null) {
			if (!DateUtils.isBetween(dataAtual, calGerente.getDtInicioPactuacao(), calGerente.getDtFimPactuacao())) {
				errors.add("Fase de pactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de pactua\u00E7\u00E3o do calend\u00E1rio do gerente.");
			}
		}

		Calendario calEquipe = cc.getCalendarioEquipe();
		if (calEquipe != null) {
			if (!DateUtils.isBetween(dataAtual, calEquipe.getDtInicioPactuacao(), calEquipe.getDtFimPactuacao())) {
				errors.add("Fase de pactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de pactua\u00E7\u00E3o do calend\u00E1rio de equipe.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	@Transactional
	public void iniciarRepactuacao(CicloConfiguracao cc) throws ValidateException {
		try {
			validarInicioRepactuacao(cc);
			// gerente
			FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
			if (faseCicloConfiguracaoGerente == null) {
				faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
				faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
				faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
			}
			FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.R);
			if (fcGerente == null) {
				fcGerente = new FaseCiclo();
				fcGerente.setFase(Fase.R);
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
				faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
				fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
			} else {
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

			// colaborador
			FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
			if (faseCicloConfiguracaoColaborador == null) {
				faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
				faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
				faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
			}
			FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.R);
			if (fcColaborador == null) {
				fcColaborador = new FaseCiclo();
				fcColaborador.setFase(Fase.R);
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
				faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
				fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
			} else {
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
			}

			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

			// equipe
			FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
			if (faseCicloConfiguracaoEquipe == null) {
				faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
				faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
				faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
			}
			FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.R);
			if (fcEquipe == null) {
				fcEquipe = new FaseCiclo();
				fcEquipe.setFase(Fase.R);
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
				faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
				fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
			} else {
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);

			cc.getCiclo().setStatus(StatusCiclo.I);
			this.em.merge(cc);
			this.em.merge(cc.getCiclo());
		} catch (ValidateException e) {
			throw e;
		}
	}

	private void validarInicioRepactuacao(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}
		
		FaseCicloConfiguracao faseCicloConfiguracao = getFaseCicloConfiguracao(cc);

		/*
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de repactua\u00E7\u00E3o n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado "
		 * +cc.getCiclo().getDescricao()); }
		 */

		if (faseCicloConfiguracao == null) {
			errors.add("Nenhuma fase para o perfil dispon\u00EDvel para inicializa\u00E7\u00E3o da pactua\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		Calendario calColaborador = cc.getCalendarioColaborador();

		if (calColaborador == null || calColaborador.getDtInicioRepactuacao() == null
				|| calColaborador.getDtFimRepactuacao() == null) {
			errors.add("\u00C9 necess\u00E1rio configurar o calend\u00E1rio de repactua\u00E7\u00E3o do colaborador.");
		}

		if (calColaborador != null) {
			if (!DateUtils.isBetween(dataAtual, calColaborador.getDtInicioRepactuacao(),
					calColaborador.getDtFimRepactuacao())) {
				errors.add("Fase de repactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de repactua\u00E7\u00E3o do calend\u00E1rio do colaborador.");
			}
		}

		Calendario calGerente = cc.getCalendarioGerente();
		if (calGerente != null) {
			if (!DateUtils.isBetween(dataAtual, calGerente.getDtInicioRepactuacao(), calGerente.getDtFimRepactuacao())) {
				errors.add("Fase de repactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de repactua\u00E7\u00E3o do calend\u00E1rio do gerente.");
			}
		}

		Calendario calEquipe = cc.getCalendarioEquipe();
		if (calEquipe != null) {
			if (!DateUtils.isBetween(dataAtual, calEquipe.getDtInicioRepactuacao(), calEquipe.getDtFimRepactuacao())) {
				errors.add("Fase de repactua\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de repactua\u00E7\u00E3o do calend\u00E1rio de equipe.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	@Transactional
	public void iniciarMonitoramentoAjustes(CicloConfiguracao cc) throws ValidateException {
		if (cc.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
			iniciarMonitoramento(cc);
		} else if (cc.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESENV) {
			iniciarAjustes(cc);
		}
	}

	private void iniciarMonitoramento(CicloConfiguracao cc) throws ValidateException {
		validarInicioMonitoramento(cc);
		// gerente
		FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
		if (faseCicloConfiguracaoGerente == null) {
			faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
			faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
			faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
		}
		FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.M);
		if (fcGerente == null) {
			fcGerente = new FaseCiclo();
			fcGerente.setFase(Fase.M);
			fcGerente.setStatus(StatusCiclo.I);
			fcGerente.setDataInicio(new Date());
			faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
			fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
		} else {
			fcGerente.setStatus(StatusCiclo.I);
			fcGerente.setDataInicio(new Date());
		}
		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

		// colaborador
		FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
		if (faseCicloConfiguracaoColaborador == null) {
			faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
			faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
			faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
		}
		FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.M);
		if (fcColaborador == null) {
			fcColaborador = new FaseCiclo();
			fcColaborador.setFase(Fase.M);
			fcColaborador.setStatus(StatusCiclo.I);
			fcColaborador.setDataInicio(new Date());
			faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
			fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
		} else {
			fcColaborador.setStatus(StatusCiclo.I);
			fcColaborador.setDataInicio(new Date());
		}

		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

		// equipe
		FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
		if (faseCicloConfiguracaoEquipe == null) {
			faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
			faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
			faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
		}
		FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.M);
		if (fcEquipe == null) {
			fcEquipe = new FaseCiclo();
			fcEquipe.setFase(Fase.M);
			fcEquipe.setStatus(StatusCiclo.I);
			fcEquipe.setDataInicio(new Date());
			faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
			fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
		} else {
			fcEquipe.setStatus(StatusCiclo.I);
			fcEquipe.setDataInicio(new Date());
		}

		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);

		cc.getCiclo().setStatus(StatusCiclo.I);
		this.em.merge(cc);
		this.em.merge(cc.getCiclo());
	}

	private void iniciarAjustes(CicloConfiguracao cc) throws ValidateException {
		validarInicioAjustes(cc);
		// gerente
		FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
		if (faseCicloConfiguracaoGerente == null) {
			faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
			faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
			faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
		}
		FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.J);
		if (fcGerente == null) {
			fcGerente = new FaseCiclo();
			fcGerente.setFase(Fase.J);
			fcGerente.setStatus(StatusCiclo.I);
			fcGerente.setDataInicio(new Date());
			faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
			fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
		} else {
			fcGerente.setStatus(StatusCiclo.I);
			fcGerente.setDataInicio(new Date());
		}
		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

		// colaborador
		FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
		if (faseCicloConfiguracaoColaborador == null) {
			faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
			faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
			faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
		}
		FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.J);
		if (fcColaborador == null) {
			fcColaborador = new FaseCiclo();
			fcColaborador.setFase(Fase.J);
			fcColaborador.setStatus(StatusCiclo.I);
			fcColaborador.setDataInicio(new Date());
			faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
			fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
		} else {
			fcColaborador.setStatus(StatusCiclo.I);
			fcColaborador.setDataInicio(new Date());
		}

		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

		// equipe
		FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
		if (faseCicloConfiguracaoEquipe == null) {
			faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
			faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
			faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
		}
		FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.J);
		if (fcEquipe == null) {
			fcEquipe = new FaseCiclo();
			fcEquipe.setFase(Fase.J);
			fcEquipe.setStatus(StatusCiclo.I);
			fcEquipe.setDataInicio(new Date());
			faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
			fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
		} else {
			fcEquipe.setStatus(StatusCiclo.I);
			fcEquipe.setDataInicio(new Date());
		}
		cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
		cc.getCiclo().setStatus(StatusCiclo.I);
		this.em.merge(cc);
		this.em.merge(cc.getCiclo());
	}

	private void validarInicioAjustes(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}
		
		FaseCicloConfiguracao faseCicloConfiguracao = getFaseCicloConfiguracao(cc);

		/*
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de ajustes n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado  "
		 * +cc.getCiclo().getDescricao()); }
		 */

		if (faseCicloConfiguracao == null) {
			errors.add("Nenhuma fase para o perfil dispon\u00EDvel para inicializa\u00E7\u00E3o da pactua\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		Calendario calColaborador = cc.getCalendarioColaborador();
		if (calColaborador != null) {
			if (!DateUtils.isBetween(dataAtual, calColaborador.getDtInicioAjustes(), calColaborador.getDtFimAjustes())) {
				errors.add("Fase de ajustes n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de ajustes do calend\u00E1rio do colaborador.");
			}
		}

		Calendario calGerente = cc.getCalendarioGerente();
		if (calGerente != null) {
			if (!DateUtils.isBetween(dataAtual, calGerente.getDtInicioAjustes(), calGerente.getDtFimAjustes())) {
				errors.add("Fase de ajustes n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de ajustes do calend\u00E1rio do gerente.");
			}
		}

		Calendario calEquipe = cc.getCalendarioEquipe();
		if (calEquipe != null) {
			if (!DateUtils.isBetween(dataAtual, calEquipe.getDtInicioAjustes(), calEquipe.getDtFimAjustes())) {
				errors.add("Fase de ajustes n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de ajustes do calend\u00E1rio de equipe.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	private void validarInicioMonitoramento(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}
		
		FaseCicloConfiguracao faseCicloConfiguracao = getFaseCicloConfiguracao(cc);

		/*
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de monitoramento n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado  "
		 * +cc.getCiclo().getDescricao()); }
		 */

		if (faseCicloConfiguracao == null) {
			errors.add("Nenhuma fase para o perfil dispon\u00EDvel para inicializa\u00E7\u00E3o da pactua\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		Calendario calColaborador = cc.getCalendarioColaborador();

		if (calColaborador == null || calColaborador.getDtInicioMonitoramento() == null
				|| calColaborador.getDtFimMonitoramento() == null) {
			errors.add("\u00C9 necess\u00E1rio configurar o calend\u00E1rio de monitoramento do colaborador.");
		}

		if (calColaborador != null) {
			if (!DateUtils.isBetween(dataAtual, calColaborador.getDtInicioMonitoramento(),
					calColaborador.getDtFimMonitoramento())) {
				errors.add("Fase de monitoramento n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de monitoramento do calend\u00E1rio do colaborador.");
			}
		}

		Calendario calGerente = cc.getCalendarioGerente();
		if (calGerente != null) {
			if (!DateUtils.isBetween(dataAtual, calGerente.getDtInicioMonitoramento(),
					calGerente.getDtFimMonitoramento())) {
				errors.add("Fase de monitoramento n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de monitoramento do calend\u00E1rio do gerente.");
			}
		}

		Calendario calEquipe = cc.getCalendarioEquipe();
		if (calEquipe != null) {
			if (!DateUtils
					.isBetween(dataAtual, calEquipe.getDtInicioMonitoramento(), calEquipe.getDtFimMonitoramento())) {
				errors.add("Fase de monitoramento n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de monitoramento do calend\u00E1rio de equipe.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	@Transactional
	public void iniciarValidacao(CicloConfiguracao cc) throws ValidateException {
		try {
			validarInicioValidacao(cc);

			// gerente
			FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
			if (faseCicloConfiguracaoGerente == null) {
				faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
				faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
				faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
			}
			FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.V);
			if (fcGerente == null) {
				fcGerente = new FaseCiclo();
				fcGerente.setFase(Fase.V);
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
				faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
				fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
			} else {
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

			// colaborador
			FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
			if (faseCicloConfiguracaoColaborador == null) {
				faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
				faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
				faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
			}
			FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.V);
			if (fcColaborador == null) {
				fcColaborador = new FaseCiclo();
				fcColaborador.setFase(Fase.V);
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
				faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
				fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
			} else {
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
			}

			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

			// equipe
			FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
			if (faseCicloConfiguracaoEquipe == null) {
				faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
				faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
				faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
			}
			FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.V);
			if (fcEquipe == null) {
				fcEquipe = new FaseCiclo();
				fcEquipe.setFase(Fase.V);
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
				faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
				fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
			} else {
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);

			cc.getCiclo().setStatus(StatusCiclo.I);
			this.em.merge(cc);
			this.em.merge(cc.getCiclo());
		} catch (ValidateException e) {
			throw e;
		}
	}

	private void validarInicioValidacao(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}
		
		FaseCicloConfiguracao faseCicloConfiguracao = getFaseCicloConfiguracao(cc);

		/*
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de valida\u00E7\u00E3o n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado "
		 * +cc.getCiclo().getDescricao()); }
		 */

		if (faseCicloConfiguracao == null) {
			errors.add("Nenhuma fase para o perfil dispon\u00EDvel para inicializa\u00E7\u00E3o da pactua\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		Calendario calColaborador = cc.getCalendarioColaborador();

		if (calColaborador == null || calColaborador.getDtInicioValidacao() == null
				|| calColaborador.getDtFimValidacao() == null) {
			errors.add("\u00C9 necess\u00E1rio configurar o calend\u00E1rio de valida\u00E7\u00E3o do colaborador.");
		}

		if (calColaborador != null) {
			if (!DateUtils.isBetween(dataAtual, calColaborador.getDtInicioValidacao(),
					calColaborador.getDtFimValidacao())) {
				errors.add("Fase de valida\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de valida\u00E7\u00E3o do calend\u00E1rio do colaborador.");
			}
		}

		Calendario calGerente = cc.getCalendarioGerente();
		if (calGerente != null) {
			if (!DateUtils.isBetween(dataAtual, calGerente.getDtInicioValidacao(), calGerente.getDtFimValidacao())) {
				errors.add("Fase de valida\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de valida\u00E7\u00E3o do calend\u00E1rio do gerente.");
			}
		}

		Calendario calEquipe = cc.getCalendarioEquipe();
		if (calEquipe != null) {
			if (!DateUtils.isBetween(dataAtual, calEquipe.getDtInicioValidacao(), calEquipe.getDtFimValidacao())) {
				errors.add("Fase de valida\u00E7\u00E3o n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de valida\u00E7\u00E3o do calend\u00E1rio de equipe.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	@Transactional
	public void iniciarAuditoria(CicloConfiguracao cc) throws ValidateException {
		try {
			validarInicioAuditoria(cc);
			// gerente
			FaseCicloConfiguracao faseCicloConfiguracaoGerente = cc.getFaseCicloGerente();
			if (faseCicloConfiguracaoGerente == null) {
				faseCicloConfiguracaoGerente = new FaseCicloConfiguracao();
				faseCicloConfiguracaoGerente.setTipo(TipoGrupo.G);
				faseCicloConfiguracaoGerente.setCicloConfiguracao(cc);
			}
			FaseCiclo fcGerente = faseCicloConfiguracaoGerente.getFaseByTipo(Fase.A);
			if (fcGerente == null) {
				fcGerente = new FaseCiclo();
				fcGerente.setFase(Fase.A);
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
				faseCicloConfiguracaoGerente.adicionarFase(fcGerente);
				fcGerente.setFaseCicloConfiguracao(faseCicloConfiguracaoGerente);
			} else {
				fcGerente.setStatus(StatusCiclo.I);
				fcGerente.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoGerente);

			// colaborador
			FaseCicloConfiguracao faseCicloConfiguracaoColaborador = cc.getFaseCicloColaborador();
			if (faseCicloConfiguracaoColaborador == null) {
				faseCicloConfiguracaoColaborador = new FaseCicloConfiguracao();
				faseCicloConfiguracaoColaborador.setTipo(TipoGrupo.C);
				faseCicloConfiguracaoColaborador.setCicloConfiguracao(cc);
			}
			FaseCiclo fcColaborador = faseCicloConfiguracaoColaborador.getFaseByTipo(Fase.A);
			if (fcColaborador == null) {
				fcColaborador = new FaseCiclo();
				fcColaborador.setFase(Fase.A);
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
				faseCicloConfiguracaoColaborador.adicionarFase(fcColaborador);
				fcColaborador.setFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);
			} else {
				fcColaborador.setStatus(StatusCiclo.I);
				fcColaborador.setDataInicio(new Date());
			}

			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoColaborador);

			// equipe
			FaseCicloConfiguracao faseCicloConfiguracaoEquipe = cc.getFaseCicloEquipe();
			if (faseCicloConfiguracaoEquipe == null) {
				faseCicloConfiguracaoEquipe = new FaseCicloConfiguracao();
				faseCicloConfiguracaoEquipe.setTipo(TipoGrupo.E);
				faseCicloConfiguracaoEquipe.setCicloConfiguracao(cc);
			}
			FaseCiclo fcEquipe = faseCicloConfiguracaoEquipe.getFaseByTipo(Fase.A);
			if (fcEquipe == null) {
				fcEquipe = new FaseCiclo();
				fcEquipe.setFase(Fase.A);
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
				faseCicloConfiguracaoEquipe.adicionarFase(fcEquipe);
				fcEquipe.setFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);
			} else {
				fcEquipe.setStatus(StatusCiclo.I);
				fcEquipe.setDataInicio(new Date());
			}
			cc.adicionarFaseCicloConfiguracao(faseCicloConfiguracaoEquipe);

			cc.getCiclo().setStatus(StatusCiclo.I);
			this.em.merge(cc);
			this.em.merge(cc.getCiclo());
		} catch (ValidateException e) {
			throw e;
		}
	}

	private void validarInicioAuditoria(CicloConfiguracao cc) throws ValidateException {
		List<String> errors = new ArrayList<String>();
		
		if(!cc.getCiclo().isPodeIniciar()){
			errors.add("Para que o ciclo possa ser iniciado, \u00E9 necess\u00E1rio concluir as configura\u00E7\u00F5es de Calend\u00E1rio, Avaliadores para o ciclo, Metas Individuais e de Equipe, Visualiza\u00E7\u00E3o de Metas e Fluxo de aprova\u00E7\u00E3o.");
		}
		
		FaseCicloConfiguracao faseCicloConfiguracao = getFaseCicloConfiguracao(cc);

		/*
		 * if(cc.getStatusConfiguracao() != StatusConfiguracao.C){ errors.add(
		 * "Fase de auditoria n\u00E3o pode ser iniciada, ainda existe configura\u00E7\u00F5es em andamento para o ciclo selecionado "
		 * +cc.getCiclo().getDescricao()); }
		 */

		if (faseCicloConfiguracao == null) {
			errors.add("Nenhuma fase para o perfil dispon\u00EDvel para inicializa\u00E7\u00E3o da pactua\u00E7\u00E3o.");
		}

		Date dataAtual = new Date();
		ConfiguracaoAuditoria confAuditoria = cc.getConfiguracaoAuditoria();

		if (confAuditoria == null || confAuditoria.getDtInicio() == null || confAuditoria.getDtFim() == null) {
			errors.add("\u00C9 necess\u00E1rio configurar o calend\u00E1rio de auditoria do ciclo.");
		}

		if (confAuditoria != null) {
			if (!DateUtils.isBetween(dataAtual, confAuditoria.getDtInicio(), confAuditoria.getDtFim())) {
				errors.add("Fase de auditoria n\u00E3o pode ser iniciada, data atual n\u00E3o encontra-se dentro da data inicial e final de auditoria configurado.");
			}
		}

		if (!errors.isEmpty()) {
			throw new ValidateException(errors);
		}
	}

	public FaseCicloConfiguracao getFaseCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseGerente = cicloConfiguracao.getFaseCicloGerente();
			if (faseGerente != null) {
				return faseGerente;
			} else {
				faseGerente = new FaseCicloConfiguracao();
				faseGerente.setTipo(TipoGrupo.G);
				faseGerente.setCicloConfiguracao(cicloConfiguracao);
				this.em.persist(cicloConfiguracao);
				return faseGerente;
			}
		}

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)
				&& !perfilService.isGerente(cicloConfiguracao, appBean.getUsuarioAutenticado(), Fase.P)) {
			FaseCicloConfiguracao fase = cicloConfiguracao.getFaseCicloColaborador();
			if (fase != null) {
				return fase;
			} else {
				fase = new FaseCicloConfiguracao();
				fase.setTipo(TipoGrupo.C);
				fase.setCicloConfiguracao(cicloConfiguracao);
				this.em.persist(cicloConfiguracao);
				return fase;
			}
		}

		if (perfilService.isGerente(cicloConfiguracao, appBean.getUsuarioAutenticado(), Fase.P)) {
			FaseCicloConfiguracao fase = cicloConfiguracao.getFaseCicloEquipe();
			if (fase != null) {
				return fase;
			} else {
				fase = new FaseCicloConfiguracao();
				fase.setTipo(TipoGrupo.E);
				fase.setCicloConfiguracao(cicloConfiguracao);
				this.em.persist(cicloConfiguracao);
				return fase;
			}
		}

		// se nao for nenhum dos perfis logados, retorna do colaborador
		FaseCicloConfiguracao fase = cicloConfiguracao.getFaseCicloColaborador();
		if (fase != null) {
			return fase;
		} else {
			fase = new FaseCicloConfiguracao();
			fase.setTipo(TipoGrupo.C);
			fase.setCicloConfiguracao(cicloConfiguracao);
			this.em.persist(cicloConfiguracao);
			return fase;
		}
	}

	public StatusCiclo getStatusPactuacao(CicloConfiguracao cicloConfiguracao) {
		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

		cicloConfiguracao = this.em.find(CicloConfiguracao.class, cicloConfiguracao.getId());
		if (cicloConfiguracao.getFasesCicloConfiguracao() != null)
			cicloConfiguracao.getFasesCicloConfiguracao().size();

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloGerente();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.P);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)
				&& !perfilService.isGerente(cicloConfiguracao, appBean.getUsuarioAutenticado(), Fase.P)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.P);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		if (perfilService.isGerente(cicloConfiguracao, appBean.getUsuarioAutenticado(), Fase.P)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloEquipe();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.P);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		// caso nao for nenhum perfil acima, retorna do colaborador
		FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
		if (faseConfiguracao != null) {
			FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.P);
			if (fase != null) {
				return fase.getStatus();
			}
		}

		return StatusCiclo.N;
	}

	public StatusCiclo getStatusRepactuacao(CicloConfiguracao cicloConfiguracao) {

		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

		cicloConfiguracao = this.em.find(CicloConfiguracao.class, cicloConfiguracao.getId());
		if (cicloConfiguracao.getFasesCicloConfiguracao() != null)
			cicloConfiguracao.getFasesCicloConfiguracao().size();

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloGerente();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.R);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.R);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
		if (faseConfiguracao != null) {
			FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.R);
			if (fase != null) {
				return fase.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusMonitoramentoAjustes(CicloConfiguracao cicloConfiguracao) {
		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

		cicloConfiguracao = this.em.find(CicloConfiguracao.class, cicloConfiguracao.getId());
		if (cicloConfiguracao.getFasesCicloConfiguracao() != null)
			cicloConfiguracao.getFasesCicloConfiguracao().size();

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloGerente();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.M);
				if (fase != null) {
					return fase.getStatus();
				}

				fase = faseConfiguracao.getFaseByTipo(Fase.J);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.M);
				if (fase != null) {
					return fase.getStatus();
				}

				fase = faseConfiguracao.getFaseByTipo(Fase.J);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
		if (faseConfiguracao != null) {
			FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.M);
			if (fase != null) {
				return fase.getStatus();
			}

			fase = faseConfiguracao.getFaseByTipo(Fase.J);
			if (fase != null) {
				return fase.getStatus();
			}
		}

		return StatusCiclo.N;
	}

	public StatusCiclo getStatusValidacao(CicloConfiguracao cicloConfiguracao) {
		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

		cicloConfiguracao = this.em.find(CicloConfiguracao.class, cicloConfiguracao.getId());
		if (cicloConfiguracao.getFasesCicloConfiguracao() != null)
			cicloConfiguracao.getFasesCicloConfiguracao().size();

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloGerente();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.V);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.V);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
		if (faseConfiguracao != null) {
			FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.V);
			if (fase != null) {
				return fase.getStatus();
			}
		}
		return StatusCiclo.N;
	}

	public StatusCiclo getStatusAuditoria(CicloConfiguracao cicloConfiguracao) {
		AppBean appBean = BeanProvider.getContextualReference(AppBean.class);

		cicloConfiguracao = this.em.find(CicloConfiguracao.class, cicloConfiguracao.getId());
		if (cicloConfiguracao.getFasesCicloConfiguracao() != null)
			cicloConfiguracao.getFasesCicloConfiguracao().size();

		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE)
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloGerente();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.A);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE)) {
			FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
			if (faseConfiguracao != null) {
				FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.A);
				if (fase != null) {
					return fase.getStatus();
				}
			}
		}

		FaseCicloConfiguracao faseConfiguracao = cicloConfiguracao.getFaseCicloColaborador();
		if (faseConfiguracao != null) {
			FaseCiclo fase = faseConfiguracao.getFaseByTipo(Fase.A);
			if (fase != null) {
				return fase.getStatus();
			}
		}
		return StatusCiclo.N;
	}
	
	public List<Prorrogacao> loadCalendarioAndProrrogacao(Ciclo ciclo, TipoGrupo tipo) {
		List<Prorrogacao> retorno = this.em
				.createQuery("Select p from Prorrogacao p join fetch p.calendario pc join pc.cicloConfiguracao pcc where pcc.ciclo =:ciclo and pc.tipo =:tipo")
				.setParameter("ciclo", ciclo).setParameter("tipo", tipo).getResultList();
		return retorno;
	} 

}
