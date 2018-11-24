package br.com.sebrae.sgm.service.observer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.naming.NamingException;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoEmail;
import br.com.sebrae.sgm.model.FaseCiclo;
import br.com.sebrae.sgm.model.FaseCicloConfiguracao;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.StatusCiclo;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.service.MailService;
import br.com.sebrae.sgm.service.events.EnviarEmailAlteracaoMetaEvent;
import br.com.sebrae.sgm.utils.PropertiesUtils;

public class EnviarEmailAlteracaoMetaObserver implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MailService mailService;

	public void enviarEmailAlteracaoMeta(@Observes EnviarEmailAlteracaoMetaEvent event) throws AddressException,
			NamingException, MessagingException, IOException {
		String alteracaoRealizada = event.getAlteracaoRealizada();
		Meta meta = event.getMeta();
		TipoConfiguracaoCiclo tipoConfiguracao = event.getTipoConfiguracao();
		TipoGrupo tipoCalendario = event.getTipoCalendario();

		CicloConfiguracao cc = meta.getCicloConfiguracao();
		List<ConfiguracaoEmail> configuracoesEmail = cc.getConfiguracoesEmails();

		if (configuracoesEmail != null) {
			for (ConfiguracaoEmail confEmail : configuracoesEmail) {
				FaseCicloConfiguracao fcc = cc.getFaseCicloByTipo(tipoCalendario);
				if (fcc != null) {
					List<Fase> fases = confEmail.getFasesCiclo();
					for (Fase fase : fases) {
						FaseCiclo fc = fcc.getFaseByTipo(fase);
						if (fc != null) {
							if (fc.getStatus() == StatusCiclo.I) {// envia os emails
																	// para a fase
																	// configurada
								if (meta.getTipo() == TipoMeta.E) {// envia para
																	// todo mundo da
																	// unidade
									Unidade um = meta.getUnidade();
									List<Usuario> usuarios = um.getUsuarios();
									if (usuarios != null) {
										for (Usuario usu : usuarios) {
											if (confEmail.getUsuarios().contains(usu)) {
												Map<String, String> params = new HashMap<String, String>();
												params.put("url_app",
														PropertiesUtils.getInstance("app").getProperty("app.url"));
												params.put("nome_usuario", usu.getNome());
												params.put("codigo_descricao_meta",
														meta.getCodigo() + " - " + meta.getResultadosEsperados());
												params.put("nome_alteracao", alteracaoRealizada);
												mailService.sendEmailForTemplate(usu.getEmail(),
														"Altera\u00E7\u00E3o de Meta SGM", "tpl/alteracao_meta.html",
														params, null);
											}
										}
									}
								} else if (meta.getTipo() == TipoMeta.I) {// envia
																			// somente
																			// para
																			// o
																			// colaborador
									if (confEmail.getUsuarios().contains(meta.getColaborador())) {
										Map<String, String> params = new HashMap<String, String>();
										params.put("url_app", PropertiesUtils.getInstance("app").getProperty("app.url"));
										params.put("nome_usuario", meta.getColaborador().getNome());
										params.put("codigo_descricao_meta",
												meta.getCodigo() + " - " + meta.getResultadosEsperados());
										params.put("nome_alteracao", alteracaoRealizada);
										mailService.sendEmailForTemplate(meta.getColaborador().getEmail(),
												"Altera\u00E7\u00E3o de Meta SGM", "tpl/alteracao_meta.html", params,
												null);
									}
								}
							}
						}
					}

				}
			}
		}
	}
}
