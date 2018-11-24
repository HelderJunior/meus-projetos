package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMarcoCritico;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.UnidadeService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class CicloConfigurarMarcosCriticosBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(CicloConfigurarMarcosCriticosBean.class);

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private UnidadeService unidadeService;
	
	@Inject
	private CicloService cicloService;

	private CicloConfiguracao cicloConfiguracao;

	private Boolean visualizando = Boolean.FALSE;

	private ConfiguracaoMarcoCritico configuracaoMarcoCritico;

	private List<Unidade> unidades;

	@Override
	public void init() {
		super.init();
	}

	public void salvar() {
		try {
			if (this.cicloConfiguracao.getConfiguracoesMarcoCritico() == null) {
				this.cicloConfiguracao.setConfiguracoesMarcoCritico(new ArrayList<ConfiguracaoMarcoCritico>());
			}
			this.cicloConfiguracao.getConfiguracoesMarcoCritico().add(this.configuracaoMarcoCritico);
			this.cicloConfiguracao.setStatusConfiguracaoMarcosCriticos(StatusConfiguracao.C);
			this.configuracaoMarcoCritico.setCicloConfiguracao(this.cicloConfiguracao);

			verificaStatus();

			
			this.configuracaoMarcoCritico = new ConfiguracaoMarcoCritico();
			FacesUtil.addInfoMessage("Configura\u00E7\u00E3o salva com sucesso!");
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void alterarParametro() {
		try {
			this.cicloConfiguracaoService.save(cicloConfiguracao);
			this.configuracaoMarcoCritico = new ConfiguracaoMarcoCritico();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void editar(ConfiguracaoMarcoCritico cfg) {
		try {
			this.configuracaoMarcoCritico = cfg;
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluir() {
		try {
			this.cicloConfiguracao.getConfiguracoesMarcoCritico().remove(this.configuracaoMarcoCritico);
			verificaStatus();
			cicloConfiguracaoService.save(this.cicloConfiguracao);
			this.configuracaoMarcoCritico = new ConfiguracaoMarcoCritico();
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	private void verificaStatus() {
		if (this.cicloConfiguracao.getConfiguracoesMarcoCritico() != null
				&& !this.cicloConfiguracao.getConfiguracoesMarcoCritico().isEmpty()) {
			this.cicloConfiguracao.setStatusConfiguracaoMarcosCriticos(StatusConfiguracao.C);
		} else {
			this.cicloConfiguracao.setStatusConfiguracaoMarcosCriticos(StatusConfiguracao.A);
		}
		
		try {
			cicloConfiguracaoService.save(cicloConfiguracao);
			cicloService.save(cicloConfiguracao.getCiclo());
		} catch (Exception e) {
			// do nothing
		}
	}

	public void selecionouSomenteSuasMetas() {
		if (this.cicloConfiguracao.getVisualizacaoMeta().getSomenteSuasMetas()) {
			this.cicloConfiguracao.getVisualizacaoMeta().setDemaisUnidades(Boolean.FALSE);
		}
	}

	public void selecionouDemaisUnidades() {
		if (this.cicloConfiguracao.getVisualizacaoMeta().getDemaisUnidades()) {
			this.cicloConfiguracao.getVisualizacaoMeta().setSomenteSuasMetas(Boolean.FALSE);
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

	public ConfiguracaoMarcoCritico getConfiguracaoMarcoCritico() {
		if (this.configuracaoMarcoCritico == null) {
			this.configuracaoMarcoCritico = new ConfiguracaoMarcoCritico();
		}
		return configuracaoMarcoCritico;
	}

	public void setConfiguracaoMarcoCritico(ConfiguracaoMarcoCritico configuracaoMarcoCritico) {
		this.configuracaoMarcoCritico = configuracaoMarcoCritico;
	}

	public List<Unidade> getUnidades() {
		if (unidades == null) {
			unidades = unidadeService.findByUf(cicloConfiguracao.getCiclo().getUf());
		}
		return unidades;
	}
	
	public List<Unidade> completeUnidade(String query) {
        List<Unidade> retorno = unidadeService.findByDescricaoOrCodigoAndUF(query, this.cicloConfiguracao.getCiclo().getUf());
        return retorno;
    }

	public void setUnidades(List<Unidade> unidades) {
		this.unidades = unidades;
	}

	public List<TipoMeta> getTiposMeta() {
		return Arrays.asList(new TipoMeta[] { TipoMeta.E, TipoMeta.I });
	}

}
