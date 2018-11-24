package br.com.sebrae.sgm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Exportacao;
import br.com.sebrae.sgm.model.ExportacaoDetalhe;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.service.CicloConfiguracaoService;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class ListarCicloBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ListarCicloBean.class);

	@Inject
	private CicloService cicloService;

	@Inject
	private CicloConfiguracaoService cicloConfiguracaoService;

	@Inject
	private AppBean appBean;

	private List<Ciclo> ciclos = new ArrayList<Ciclo>();

	private Ciclo cicloSelecionado;

	private CicloConfiguracao cicloConfiguracaoSel;

	private Exportacao exporatacaoSelecionada;

	private List<Exportacao> exportacoesCiclo;

	@Override
	public void init() {
		super.init();
		atualizarListagem();
	}

	private void atualizarListagem() {
		ciclos = cicloService.findAllAndamento();
	}

	public void excluir() {
		try {
			cicloSelecionado = cicloService.load(cicloSelecionado.getId());
			this.cicloService.delete(this.cicloSelecionado);
			atualizarListagem();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Ciclo n\u00E3o pode ser exclu\u00EDdo, pois possui registros associados a ele.");
		}
	}

	public void iniciarPactuacao() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());
			cicloConfiguracaoService.iniciarPactuacao(cicloConfiguracaoSel);
			Ciclo c = cicloConfiguracaoSel.getCiclo();
			c.setStatus(StatusCiclo.I);
			cicloService.save(c);
			FacesUtil.addInfoMessage("Fase de pactua\u00E7\u00E3o iniciada com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void iniciarRepactuacao() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());
			cicloConfiguracaoService.iniciarRepactuacao(cicloConfiguracaoSel);
			Ciclo c = cicloConfiguracaoSel.getCiclo();
			c.setStatus(StatusCiclo.I);
			cicloService.save(c);
			FacesUtil.addInfoMessage("Fase de repactua\u00E7\u00E3o iniciada com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void iniciarMonitoramentoAjustes() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());
			cicloConfiguracaoService.iniciarMonitoramentoAjustes(cicloConfiguracaoSel);
			Ciclo c = cicloConfiguracaoSel.getCiclo();
			c.setStatus(StatusCiclo.I);
			cicloService.save(c);
			FacesUtil.addInfoMessage("Fase de monitoramento/ajustes iniciada com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void iniciarValidacao() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());
			cicloConfiguracaoService.iniciarValidacao(cicloConfiguracaoSel);
			Ciclo c = cicloConfiguracaoSel.getCiclo();
			c.setStatus(StatusCiclo.I);
			cicloService.save(c);
			FacesUtil.addInfoMessage("Fase de valida\u00E7\u00E3o iniciada com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void iniciarAuditoria() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());
			cicloConfiguracaoService.iniciarAuditoria(cicloConfiguracaoSel);
			Ciclo c = cicloConfiguracaoSel.getCiclo();
			c.setStatus(StatusCiclo.I);
			cicloService.save(c);
			FacesUtil.addInfoMessage("Fase de auditoria iniciada com sucesso!");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void exportarResultados() {
		try {
			cicloConfiguracaoSel = cicloConfiguracaoService.load(cicloConfiguracaoSel.getId());

			List<Meta> metas = cicloConfiguracaoSel.getMetas();

			if (metas == null || metas.isEmpty()) {
				FacesUtil.addErrorMessage("form_dados_exportacao",
						"N\u00E3o existem metas associadas ao ciclo para serem exportadas.");
			} else {
				Exportacao ex = new Exportacao();
				ex.setCicloConfiguracao(this.cicloConfiguracaoSel);
				ex.setDetalhesExportacao(new ArrayList<ExportacaoDetalhe>());
				ex.setUsuarioExportador(appBean.getUsuarioAutenticado());
				for (Meta meta : metas) {
					ExportacaoDetalhe exd = new ExportacaoDetalhe();
					exd.setExportacao(ex);
					exd.setUnidade(meta.getColaborador().getUnidadeAtual());
					exd.setUsuario(meta.getColaborador());
					exd.setMetasEstipuladas(meta.getQuantidadePrevista());
					exd.setMetasAlcancadas(meta.getValorRealizado());
					exd.setAnoVigencia(cicloConfiguracaoSel.getCiclo().getVigencia());
					ex.getDetalhesExportacao().add(exd);
				}

				if (this.cicloConfiguracaoSel.getExportacoes() == null) {
					this.cicloConfiguracaoSel.setExportacoes(new ArrayList<Exportacao>());
				}

				this.cicloConfiguracaoSel.getExportacoes().add(ex);
				this.cicloConfiguracaoService.save(this.cicloConfiguracaoSel);
				this.exporatacaoSelecionada = ex;
				FacesUtil.addInfoMessage("form_dados_exportacao", "Dados exportados com sucesso!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void listarExportacoes() {
		try {
			this.exportacoesCiclo = this.cicloConfiguracaoSel.getExportacoes();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public List<Ciclo> getCiclos() {
		return ciclos;
	}

	public void setCiclos(List<Ciclo> ciclos) {
		this.ciclos = ciclos;
	}

	public Ciclo getCicloSelecionado() {
		return cicloSelecionado;
	}

	public void setCicloSelecionado(Ciclo cicloSelecionado) {
		this.cicloSelecionado = cicloSelecionado;
	}

	public CicloConfiguracao getCicloConfiguracaoSel() {
		return cicloConfiguracaoSel;
	}

	public void setCicloConfiguracaoSel(CicloConfiguracao cicloConfiguracaoSel) {
		this.cicloConfiguracaoSel = cicloConfiguracaoSel;
	}

	public Exportacao getExporatacaoSelecionada() {
		return exporatacaoSelecionada;
	}

	public void setExporatacaoSelecionada(Exportacao exporatacaoSelecionada) {
		this.exporatacaoSelecionada = exporatacaoSelecionada;
	}

	public List<Exportacao> getExportacoesCiclo() {
		return exportacoesCiclo;
	}

	public void setExportacoesCiclo(List<Exportacao> exportacoesCiclo) {
		this.exportacoesCiclo = exportacoesCiclo;
	}

}
