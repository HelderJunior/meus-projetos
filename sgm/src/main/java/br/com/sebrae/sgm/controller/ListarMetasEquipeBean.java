package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.enums.TipoFiltro;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ListarMetasEquipeBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ListarMetasEquipeBean.class);

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private MetaService metaService;

	@Inject
	private AppBean appBean;

	private List<Unidade> unidades;

	private Unidade unidadeSelecionada;

	private List<Meta> metas = new ArrayList<Meta>();

	private Meta metaSelecionada;

	private TipoFiltro tipoFiltro = TipoFiltro.L;

	private Ciclo ciclo;

	private StatusMeta status;

	@Override
	public void init() {
		super.init();
		atualizarListagem();
	}

	public void atualizarListagem() {
		if (tipoFiltro == TipoFiltro.L) {
			if (unidadeSelecionada == null) {
				if (appBean.getUsuarioAutenticado().getUnidades() != null
						&& !appBean.getUsuarioAutenticado().getUnidades().isEmpty()) {
					metas = metaService.findAllEquipeCiclo(appBean.getUsuarioAutenticado().getUnidadeAtual(),
							appBean.getCicloSelecionado());
				}
			} else {
				metas = metaService.findAllEquipeCiclo(unidadeSelecionada, appBean.getCicloSelecionado());
			}
		} else if (tipoFiltro == TipoFiltro.C) {
			if (unidadeSelecionada == null) {
				if (appBean.getUsuarioAutenticado().getUnidades() != null
						&& !appBean.getUsuarioAutenticado().getUnidades().isEmpty()) {

					metas = metaService.findAllEquipeCiclo(appBean.getUsuarioAutenticado().getUnidadeAtual(), getCiclo());
				}
			} else {
				metas = metaService.findAllEquipeCiclo(unidadeSelecionada, getCiclo());
			}
		} else if (tipoFiltro == TipoFiltro.S) {
			if (unidadeSelecionada == null) {
				if (appBean.getUsuarioAutenticado().getUnidades() != null
						&& !appBean.getUsuarioAutenticado().getUnidades().isEmpty()) {
					metas = metaService.findAllEquipeCicloStatus(appBean.getUsuarioAutenticado().getUnidadeAtual(),
							getCiclo(), this.status);
				}
			} else {
				metas = metaService.findAllEquipeCicloStatus(unidadeSelecionada, getCiclo(), this.status);
			}
		}
	}

	public void excluir() {
		try {
			this.metaService.delete(this.metaSelecionada);
			atualizarListagem();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Unidade> getUnidades() {
		if (unidades == null || unidades.isEmpty()) {
			unidades = unidadeService.findByUf(appBean.getUfSelecionada());
		}
		return unidades;
	}

	public void alterouUnidade() {
		try {
			atualizarListagem();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public Unidade getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(Unidade unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public Meta getMetaSelecionada() {
		return metaSelecionada;
	}

	public void setMetaSelecionada(Meta metaSelecionada) {
		this.metaSelecionada = metaSelecionada;
	}

	public TipoFiltro getTipoFiltro() {
		return tipoFiltro;
	}

	public void setTipoFiltro(TipoFiltro tipoFiltro) {
		this.tipoFiltro = tipoFiltro;
	}

	public Ciclo getCiclo() {
		if(ciclo == null){
			ciclo = appBean.getCicloSelecionado();
		}
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public StatusMeta getStatus() {
		return status;
	}

	public void setStatus(StatusMeta status) {
		this.status = status;
	}

}
