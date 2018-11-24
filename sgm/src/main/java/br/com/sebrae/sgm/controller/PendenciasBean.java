package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.enums.AcaoComboPendencia;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioPerfilService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@ConversationScoped
@Named
public class PendenciasBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(PendenciasBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AppBean appBean;

	private List<Meta> metas;

	private Meta metaSelecionada;

	private List<Usuario> colaboradores;
	private Usuario colaborador;

	private List<Unidade> unidades;
	private Unidade unidade;

	private StatusMeta statusMetaSelecionado;
	private TipoMeta tipoMetaSelecionado;

	private AcaoComboPendencia acaoPendencia;

	@Inject
	private UsuarioPerfilService usuarioPerfilService;
	

	public List<Meta> getMetas() {
		if (metas == null || metas.isEmpty()) {
			metas = metaService.findPendentesColaborador(this.unidade, this.colaborador, appBean.getCicloSelecionado(),
					statusMetaSelecionado, tipoMetaSelecionado);
			for (Meta m : metas) {
				m.setSelecionado(false);
			}
		}
		return metas;
	}
	
	public List<Meta> getFiltrarPedencias() {

		// Caso seja Colaborador ou Administrador Master
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.COLABORADOR_CHAVE.toString())
				|| appBean.getPerfilSelecionado().getChave().equals(Perfil.ADM_MASTER.toString())) {
			metas = metaService.findPendentesColaborador(this.unidade, appBean.getUsuarioAutenticado(), colaborador, appBean.getCicloSelecionado(),
					statusMetaSelecionado, tipoMetaSelecionado);
		}

		// Caso seja Gerente
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE.toString())) {
			List<String> unidades = usuarioPerfilService.findByUnidadesGerenciaveis(appBean.getUsuarioAutenticado());
			unidades.add(appBean.getUsuarioAutenticado().getUnidade().getUnidadePK().getCodigo());
			metas = metaService.findPendentesGerente(this.unidade, appBean.getUsuarioAutenticado(), colaborador, appBean.getCicloSelecionado(),
					statusMetaSelecionado, tipoMetaSelecionado, unidades);
		}

		if (metas == null || metas.isEmpty()) {
			for (Meta m : metas) {
				m.setSelecionado(false);
			}
		}

		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public Meta getMetaSelecionada() {
		return metaSelecionada;
	}

	public void setMetaSelecionada(Meta metaSelecionada) {
		this.metaSelecionada = metaSelecionada;
	}

	public AcaoComboPendencia getAcaoPendencia() {
		return acaoPendencia;
	}

	public void setAcaoPendencia(AcaoComboPendencia acaoPendencia) {
		this.acaoPendencia = acaoPendencia;
	}

	public List<AcaoComboPendencia> getAcoesComboPendencia() {
		List<AcaoComboPendencia> retorno = new ArrayList<AcaoComboPendencia>();

		if (appBean.getCicloSelecionado() != null) {
			if (isPossuiMetasSelecionadas()) {
				retorno.add(AcaoComboPendencia.EA);
				if (appBean.getCicloSelecionado().getStatusPactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I
						&& !(appBean.getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I))
				{
					retorno.add(AcaoComboPendencia.EX);
					//retorno.add(AcaoComboPendencia.CA);
				}
				if (appBean.getCicloSelecionado().getStatusRepactuacao(TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C) == StatusCiclo.I) {
					retorno.add(AcaoComboPendencia.CA);
				}
			}
		}
		return retorno;
	}

//	private boolean isPossuiMetasSelecionadas() {
//		if (this.getFiltrarPedencias() != null && !this.getFiltrarPedencias().isEmpty()) {
//			for (Meta m : this.metas) {
//				if (m.getSelecionado()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	private boolean isPossuiMetasSelecionadas() {
		if (this.getMetas() != null && !this.getMetas().isEmpty()) {
			for (Meta m : this.metas) {
				if (m.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}
	public void buscarPeloColaboradorUnidadeStatusTipo() {
		try {
			this.metas = metaService.findPendentesColaborador(this.unidade, this.colaborador,
					appBean.getCicloSelecionado(), statusMetaSelecionado, tipoMetaSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirMetasSelecionadas() {
		try {
			List<Meta> metasEcluir = Lists.newArrayList(Collections2.filter(this.metas, new Predicate<Meta>() {
				@Override
				public boolean apply(Meta meta) {
					return meta.getSelecionado();
				}
			}));
			for (Meta meta : metasEcluir) {
				metaService.delete(meta);
			}
			this.metas.removeAll(metasEcluir);
			FacesUtil.addInfoMessage("Meta excluida com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void enviarAprovacaoMetasSelecionadas() {
		try {
			List<Meta> metasSelecionadas = Lists.newArrayList(Collections2.filter(this.metas, new Predicate<Meta>() {
				@Override
				public boolean apply(Meta meta) {
					return meta.getSelecionado();
				}
			}));

			List<Meta> metasSucesso = new ArrayList<Meta>();
			for (Meta meta : metasSelecionadas) {
				try {
					metaService.enviarMetaAprovacao(meta, meta.getCicloConfiguracao().getTipoConfiguracao(), TipoGrupo.C);
					metasSucesso.add(meta);
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			if (metasSucesso.size() > 0) {
				this.metas.removeAll(metasSucesso);
				FacesUtil.addInfoMessage("Metas enviadas para aprova\u00E7\u00E3o com sucesso.");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void removerMeta(Meta m) {
		if (this.metas != null)
			this.metas.remove(m);
	}

	public void cancelarMetasSelecionadas() {
		try {
			List<Meta> metasSelecionadas = Lists.newArrayList(Collections2.filter(this.metas, new Predicate<Meta>() {
				@Override
				public boolean apply(Meta meta) {
					return meta.getSelecionado();
				}
			}));

			for (Meta meta : metasSelecionadas) {
				if (meta.getStatusByFase(Fase.R) != null) {
					meta.getMetaStatusByFase(Fase.R).setStatus(StatusMeta.CS);
				} else {
					meta.getMetaStatusByFase(Fase.P).setStatus(StatusMeta.CS);
				}
				metaService.save(meta);
			}
			this.metas.removeAll(metasSelecionadas);
			FacesUtil.addInfoMessage("Metas canceladas com sucesso.");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<StatusMeta> getStatusMeta() {
		List<StatusMeta> statusRetorno = new ArrayList<StatusMeta>();

		//statusRetorno.add(StatusMeta.SA);
		statusRetorno.add(StatusMeta.OS);
		statusRetorno.add(StatusMeta.OC);
		statusRetorno.add(StatusMeta.OA);

		return statusRetorno;
	}

	public List<Usuario> getColaboradores() {
		if (colaboradores == null || colaboradores.isEmpty()) {
			this.colaboradores = usuarioService.findAllInternos();
		}
		return colaboradores;
	}

	public void setColaboradores(List<Usuario> colaboradores) {
		this.colaboradores = colaboradores;
	}

	public List<Unidade> getUnidades() {

		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findAll();
		}

		return unidades;
	}
	
	public boolean isPerfilSlelecionado() {
		if (appBean.getPerfilSelecionado().getChave().equals(Perfil.GERENTE.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public List<TipoMeta> getTiposMeta() {
		return Arrays.asList(TipoMeta.values());
	}

	public Usuario getColaborador() {
		return colaborador;
	}

	public void setColaborador(Usuario colaborador) {
		this.colaborador = colaborador;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public StatusMeta getStatusMetaSelecionado() {
		return statusMetaSelecionado;
	}

	public void setStatusMetaSelecionado(StatusMeta statusMetaSelecionado) {
		this.statusMetaSelecionado = statusMetaSelecionado;
	}

	public TipoMeta getTipoMetaSelecionado() {
		return tipoMetaSelecionado;
	}

	public void setTipoMetaSelecionado(TipoMeta tipoMetaSelecionado) {
		this.tipoMetaSelecionado = tipoMetaSelecionado;
	}

}
