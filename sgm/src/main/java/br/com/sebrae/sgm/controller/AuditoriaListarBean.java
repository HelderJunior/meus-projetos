package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class AuditoriaListarBean extends BaseBean {

	@Inject
	private CicloService cicloService;

	@Inject
	private UnidadeService unidadeService;

	@Inject
	private AppBean appBean;

	@Inject
	private MetaService metaService;

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(AuditoriaListarBean.class);

	private Fase faseCiclo;
	private Ciclo ciclo;
	private Unidade unidade;

	private List<Ciclo> ciclos;
	private List<Unidade> unidades;

	private List<Meta> metas;

	@Override
	public void init() {
		super.init();
	}

	public List<Fase> getFasesCicloExistentes() {
		List<Fase> retorno = new ArrayList<Fase>();
		retorno.add(Fase.P);
		retorno.add(Fase.R);
		retorno.add(Fase.V);
		retorno.add(Fase.M);
		return retorno;
	}

	public void setCiclos(List<Ciclo> ciclos) {
		this.ciclos = ciclos;
	}

	public List<Ciclo> getCiclos() {
		if (ciclos == null || ciclos.isEmpty()) {
			ciclos = this.cicloService.findIniciadosByUf(appBean.getUfSelecionada());
		}
		return ciclos;
	}

	public Fase getFaseCiclo() {
		return faseCiclo;
	}

	public void setFaseCiclo(Fase faseCiclo) {
		this.faseCiclo = faseCiclo;
	}

	public Ciclo getCiclo() {
		return ciclo;
	}

	public void setCiclo(Ciclo ciclo) {
		this.ciclo = ciclo;
	}

	public List<Meta> getMetas() {
		return metas;
	}

	public void setMetas(List<Meta> metas) {
		this.metas = metas;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public List<Unidade> completeUnidade(String desc) {
		List<Unidade> retorno = unidadeService.findByDescricao(desc);
		return retorno;
	}

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public void buscar() {
		try {
			metas = metaService.findMetasAuditor(ciclo, this.faseCiclo, unidade);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}
	
	public void removerDaLista(Meta meta){
		if(this.metas != null){
			this.metas.remove(meta);
		}
	}

}
