package br.com.sebrae.sgm.controller;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.jpa.validator.Password;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.service.UsuarioService;
import br.com.sebrae.sgm.utils.FacesUtil;
import br.com.sebrae.sgm.utils.SenhaUtils;

@ConversationScoped
@Named
public class AlterarSenhaBean extends BaseBean {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(AlterarSenhaBean.class);

	private String senhaAtual;

	@Password
	private String novaSenha;

	private String confirmarNovaSenha;

	@Inject
	private UsuarioService usuarioGlobalService;

	@Inject
	private AppBean appBean;

	public String salvarNovaSenha() {
		try {
			Usuario u = appBean.getUsuarioAutenticado();
			String senhaAntiga = u.getSenha();
			if (senhaAntiga.equals(SenhaUtils.encrypt(novaSenha))) {
				FacesUtil.addErrorMessage("Nova senha n\u00E3o pode ser igual a senha anterior");
			} else {
				u.setSenha(SenhaUtils.encrypt(novaSenha));
				usuarioGlobalService.save(u);
				FacesUtil.addInfoMessage("Senha alterada com sucesso!");
			}
			senhaAtual = "";
			novaSenha = "";
			confirmarNovaSenha = "";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			FacesUtil.addErrorMessage("Ocorreu um erro ao alterar a senha, tente novamente mais tarde.");
		}
		return "";
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmarNovaSenha() {
		return confirmarNovaSenha;
	}

	public void setConfirmarNovaSenha(String confirmarNovaSenha) {
		this.confirmarNovaSenha = confirmarNovaSenha;
	}

}
