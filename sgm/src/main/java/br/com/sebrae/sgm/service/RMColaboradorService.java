package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.PropertiesUtils;
import br.com.sebrae.sgm.utils.SenhaUtils;

@Named
public class RMColaboradorService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private MailService mail;

	@Inject
	private UsuarioService usuarioService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public RMColaborador loadByCpf(String cpf) throws Exception {
		try {
			return (RMColaborador) this.em.createNamedQuery("RMColaborador.findByColaboradorCpf")
					.setParameter("colaboradorCpf", cpf).setMaxResults(1).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			log.error("Erro ao buscar usuario por email", e);
			throw e;
		}
	}

	public List<RMColaborador> getColaboradoresAtivosCPF(String cpf) throws Exception {
		return (List<RMColaborador>) this.em.createNamedQuery("RMColaborador.findAtivosByColaboradorCpf")
				.setParameter("colaboradorCpf", cpf).getResultList();
	}
	
	public List<UF> getUfsColaborador(String cpf){
		return this.em.createNamedQuery("RMColaborador.findUFsColaborador")
				.setParameter("colaboradorCpf", cpf).getResultList();
		
	}

	@Transactional
	public void reenviarSenha(String cpf) throws ValidateException, Exception {
		try {
			RMColaborador urm = loadByCpf(cpf);
			if (urm == null) {
				throw new ValidateException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"msg_usuario_nao_encontrado"));
			} else {
				String novaSenha = SenhaUtils.generatePassword();
				Map<String, String> params = new HashMap<String, String>();
				params.put("url_app", PropertiesUtils.getInstance("app").getProperty("app.url"));
				params.put("cpf", cpf);
				params.put("nome_usuario", urm.getColaboradorNome());
				params.put("senha_usuario", novaSenha);
				mail.sendEmailForTemplate(urm.getColaboradorEmail(), "Sua senha para acesso ao SGM",
						"tpl/envio_senha.html", params, null);
				usuarioService.createUpdateFromRM(urm, novaSenha, new Date());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public String reenviarSenhaComRetornoSenha(String cpf) throws ValidateException, Exception {
		try {
			RMColaborador urm = loadByCpf(cpf);
			if (urm == null) {
				throw new ValidateException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"msg_usuario_nao_encontrado"));
			} else {
				String novaSenha = SenhaUtils.generatePassword();
				Map<String, String> params = new HashMap<String, String>();
				params.put("url_app", PropertiesUtils.getInstance("app").getProperty("app.url"));
				params.put("nome_usuario", urm.getColaboradorNome());
				params.put("senha_usuario", novaSenha);
				mail.sendEmailForTemplate(urm.getColaboradorEmail(), "Sua senha para acesso ao SGM",
						"tpl/envio_senha.html", params, null);
				usuarioService.createUpdateFromRM(urm, novaSenha, null);

				return novaSenha;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public List<RMColaborador> findByUnidades(List<Unidade> unidades) {
		List<RMColaborador> retorno = this.em
				.createQuery("Select c from RMColaborador c where c.rmUnidade in (:unidades)")
				.setParameter("unidades", unidades).getResultList();
		return retorno;
	}

}
