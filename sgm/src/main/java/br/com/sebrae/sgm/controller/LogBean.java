package br.com.sebrae.sgm.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.enums.StatusConfiguracao;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.CicloService;
import br.com.sebrae.sgm.service.LogService;
import br.com.sebrae.sgm.utils.FacesUtil;

@ConversationScoped
@Named
public class LogBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(LogBean.class);

	@Inject
	private CicloService cicloService;

	@Inject
	private AppBean appBean;

	@Inject
	private LogService logService;

	private TipoLog tipoLog;
	private TipoMeta tipoMeta;

	private String codigoMeta;
	private String cpf;

	private Ciclo cicloSelecionado;

	private List<Ciclo> ciclos;

	private List<Object[]> registrosLog;

	private String textoBuscaRapida;

	public TipoLog getTipoLog() {
		return tipoLog;
	}

	public void setTipoLog(TipoLog tipoLog) {
		this.tipoLog = tipoLog;
	}

	public TipoMeta getTipoMeta() {
		return tipoMeta;
	}

	public void setTipoMeta(TipoMeta tipoMeta) {
		this.tipoMeta = tipoMeta;
	}

	public List<TipoLog> getTiposLog() {
		return Arrays.asList(TipoLog.values());
	}

	public List<TipoMeta> getTiposMeta() {
		return Arrays.asList(TipoMeta.values());
	}

	public Ciclo getCicloSelecionado() {
		return cicloSelecionado;
	}

	public void setCicloSelecionado(Ciclo cicloSelecionado) {
		this.cicloSelecionado = cicloSelecionado;
	}

	public List<Ciclo> getCiclos() {
		if (ciclos == null || ciclos.isEmpty()) {
			ciclos = this.cicloService.findIniciadosByUf(appBean.getUfSelecionada());
		}
		return ciclos;
	}

	public void setCiclos(List<Ciclo> ciclos) {
		this.ciclos = ciclos;
	}

	public String getCodigoMeta() {
		return codigoMeta;
	}

	public void setCodigoMeta(String codigoMeta) {
		this.codigoMeta = codigoMeta;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void pesquisar() {
		try {
			registrosLog = logService.findHistoricoMeta(tipoLog, tipoMeta, codigoMeta, this.cpf, this.cicloSelecionado);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao pesquisar registros de log, tente novamente mais tarde.");
		}
	}

	public List<Object[]> getRegistrosLog() {
		return registrosLog;
	}

	public void setRegistrosLog(List<Object[]> registrosLog) {
		this.registrosLog = registrosLog;
	}

	public String getTextoBuscaRapida() {
		return textoBuscaRapida;
	}

	public void setTextoBuscaRapida(String textoBuscaRapida) {
		this.textoBuscaRapida = textoBuscaRapida;
	}

}
