package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoComite;
import br.com.sebrae.sgm.model.ItemAvaliadoComite;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioConfiguracaoComite;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.PerfilService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarAvaliacaoComiteDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarAvaliacaoComiteDesenvolvimentoBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;
	@Inject
	private UnidadeService unidadeService;
	@Inject
	private UsuarioService usuarioService;
	@Inject
	private PerfilService perfilService;
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private ConfiguracaoComite configuracaoComite;

	private List<Usuario> usuariosComite;
	private List<Unidade> unidades;

	private UsuarioConfiguracaoComite usuarioComiteUnidade = new UsuarioConfiguracaoComite();
	private Boolean editandoUsuario = Boolean.FALSE;

	private ItemAvaliadoComite itemAvaliadoComite = new ItemAvaliadoComite();
	private Boolean editandoItemAvaliado = Boolean.FALSE;

	private List<Unidade> unidadesDisponiveis;
	private List<Unidade> unidadesSelecionadasVincular;
	private Boolean editandoUnidades;
	private Unidade unidadeSelecionada;

	public void inserirUsuarioComite() {
		try {
			validarInsercaoAtualizacao();
			if (this.configuracaoComite.getUsuariosComiteUnidade() == null) {
				this.configuracaoComite.setUsuariosComiteUnidade(new ArrayList<UsuarioConfiguracaoComite>());
			}
			this.configuracaoComite.getUsuariosComiteUnidade().add(this.usuarioComiteUnidade);
			this.usuarioComiteUnidade.setConfiguracaoComite(this.configuracaoComite);
			this.configuracaoComite.setCicloConfiguracao(this.cicloConfiguracao);
			this.configuracaoComite.setId(this.cicloConfiguracao.getId());
			this.cicloConfiguracao.setConfiguracaoComite(this.configuracaoComite);
			this.usuarioComiteUnidade = new UsuarioConfiguracaoComite();
			editandoUsuario = false;
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void atualizarUsuarioComite() {
		try {
			validarInsercaoAtualizacao();
			this.usuarioComiteUnidade = new UsuarioConfiguracaoComite();
			editandoUsuario = false;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void validarInsercaoAtualizacao() throws ValidateException {
		List<String> erros = new ArrayList<String>();

		if (this.usuarioComiteUnidade.getUnidades() == null || this.usuarioComiteUnidade.getUnidades().isEmpty()) {
			erros.add("Informe no m\u00EDnimo uma unidade para o par\u00E2metro");
		}

		if (!erros.isEmpty()) {
			throw new ValidateException(erros);
		}
	}

	public void editarUsuarioComite(UsuarioConfiguracaoComite ucu) {
		try {
			this.usuarioComiteUnidade = ucu;
			this.editandoUsuario = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirUsuarioComite() {
		try {
			this.configuracaoComite.getUsuariosComiteUnidade().remove(this.usuarioComiteUnidade);
			this.usuarioComiteUnidade = new UsuarioConfiguracaoComite();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void inserirItemComite() {
		try {
			if (this.configuracaoComite.getItemsAvaliados() == null) {
				this.configuracaoComite.setItemsAvaliados(new ArrayList<ItemAvaliadoComite>());
			}

			if (this.configuracaoComite.getItemsAvaliados().size() >= 10) {
				FacesUtil
						.addErrorMessage("N\u00E3o \u00E9 permitido mais de 10 items para avalia\u00E7\u00E3o do comit\u00EA.");
				return;
			}

			this.itemAvaliadoComite.setConfiguracaoComite(this.configuracaoComite);
			this.configuracaoComite.getItemsAvaliados().add(this.itemAvaliadoComite);
			this.usuarioComiteUnidade.setConfiguracaoComite(this.configuracaoComite);
			this.configuracaoComite.setCicloConfiguracao(this.cicloConfiguracao);
			this.configuracaoComite.setId(this.cicloConfiguracao.getId());
			this.cicloConfiguracao.setConfiguracaoComite(this.configuracaoComite);
			this.itemAvaliadoComite = new ItemAvaliadoComite();
			editandoItemAvaliado = false;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void atualizarItemComite() {
		try {
			this.itemAvaliadoComite = new ItemAvaliadoComite();
			editandoItemAvaliado = false;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editarItemComite(ItemAvaliadoComite ucu) {
		try {
			this.itemAvaliadoComite = ucu;
			this.editandoItemAvaliado = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirItemComite() {
		try {
			this.configuracaoComite.getItemsAvaliados().remove(this.itemAvaliadoComite);
			this.itemAvaliadoComite = new ItemAvaliadoComite();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public String salvar() {
		try {
			verificarStatus();
			this.cicloConfiguracaoService.save(this.cicloConfiguracao);
			return "/pages/ciclo/manter.xhtml";
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return "";
	}

	private void verificarStatus() {
		if (this.cicloConfiguracao.getConfiguracaoComite() != null) {
			this.cicloConfiguracao.setStatusConfiguracaoComite(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoComite(StatusConfiguracao.A);
		}
		
		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}
	}

	public CicloConfiguracao getCicloConfiguracao() {
		return cicloConfiguracao;
	}

	public void setCicloConfiguracao(CicloConfiguracao cicloConfiguracao) {
		this.cicloConfiguracao = cicloConfiguracao;
	}

	public Boolean getVisualizando() {
		return visualizando;
	}

	public void setVisualizando(Boolean visualizando) {
		this.visualizando = visualizando;
	}

	public ConfiguracaoComite getConfiguracaoComite() {
		if (this.cicloConfiguracao.getConfiguracaoComite() != null) {
			this.configuracaoComite = this.cicloConfiguracao.getConfiguracaoComite();
		}

		if (this.configuracaoComite == null) {
			this.configuracaoComite = new ConfiguracaoComite();
		}

		return configuracaoComite;
	}

	public void setConfiguracaoComite(ConfiguracaoComite configuracaoComite) {
		this.configuracaoComite = configuracaoComite;
	}

	public List<Usuario> getUsuariosComite() {
		if (this.usuariosComite == null) {
			Perfil perfilComite = perfilService.load(Perfil.COMITE);
			this.usuariosComite = this.usuarioService.findByPerfil(perfilComite);
		}
		return this.usuariosComite;
	}

	public List<Unidade> getUnidades() {
		if (this.unidades == null) {
			unidades = this.unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
		}
		return unidades;
	}

	public List<Unidade> completeUnidade(String query) {
		List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo()
				.getUf());
		return retorno;
	}

	public void prepararModalVincularUnidades() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = new ArrayList<Unidade>();
			editandoUnidades = false;
			if (getUsuarioComiteUnidade().getUnidades() != null) {
				unidadesDisponiveis.removeAll(getUsuarioComiteUnidade().getUnidades());
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void prepararModalVincularUnidadesEditar() {
		try {
			unidadesDisponiveis = unidadeService.findByUf(this.cicloConfiguracao.getCiclo().getUf());
			unidadesSelecionadasVincular = getUsuarioComiteUnidade().getUnidades();
			editandoUnidades = true;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void desvincularUnidade() {
		try {
			getUsuarioComiteUnidade().getUnidades().remove(this.unidadeSelecionada);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void vincularUnidades() {
		try {
			if (editandoUnidades) {
				this.getUsuarioComiteUnidade().setUnidades(unidadesSelecionadasVincular);
			} else {
				if (this.getUsuarioComiteUnidade().getUnidades() == null) {
					this.getUsuarioComiteUnidade().setUnidades(new ArrayList<Unidade>());
				}
				List<Unidade> unidadesVinculadosTemp = this.getUsuarioComiteUnidade().getUnidades();
				unidadesVinculadosTemp.addAll(this.unidadesSelecionadasVincular);

				Collections.sort(unidadesVinculadosTemp, new Comparator<Unidade>() {
					@Override
					public int compare(Unidade o1, Unidade o2) {
						return o1.getDescricao().compareTo(o2.getDescricao());
					}
				});
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage(e.getMessage());
		}
	}

	public UsuarioConfiguracaoComite getUsuarioComiteUnidade() {
		return usuarioComiteUnidade;
	}

	public void setUsuarioComiteUnidade(UsuarioConfiguracaoComite usuarioComiteUnidade) {
		this.usuarioComiteUnidade = usuarioComiteUnidade;
	}

	public ItemAvaliadoComite getItemAvaliadoComite() {
		return itemAvaliadoComite;
	}

	public void setItemAvaliadoComite(ItemAvaliadoComite itemAvaliadoComite) {
		this.itemAvaliadoComite = itemAvaliadoComite;
	}

	public Boolean getEditandoUsuario() {
		return editandoUsuario;
	}

	public void setEditandoUsuario(Boolean editandoUsuario) {
		this.editandoUsuario = editandoUsuario;
	}

	public Boolean getEditandoItemAvaliado() {
		return editandoItemAvaliado;
	}

	public void setEditandoItemAvaliado(Boolean editandoItemAvaliado) {
		this.editandoItemAvaliado = editandoItemAvaliado;
	}

	public List<Unidade> getUnidadesDisponiveis() {
		return unidadesDisponiveis;
	}

	public void setUnidadesDisponiveis(List<Unidade> unidadesDisponiveis) {
		this.unidadesDisponiveis = unidadesDisponiveis;
	}

	public List<Unidade> getUnidadesSelecionadasVincular() {
		return unidadesSelecionadasVincular;
	}

	public void setUnidadesSelecionadasVincular(List<Unidade> unidadesSelecionadasVincular) {
		this.unidadesSelecionadasVincular = unidadesSelecionadasVincular;
	}

	public Boolean getEditandoUnidades() {
		return editandoUnidades;
	}

	public void setEditandoUnidades(Boolean editandoUnidades) {
		this.editandoUnidades = editandoUnidades;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public void setUsuariosComite(List<Usuario> usuariosComite) {
		this.usuariosComite = usuariosComite;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

}
