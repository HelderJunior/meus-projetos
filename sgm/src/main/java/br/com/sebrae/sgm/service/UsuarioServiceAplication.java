package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.producer.qualifier.Aplicacao;
import br.com.sebrae.sgm.utils.CPFUtils;
import br.com.sebrae.sgm.utils.SenhaUtils;

@Named
@ApplicationScoped
public class UsuarioServiceAplication implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@Aplicacao
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void importarUsuarios() {
		try {
			List<RMColaborador> colaboradoresNaoImportados = em.createQuery(
					"select c from RMColaborador c where c.rmColaboradorPK.colaboradorCpf "
							+ " not in(Select u.cpf from Usuario u) order by c.colaboradorCodSituacao asc").getResultList();

			if (colaboradoresNaoImportados != null && !colaboradoresNaoImportados.isEmpty()) {
				for (RMColaborador rmColaborador : colaboradoresNaoImportados) {
					if (CPFUtils.validate(rmColaborador.getRmColaboradorPK().getColaboradorCpf()))
						importarDoRM(rmColaborador);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Transactional
	public void importarDoRM(RMColaborador urm) throws Exception {

		Usuario novoUsuario = new Usuario();
		novoUsuario.setCpf(urm.getRmColaboradorPK().getColaboradorCpf());
		novoUsuario.setSenha(SenhaUtils.encrypt(SenhaUtils.generatePassword()));
		novoUsuario.setEmail(urm.getColaboradorEmail());
		novoUsuario.setNome(urm.getColaboradorNome());
		novoUsuario.setAtivo(Boolean.TRUE);
		novoUsuario.setMatricula(urm.getRmColaboradorPK().getColaboradorMatricula());
		novoUsuario.setTipo(TipoUsuario.I);
		novoUsuario.setEspacoOcupacional(urm.getRmEspacoOcupacional());
		novoUsuario.setUnidade(urm.getRmUnidade());

		Perfil p = carregarPerfil(Perfil.COLABORADOR_CHAVE);
		UsuarioPerfil up = new UsuarioPerfil();
		up.setUsuario(novoUsuario);
		up.setPerfil(p);
		up.setUnidade(urm.getRmUnidade());
		novoUsuario.setPerfis(new ArrayList<UsuarioPerfil>());
		novoUsuario.getPerfis().add(up);

		this.em.persist(novoUsuario);
	}

	private Perfil carregarPerfil(String chave) {
		return this.em.find(Perfil.class, chave);
	}

}
