package br.com.sebrae.sgm.model.fluxo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.MetaStatus;
import br.com.sebrae.sgm.model.Observacao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.audit.listener.TipoAlteracaoTheadLocal;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusEnvio;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.model.enums.TipoObservacao;

public class Fluxo1 implements Fluxo {

	@Override
	public void isPodeEnviarAprovacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();

		/*StatusMeta statusAtual = meta.getStatusAtual();
		if (statusAtual != StatusMeta.SA) {
			if (statusAtual == StatusMeta.OS || statusAtual == StatusMeta.NA || statusAtual == StatusMeta.CS)
				meta.getMetaStatusByStatus(statusAtual).setStatus(StatusMeta.SA);
		}

		StatusMeta status = meta.getStatusMetaByStatus(StatusMeta.SA);

		if (status == null) {
			erros.add("Meta " + meta.getCodigo()
					+ " n\u00E3o pode ser enviada para valida\u00E7\u00E3o devido a seu status atual: "
					+ meta.getStatusAtual().getValue());
		} */

		if (!(meta.getCicloConfiguracao().getCiclo().getStatusPactuacao(tipoConfiguracao, tipoCalendario) == StatusCiclo.I || meta
				.getCicloConfiguracao().getCiclo().getStatusRepactuacao(tipoConfiguracao, tipoCalendario) == StatusCiclo.I)) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser enviada para valida\u00E7\u00E3o, ciclo deve ter a fase de pactua\u00E7ao/repactua\u00E7\u00E3o iniciada.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void enviarAprovacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		MetaStatus ms = null;
		Fase f = null;

		if (meta.getCicloConfiguracao().getCiclo().getStatusPactuacao(tipoConfiguracao, tipoCalendario) == StatusCiclo.I) {
			f = Fase.P;
		}
		if (meta.getCicloConfiguracao().getCiclo().getStatusRepactuacao(tipoConfiguracao, tipoCalendario) == StatusCiclo.I) {
			f = Fase.R;
		}
		if (meta.getMetaStatusByStatus(StatusMeta.SA) != null) {
			ms = meta.getMetaStatusByStatus(StatusMeta.SA);
		} else  {
			ms = meta.getMetaStatusAtual();
		}

		if (ms == null) {
			ms = new MetaStatus();
			ms.setFase(f);
			ms.setStatus(StatusMeta.PA);
			ms.setMeta(meta);
			meta.adicionarMetaStatus(ms);
		} else {
			ms.setFase(f);
			ms.setStatus(StatusMeta.PA);
		}
		meta.setStatusAtual(StatusMeta.PA);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.EVA);
	}

	public void isPodeAprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();
		/*MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);

		if (ms == null) {
			erros.add("A meta n\u00E3o pode ser aprovada, \u00E9 necess\u00E1rio que a meta seja enviada para aprova\u00E7\u00E3o.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}*/
	}

	@Override
	public void aprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		if (meta.getTipo() == TipoMeta.I || meta.getTipo() == TipoMeta.E) {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);
			ms.setStatus(StatusMeta.AP);
			ms.setFase(Fase.M);
			meta.setStatusAtual(StatusMeta.AP);
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.APR);
		} else if (meta.getTipo() == TipoMeta.D) {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);
			ms.setStatus(StatusMeta.AP);
			meta.setStatusAtual(StatusMeta.AP);
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.APR);
			ms.setFase(Fase.A);
		}
	}

	public void isPodeReprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);

		/*if (ms == null) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser reprovada, \u00E9 necess\u00E1rio que a meta seja enviada para aprova\u00E7\u00E3o.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}*/
	}

	@Override
	public void reprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);
		ms.setStatus(StatusMeta.NA);
		meta.setStatusAtual(StatusMeta.NA);
	}

	@Override
	public void isPodeCancelarSuperior(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);

		List<String> erros = new ArrayList<String>();

		/*if (ms == null) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser cancelada, \u00E9 necess\u00E1rio que a meta seja enviada para aprova\u00E7\u00E3o.");
		}*/

		if (meta.getCicloConfiguracao().getCiclo().getStatusPactuacao(tipoConfiguracao, tipoCalendario) != StatusCiclo.I
				&& meta.getCicloConfiguracao().getCiclo().getStatusRepactuacao(tipoConfiguracao, tipoCalendario) != StatusCiclo.I) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser cancelada, o ciclo deve estar na fase de pactua\u00E7\u00E3o/repactua\u00E7\u00E3o para envio.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void cancelarSuperior(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PA);
		ms.setStatus(StatusMeta.CS);
		meta.setStatusAtual(StatusMeta.CS);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.CAM);
	}

	@Override
	public void isPodeEnviarObservacaoSuperior(Meta meta) throws ValidateException {
		List<String> erros = new ArrayList<String>();
		MetaStatus ms = meta.getMetaStatusAtual();
		/*
		 * if (ms.getStatus() != StatusMeta.PA) { erros.add("Meta " + meta.getCodigo() +
		 * " n\u00E3o pode ser enviada observaï¿½ï¿½o do superior, deve ser enviada para aprovaï¿½ï¿½o antes."); }
		 */
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void enviarObservacaoSuperior(Meta meta, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.G);
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);
		obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);

		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}

		meta.getObservacoes().add(obs);
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OS);
		ms.setFase(Fase.P);
		meta.setStatusAtual(StatusMeta.OS);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}

	@Override
	public void isPodeEnviarValidacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();

		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);

		/*if (ms == null) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser enviada para valida\u00E7\u00E3o, \u00E9 necess\u00E1rio que seja aprovada pelo superior.");
		}*/


		if (meta.getCicloConfiguracao().getCiclo().getStatusMonitoramento(tipoConfiguracao, tipoCalendario) != StatusCiclo.I) {
			erros.add("Meta " + meta.getCodigo()
					+ " n\u00E3o pode ser enviada para valida\u00E7\u00E3o, o ciclo deve estar com a fase de valida\u00E7\u00E3o iniciada.");
		}


		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void enviarValidacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.PV);
		ms.setFase(Fase.V);
		meta.setStatusAtual(StatusMeta.PV);
	}

	@Override
	public void isPodeEnviarObservacaoComite(Meta meta) throws ValidateException {
		List<String> erros = new ArrayList<String>();
		/*if (meta.getCicloConfiguracao().getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PV);
			if (ms == null) {
				erros.add("Meta "
						+ meta.getCodigo()
						+ " n\u00E3o pode ser enviado observa\u00E7\u00E3o, \u00E9 necess\u00E1rio que seja enviada para valida\u00E7\u00E3o.");
			}
		} else {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);
			if (ms == null) {
				erros.add("Meta "
						+ meta.getCodigo()
						+ " n\u00E3o pode ser enviado observa\u00E7\u00E3o, \u00E9 necess\u00E1rio que seja enviada para valida\u00E7\u00E3o.");
			}
		}
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}*/
	}

	@Override
	public void enviarObservacaoComite(Meta meta, Usuario usuario, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.C);
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);
		obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);
		obs.setRemetente(usuario);

		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}

		meta.getObservacoes().add(obs);

		if (meta.getCicloConfiguracao().getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
			//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PV);
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.OC);
			meta.setStatusAtual(StatusMeta.OC);
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
		} else {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);
			ms.setStatus(StatusMeta.OC);
			meta.setStatusAtual(StatusMeta.OC);
			TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
		}
	}

	@Override
	public void isPodeValidarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP) {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PV);
			/*if (ms == null) {
				erros.add("Meta "
						+ meta.getCodigo()
						+ " n\u00E3o pode ser validada, \u00E9 necess\u00E1rio que seja enviada para valida\u00E7\u00E3o primeiro.");
			}*/


			 if (meta.getCicloConfiguracao().getCiclo().getStatusValidacao(tipoConfiguracao, tipoCalendario) != StatusCiclo.I) {
			 erros.add("Meta " + meta.getCodigo() +
			  " n\u00E3o pode ser validada, o ciclo deve estar com a fase de validacao iniciada."); }

		} /*else {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);
			if (ms == null) {
				erros.add("Meta "
						+ meta.getCodigo()
						+ " n\u00E3o pode ser validada, \u00E9 necess\u00E1rio que seja aprovada pelo superior primeiro.");
			}
		}*/

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void validarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		if (tipoConfiguracao == TipoConfiguracaoCiclo.DESEMP) {
			//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.PV);
			MetaStatus ms = meta.getMetaStatusAtual();
			ms.setStatus(StatusMeta.VC);
			meta.setStatusAtual(StatusMeta.VC);
			ms.setFase(Fase.A);
		} else {
			MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.AS);
			ms.setStatus(StatusMeta.VC);
			ms.setFase(Fase.V);
			meta.setStatusAtual(StatusMeta.VC);
		}
	}

	@Override
	public void isPodeEnviarObservacaoAuditor(Meta meta) throws ValidateException {
		List<String> erros = new ArrayList<String>();
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void isPodeEnviarObservacaoAuditorGerente(Meta meta) throws ValidateException {
		List<String> erros = new ArrayList<String>();
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void enviarObservacaoAuditor(Meta meta, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.U);
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);
		obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);
		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}
		meta.getObservacoes().add(obs);
		//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OA);
		meta.setStatusAtual(StatusMeta.OA);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}

	@Override
	public void enviarObservacaoAuditorGerente(Meta meta, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.U);
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);
		obs.setChavePerfilDestino(Perfil.GERENTE);

		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}

		meta.getObservacoes().add(obs);
		//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OA);
		meta.setStatusAtual(StatusMeta.OA);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}

	@Override
	public void atestarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		ms.setStatus(StatusMeta.AA);
		ms.setFase(Fase.A);
		meta.setStatusAtual(StatusMeta.AA);
	}

	@Override
	public void isPodeAtestarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {

		List<String> erros = new ArrayList<String>();
		MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		/*
		if (ms == null) {
			erros.add("Meta " + meta.getCodigo()
					+ " n\u00E3o pode ser atestada, \u00E9 necess\u00E1rio que seja aprovada pelo comit\u00EA.");
		}*/

		if (meta.getCicloConfiguracao().getCiclo().getStatusAuditoria(tipoConfiguracao, tipoCalendario) != StatusCiclo.I) {
			erros.add("Meta " + meta.getCodigo()
					+ " n\u00E3o pode ser atestada, o ciclo deve estar com a fase de auditoria iniciada.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	@Override
	public void isPodeEnviarComiteAuditor(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario)
			throws ValidateException {
		List<String> erros = new ArrayList<String>();

		/*MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);

		if (ms == null) {
			erros.add("Meta "
					+ meta.getCodigo()
					+ " n\u00E3o pode ser enviada para valida\u00E7\u00E3o do comit\u00EA, \u00E9 necess\u00E1rio que seja aprovada pelo superior.");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}*/
	}

	@Override
	public void enviarComiteAuditor(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		//MetaStatus ms = meta.getMetaStatusByStatus(StatusMeta.VC);
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.PV);
		ms.setFase(Fase.A);
		meta.setStatusAtual(StatusMeta.PV);
	}

	@Override
	public void enviarObservacaoColaborador(Meta meta, Usuario usuario, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.G);//Gerente
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);//Enviado
		obs.setDataHora(new Date());
		obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);
		obs.setRemetente(usuario);

		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}

		meta.getObservacoes().add(obs);

		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OS);

		meta.setStatusAtual(StatusMeta.OS);//Observações do Gerente
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}

	@Override
	public void getenteEnviarObservacaoComite(Meta meta, Usuario usuario, String observacao) {
		Observacao obs = new Observacao();
		obs.setMeta(meta);
		obs.setTipo(TipoObservacao.C);//Comitê
		obs.setDescricao(observacao);
		obs.setStatus(StatusEnvio.E);//Enviado
		obs.setDataHora(new Date());
		obs.setChavePerfilDestino(Perfil.COLABORADOR_CHAVE);
		obs.setRemetente(usuario);

		if (meta.getObservacoes() == null) {
			meta.setObservacoes(new ArrayList<Observacao>());
		}

		meta.getObservacoes().add(obs);

		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OC);

		meta.setStatusAtual(StatusMeta.OC);//Observações do Comitê
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}
	
	@Override
	public void enviarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) {
		MetaStatus ms = meta.getMetaStatusAtual();
		ms.setStatus(StatusMeta.OC);

		meta.setStatusAtual(StatusMeta.OC);
		TipoAlteracaoTheadLocal.tipoLog.set(TipoLog.OBS);
	}
	
	@Override
	public void isPodeEnviarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException {
		List<String> erros = new ArrayList<String>();

		/*if (meta.getCicloConfiguracao().getCiclo().getStatusValidacao(tipoConfiguracao, tipoCalendario) != StatusCiclo.I) {
			erros.add("Meta " + meta.getCodigo() + " n\u00E3o pode ser validada, o ciclo deve estar com a fase de validacao iniciada.");
		}*/

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}
}
