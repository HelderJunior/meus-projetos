package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.GerenciarMetaDTO;
import br.com.sebrae.sgm.controller.enums.AcaoComboGestao;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class GestorGestaoMetasDesempenhoNomeBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(GestorGestaoMetasDesempenhoNomeBean.class);

	@Inject
	private AppBean appBean;

	@Inject
	private GestaoMetasBean gestaoMetasBean;

	@Inject
	private MetaService metaService;

	private GerenciarMetaDTO gerenciaMetaDto;
	private List<Meta> metas = new ArrayList<Meta>();

	private Boolean selecaoTodasMetas = Boolean.FALSE;
	private String tipoSelecao;

	private AcaoComboGestao acao;

	private String textoObservacao;


	@Override
	public void init() {
		super.init();
		metas = new ArrayList<Meta>();
		if (gerenciaMetaDto != null) {
			if ("N".equals(tipoSelecao)) {
				if (this.gerenciaMetaDto.getMetasAprovadas() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasAprovadas()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasGravadas() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasGravadas()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasCanceladas() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasCanceladas()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasPendenteAprovacao() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasPendenteAprovacao()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasPendenteCancelamento() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasPendenteCancelamento()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasObservacaoSuperior() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasObservacaoSuperior()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}

				if (this.gerenciaMetaDto.getMetasObservacaoComite() != null) {
					for (Meta m : this.gerenciaMetaDto.getMetasObservacaoComite()) {
						// if (m.getStatusAtual() != StatusMeta.OS) {
						metas.add(m);
						// }
						m.setSelecionado(Boolean.FALSE);
					}
				}
			}
		}
	}


	public boolean isPossuiItemSelecionado() {
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta m : this.metas) {
				if (m.getSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<AcaoComboGestao> getAcoesComboGestao() {
		List<AcaoComboGestao> retorno = new ArrayList<AcaoComboGestao>();
		retorno.add(AcaoComboGestao.AM);
		retorno.add(AcaoComboGestao.CM);
		retorno.add(AcaoComboGestao.EO);
		return retorno;
	}

	public void enviarObservacao() {
		try {
			List<Meta> metasEnviadoObservacao = new ArrayList<Meta>();
			for (Meta m : this.metas) {
				try {
					if (m.getSelecionado()) {
						metaService.enviarObservacaoSuperiorMeta(m, getTextoObservacao());
						metasEnviadoObservacao.add(m);
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			this.textoObservacao = null;
			gestaoMetasBean.resetCampos();
			this.metas.removeAll(metasEnviadoObservacao);
			FacesUtil.addInfoMessage(metasEnviadoObservacao.size() + " metas envidas observa\u00E7\u00E3o com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void aprovarMetas() {
		try {
			List<Meta> metasAprovadas = new ArrayList<Meta>();
			for (Meta m : this.metas) {
				try {
					if (m.getSelecionado()) {
						if (m.getTipo() == TipoMeta.I)
							metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
						if (m.getTipo() == TipoMeta.E)
							metaService.aprovarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
						metasAprovadas.add(m);
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			if (metasAprovadas.size() > 0) {
				this.metas.removeAll(metasAprovadas);
				gestaoMetasBean.resetCampos();
				FacesUtil.addInfoMessage(metasAprovadas.size() + " metas aprovadas com sucesso!");
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void cancelarMetas() {
		try {
			List<Meta> metasCanceladas = new ArrayList<Meta>();
			for (Meta m : this.metas) {
				try {
					if (m.getSelecionado()) {
						if (m.getTipo() == TipoMeta.I)
							metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.C);
						if (m.getTipo() == TipoMeta.E)
							metaService.cancelarMeta(m, TipoConfiguracaoCiclo.DESEMP, TipoGrupo.E);
						metasCanceladas.add(m);
					}
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			gestaoMetasBean.resetCampos();
			this.metas.removeAll(metasCanceladas);
			if (!metasCanceladas.isEmpty())
				FacesUtil.addInfoMessage(metasCanceladas.size() + " metas canceladas com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterouSelecaoTodos() {
		if (this.getSelecaoTodasMetas()) {
			for (Meta m : this.metas) {
				m.setSelecionado(Boolean.TRUE);
			}
		} else {
			for (Meta m : this.metas) {
				m.setSelecionado(Boolean.FALSE);
			}
		}

	}

	public void removerMeta(Meta meta) {
		if (this.metas != null) {
			this.metas.remove(meta);
		}
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public GerenciarMetaDTO getGerenciaMetaDto() {
		return gerenciaMetaDto;
	}

	public void setGerenciaMetaDto(GerenciarMetaDTO gerenciaMetaDto) {
		this.gerenciaMetaDto = gerenciaMetaDto;
	}

	public Boolean getSelecaoTodasMetas() {
		return selecaoTodasMetas;
	}

	public void setSelecaoTodasMetas(Boolean selecaoTodasMetas) {
		this.selecaoTodasMetas = selecaoTodasMetas;
	}

	public String getTipoSelecao() {
		return tipoSelecao;
	}

	public void setTipoSelecao(String tipoSelecao) {
		this.tipoSelecao = tipoSelecao;
	}

	public AcaoComboGestao getAcao() {
		return acao;
	}

	public void setAcao(AcaoComboGestao acao) {
		this.acao = acao;
	}

	public String getTextoObservacao() {
		return textoObservacao;
	}

	public void setTextoObservacao(String textoObservacao) {
		this.textoObservacao = textoObservacao;
	}

}
