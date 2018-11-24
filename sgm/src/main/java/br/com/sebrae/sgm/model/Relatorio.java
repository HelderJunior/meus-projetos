package br.com.sebrae.sgm.model;

import java.io.Serializable;

public class Relatorio implements Serializable {

	/**
	 * @author Bruno
	 */
	private static final long serialVersionUID = 1L;

	private String tipoRelatorio;
	private String ciclo;
	private String unidade;
	private String nomeColaborador;
	private String categoria;
	private String formaAquisicao;
	private String solucaoEducacional;
	private String tipoCargaHoraria;
	private String fase;
	private String statusMeta;

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getCiclo() {
		return ciclo;
	}

	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getNomeColaborador() {
		return nomeColaborador;
	}

	public void setNomeColaborador(String nomeColaborador) {
		this.nomeColaborador = nomeColaborador;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getFormaAquisicao() {
		return formaAquisicao;
	}

	public void setFormaAquisicao(String formaAquisicao) {
		this.formaAquisicao = formaAquisicao;
	}

	public String getSolucaoEducacional() {
		return solucaoEducacional;
	}

	public void setSolucaoEducacional(String solucaoEducacional) {
		this.solucaoEducacional = solucaoEducacional;
	}

	public String getTipoCargaHoraria() {
		return tipoCargaHoraria;
	}

	public void setTipoCargaHoraria(String tipoCargaHoraria) {
		this.tipoCargaHoraria = tipoCargaHoraria;
	}

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getStatusMeta() {
		return statusMeta;
	}

	public void setStatusMeta(String statusMeta) {
		this.statusMeta = statusMeta;
	}

}
