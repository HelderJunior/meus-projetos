package br.com.sebrae.sgm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.enums.TipoFiltro;
import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Anexo;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMetas;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.service.MetaService;
import br.com.sebrae.sgm.service.SolucaoEducacionalMetaService;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.FacesUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

@ConversationScoped
@Named
public class ListarMetasDesenvolvimentoBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory
			.getLogger(ListarMetasDesenvolvimentoBean.class);

	@Inject
	private MetaService metaService;

	@Inject
	private SolucaoEducacionalMetaService solucaoEducacionalService;

	@Inject
	private AppBean appBean;

	private List<Meta> metas = new ArrayList<Meta>();

	private List<Meta> metasEnviarAprovacao = new ArrayList<Meta>();

	private Meta metaSelecionada;

	private SolucaoEducacionalMeta solucaoSelecionada;

	private TipoFiltro tipoFiltro = TipoFiltro.L;

	private Ciclo ciclo;

	private StatusMeta status;

	private int indiceAnexo;

	private Boolean selecaoTodosEnviarAprovacao = Boolean.FALSE;

	@Override
	public void init() {
		super.init();
		atualizarListagem();
	}

	public void atualizarListagem() {
		/*if (metas == null || metas.isEmpty()) {
			metas = metaService.findAllDesenvolvimentoCiclo(
					appBean.getUsuarioAutenticado(),
					appBean.getCicloSelecionado());
		}*/
		if (tipoFiltro == TipoFiltro.S) {
			metas = metaService.findAllDesenvolvimentoCicloAndStatus(appBean.getUsuarioAutenticado(), appBean.getCicloSelecionado(), this.status);
		} else {
			metas = metaService.findAllDesenvolvimentoCiclo(appBean.getUsuarioAutenticado(), appBean.getCicloSelecionado());
		}
	}

	public void excluir() {
		try {
			this.metaService.delete(this.metaSelecionada);
			this.metas.remove(this.metaSelecionada);
			atualizarListagem();
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void uploadArquivo(FileUploadEvent event) {
		try {
			String fileName = FilenameUtils.getName(event.getFile()
					.getFileName());
			if (this.getSolucaoSelecionada() == null) {
				this.getSolucaoSelecionada().setAnexos(new ArrayList<Anexo>());
			}
			Anexo an = new Anexo();
			an.setNome(RandomStringUtils.random(8, true, true) + fileName);
			an.setNomeExibicao(fileName);
			byte[] fileBytes = IOUtils.toByteArray(event.getFile()
					.getInputstream());
			an.setTipo(event.getFile().getContentType());
			an.setArquivo(fileBytes);
			FileUtils.writeByteArrayToFile(new File(Constants.FILES_TMP_DIR
					+ File.separator + an.getNome()), fileBytes);
			an.setSolucaoEducacionalMeta(this.solucaoSelecionada);
			this.getSolucaoSelecionada().getAnexos().add(an);
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("form_anexo",
							"Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void excluirArquivo() {
		this.getSolucaoSelecionada().getAnexos().remove(this.indiceAnexo);
	}

	public StreamedContent getArquivo() {
		StreamedContent file = null;
		try {
			Anexo a = this.getSolucaoSelecionada().getAnexos().get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public StreamedContent getArquivo2() {
		StreamedContent file = null;
		try {
			Anexo a = appBean.getCicloConfiguracaoDesenvolvimento()
					.getConfiguracaoFormaAquisicao().getAnexos()
					.get(indiceAnexo);
			if (a.getArquivo() != null) {
				ByteArrayInputStream fis;
				fis = new ByteArrayInputStream(a.getArquivo());
				file = new DefaultStreamedContent(fis, a.getTipo(), a.getNome());
			} else {
				FacesUtil.addErrorMessage("Nenhum arquivo a ser exibido.");
			}
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
		return file;
	}

	public void salvarAnexos() {
		try {
			this.solucaoEducacionalService.atualizar(this.solucaoSelecionada);
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao executar a\u00E7\u00E3o, tente novamente mais tarde.");
			log.error(e.getMessage(), e);
		}
	}

	public void enviarMetaAprovacao() {
		try {
			metaService.enviarMetaAprovacao(this.metaSelecionada,
					TipoConfiguracaoCiclo.DESENV, TipoGrupo.C);
			FacesUtil
					.addInfoMessage("Meta enviada para aprova\u00E7\u00E3o com sucesso.");
		} catch (ValidateException ve) {
			FacesUtil.addErrorMessage(ve.getMsgErrors());
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("Erro ao enviar meta para aprova\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void prepararEnvioAprovacaoTodos() {
		this.selecaoTodosEnviarAprovacao = Boolean.FALSE;

		this.metasEnviarAprovacao = Lists.newArrayList(Collections2.filter(
				this.metas, new Predicate<Meta>() {
					@Override
					public boolean apply(Meta meta) {
						return (meta.getStatusAtual() != StatusMeta.PA && meta
								.getStatusAtual() != StatusMeta.AS);
					}
				}));

		if (this.metasEnviarAprovacao != null
				&& !metasEnviarAprovacao.isEmpty()) {
			for (Meta m : this.metasEnviarAprovacao) {
				m.setSelecionado(Boolean.FALSE);
			}
		} else {
			FacesUtil
					.addErrorMessage("form_enviar_aprovacao",
							"Nenhuma meta dispon\u00EDvel para ser enviada para aprova\u00E7\u00E3o.");
		}
	}

	public void enviarTodasAprovacao() {
		try {
			List<Meta> metasSelecionadas = Lists.newArrayList(Collections2
					.filter(this.metas, new Predicate<Meta>() {
						@Override
						public boolean apply(Meta meta) {
							return meta.getSelecionado();
						}
					}));

			if (metasSelecionadas == null || metasSelecionadas.isEmpty()) {
				FacesUtil
						.addErrorMessage("form_enviar_aprovacao",
								"Selecione no m\u00EDnimo uma meta para enviar para aprova\u00E7\u00E3o.");
				return;
			}

			List<Meta> metasSucesso = new ArrayList<Meta>();
			for (Meta meta : metasSelecionadas) {
				try {
					metaService.enviarMetaAprovacao(meta,
							TipoConfiguracaoCiclo.DESENV, TipoGrupo.C);
					metasSucesso.add(meta);
				} catch (ValidateException e) {
					FacesUtil.addErrorMessage(e.getMsgErrors());
				}
			}
			FacesUtil
					.addInfoMessage("Metas enviadas para aprova\u00E7\u00E3o com sucesso.");
		} catch (Exception e) {
			FacesUtil
					.addErrorMessage("form_enviar_aprovacao",
							"Erro ao enviar metas para aprova\u00E7\u00E3o, tente novamente mais tarde.");
		}
	}

	public void alterouSelecaoTodosEnviarAprovacao() {
		if (this.getSelecaoTodosEnviarAprovacao()) {
			if (this.metasEnviarAprovacao != null) {
				for (Meta m : this.metasEnviarAprovacao) {
					m.setSelecionado(Boolean.TRUE);
				}
			}
		} else {
			if (this.metasEnviarAprovacao != null) {
				for (Meta m : this.metasEnviarAprovacao) {
					m.setSelecionado(Boolean.FALSE);
				}
			}
		}
	}

	public BigDecimal getTotalHorasFundamentalPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.F) {
							if (se.getQuantidadePrevista() != null)
								retorno = retorno.add(se
										.getQuantidadePrevista());
						}
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalHorasComplementarPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.C) {
							if (se.getQuantidadePrevista() != null)
								retorno = retorno.add(se
										.getQuantidadePrevista());
						}
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalHorasPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getQuantidadePrevista() != null)
							retorno = retorno.add(se.getQuantidadePrevista());
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalHorasFundamentalRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.F) {
							if (se.getQuantidadeRealizada() != null)
								retorno = retorno.add(se
										.getQuantidadeRealizada());
						}
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalHorasComplementarRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.C) {
							if (se.getQuantidadeRealizada() != null)
								retorno = retorno.add(se
										.getQuantidadeRealizada());
						}
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalHorasRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getQuantidadeRealizada() != null)
							retorno = retorno.add(se.getQuantidadeRealizada());
					}
				}
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosFundamentalPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.F) {
							retorno = retorno.add(se.getPontuacaoPrevista());
						}
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaFundamental()) > 0) {
				retorno = cm.getPontuacaoMaximaFundamental();
			}
		}

		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosComplementarPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.C) {
							retorno = retorno.add(se.getPontuacaoPrevista());
						}
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaComplementar()) > 0) {
				retorno = cm.getPontuacaoMaximaComplementar();
			}
		}

		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosPrevisto() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						retorno = retorno.add(se.getPontuacaoPrevista());
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaComplementar().add(
					cm.getPontuacaoMaximaFundamental())) > 0) {
				retorno = cm.getPontuacaoMaximaComplementar().add(
						cm.getPontuacaoMaximaFundamental());
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosFundamentalRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.F) {
							retorno = retorno.add(se.getPontuacaoRealizada());
						}
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaFundamental()) > 0) {
				retorno = cm.getPontuacaoMaximaFundamental();
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosComplementarRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getVinculoOcupacional() == VinculoOcupacional.C) {
							retorno = retorno.add(se.getPontuacaoRealizada());
						}
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaComplementar()) > 0) {
				retorno = cm.getPontuacaoMaximaComplementar();
			}
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getTotalPontosRealizado() {
		BigDecimal retorno = BigDecimal.ZERO;
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						retorno = retorno.add(se.getPontuacaoRealizada());
					}
				}
			}
		}

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}

		if (cm != null) {
			if (retorno.compareTo(cm.getPontuacaoMaximaComplementar().add(
					cm.getPontuacaoMaximaFundamental())) > 0) {
				retorno = cm.getPontuacaoMaximaComplementar().add(
						cm.getPontuacaoMaximaFundamental());
			}
		}

		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	public boolean isHouveAlteracaoCargaHoraria() {
		if (this.metas != null && !this.metas.isEmpty()) {
			for (Meta meta : this.metas) {
				List<SolucaoEducacionalMeta> solucoesEducacionais = meta
						.getSolucoesEducacionais();
				if (solucoesEducacionais != null
						&& !solucoesEducacionais.isEmpty()) {
					for (SolucaoEducacionalMeta se : solucoesEducacionais) {
						if (se.getQuantidadeAntiga() == null) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public BigDecimal getPontuacaoMaximaComplementar() {
		BigDecimal pontuacaoMaximaComplementar = BigDecimal.ZERO;

		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}
		if (cm != null) {
			pontuacaoMaximaComplementar = cm.getPontuacaoMaximaComplementar();
		}

		return pontuacaoMaximaComplementar;
	}

	public BigDecimal getPontuacaoMaximaFundamental() {
		BigDecimal pontuacaoMaximaFundamental = BigDecimal.ZERO;
		CicloConfiguracao cc = appBean.getCicloConfiguracaoDesenvolvimento();
		ConfiguracaoMetas cm = null;
		if (cc != null) {
			List<ConfiguracaoMetas> configuracoesMetas = cc
					.getConfiguracoesMetasDesenvolvimento(appBean
							.getUsuarioAutenticado().getUnidade());
			if (configuracoesMetas != null && !configuracoesMetas.isEmpty()) {
				cm = configuracoesMetas.get(0);
			}
		}
		if (cm != null) {
			pontuacaoMaximaFundamental = cm.getPontuacaoMaximaFundamental();
		}
		return pontuacaoMaximaFundamental;
	}

	public BigDecimal getTotalPontosHoraComplementar() {
		BigDecimal retorno = BigDecimal.ZERO;
		try {
			BigDecimal pontuacaoMaximaComplementar = getTotalPontosComplementarPrevisto();
			BigDecimal totalHorasComplementarPrevisto = getTotalHorasComplementarPrevisto();
			retorno = pontuacaoMaximaComplementar.divide(
					totalHorasComplementarPrevisto, RoundingMode.HALF_UP);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getTotalPontosHoraFundamental() {
		BigDecimal retorno = BigDecimal.ZERO;
		try {
			BigDecimal pontuacaoMaximaComplementar = getTotalPontosFundamentalPrevisto();
			BigDecimal totalHorasComplementarPrevisto = getTotalHorasFundamentalPrevisto();
			retorno = pontuacaoMaximaComplementar.divide(
					totalHorasComplementarPrevisto, RoundingMode.HALF_UP);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}

	
	public BigDecimal getTotalPontosHoraFundamentalComplementar() {
		BigDecimal retorno = BigDecimal.ZERO;
		try {
			retorno = retorno.add(getTotalPontosHoraComplementar());
			retorno = retorno.add(getTotalPontosHoraFundamental());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return retorno.setScale(3, RoundingMode.HALF_UP);
	}
	public List<Meta> getMetas() {
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

	public TipoFiltro getTipoFiltro() {
		return tipoFiltro;
	}

	public void setTipoFiltro(TipoFiltro tipoFiltro) {
		this.tipoFiltro = tipoFiltro;
	}

	public Ciclo getCiclo() {
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

	public SolucaoEducacionalMeta getSolucaoSelecionada() {
		return solucaoSelecionada;
	}

	public void setSolucaoSelecionada(SolucaoEducacionalMeta solucaoSelecionada) {
		this.solucaoSelecionada = solucaoSelecionada;
	}

	public int getIndiceAnexo() {
		return indiceAnexo;
	}

	public void setIndiceAnexo(int indiceAnexo) {
		this.indiceAnexo = indiceAnexo;
	}

	public Boolean getSelecaoTodosEnviarAprovacao() {
		return selecaoTodosEnviarAprovacao;
	}

	public void setSelecaoTodosEnviarAprovacao(
			Boolean selecaoTodosEnviarAprovacao) {
		this.selecaoTodosEnviarAprovacao = selecaoTodosEnviarAprovacao;
	}

	public List<Meta> getMetasEnviarAprovacao() {
		return metasEnviarAprovacao;
	}

	public void setMetasEnviarAprovacao(List<Meta> metasEnviarAprovacao) {
		this.metasEnviarAprovacao = metasEnviarAprovacao;
	}

}
