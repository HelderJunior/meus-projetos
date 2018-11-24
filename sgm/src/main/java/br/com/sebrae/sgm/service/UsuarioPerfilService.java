package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.UnidadePK;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.TipoMetaAcessoAdministrador;
import br.com.sebrae.sgm.model.enums.UF;

@Named
public class UsuarioPerfilService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Transactional
	public void insert(UsuarioPerfil up) {
		this.em.persist(up);
	}

	@Transactional
	public void update(UsuarioPerfil up) {
		this.em.merge(up);
	}

	@Transactional
	public void delete(UsuarioPerfil up) {
		this.em.remove(up);
	}

	public List<UsuarioPerfil> findByTipoPerfilAndColaborador(String chave, Usuario colaborador) {
		return this.em
				.createQuery("Select up from UsuarioPerfil up where up.perfil.chave = :chave and up.usuario = :usuario")
				.setParameter("chave", chave).setParameter("usuario", colaborador).getResultList();
	}

	public List<UsuarioPerfil> findByTipoPerfilAndUnidade(String chave, Unidade unidade) {
		return this.em
				.createQuery("Select up from UsuarioPerfil up where up.perfil.chave = :chave and up.unidade = :unidade")
				.setParameter("chave", chave).setParameter("unidade", unidade).getResultList();
	}
	
	public List<UsuarioPerfil> findByTipoPerfilAndUnidades(String chave, List<Unidade> unidades) {
		return this.em
				.createQuery("Select up from UsuarioPerfil up where up.perfil.chave = :chave and up.unidade in(:unidade)")
				.setParameter("chave", chave).setParameter("unidade", unidades).getResultList();
	}

	public List<UsuarioPerfil> findByTipoPerfilAndColaboradorUnidade(String chave, Usuario colaborador, Unidade unidade) {
		return this.em
				.createQuery(
						"Select up from UsuarioPerfil up where up.perfil.chave = :chave and up.usuario = :usuario and up.unidade = :unidade")
				.setParameter("chave", chave).setParameter("usuario", colaborador).setParameter("unidade", unidade)
				.getResultList();
	}

	public List<UsuarioPerfil> findByTipoPerfilUsuarioTipoAcessoUf(Perfil perfil, Usuario usuario,
			TipoMetaAcessoAdministrador tipoAcesso, UF uf) {
		return this.em
				.createQuery(
						"Select up from UsuarioPerfil up where up.perfil = :perfil and up.usuario = :usuario and up.tipoMetaAcessoAdministrador = :tipoAcesso and up.uf = :uf")
				.setParameter("perfil", perfil).setParameter("usuario", usuario).setParameter("tipoAcesso", tipoAcesso)
				.setParameter("uf", uf).getResultList();
	}

	public List<UsuarioPerfil> findUnidadesChefe() {
		Session session = (Session) em.getDelegate();
		Criteria criteria = session.createCriteria(UsuarioPerfil.class);
		criteria.createCriteria("perfil").add(Restrictions.in("chave", Perfil.PERFIS_CHEFIA));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}

	public List<UsuarioPerfil> findAdministradores() {
		List<UsuarioPerfil> retorno = this.em
				.createQuery("Select distinct up from UsuarioPerfil up where up.perfil.chave in(:chaves_administrador)")
				.setParameter("chaves_administrador",
						Arrays.asList(new String[] { Perfil.ADM_MASTER, Perfil.ADM_UF_NACIONAL })).getResultList();
		return retorno;
	}

	public List<Usuario> findUsuariosCandidatosAvaliadores(Unidade unidade, Perfil perfil) {
		List<Usuario> retorno = new ArrayList<Usuario>();
		
		if(!unidade.getUnidadePK().getCodigo().equals(unidade.getCodigoDiretoria())){
			retorno =  this.em
					.createQuery(
							"Select distinct up.usuario from UsuarioPerfil up where up.unidade.unidadePK.codigo like :codigo and up.perfil = :perfil order by up.usuario.nome")
					.setParameter("codigo", unidade.getCodigoDiretoria() + "%").setParameter("perfil", perfil).getResultList();
		}else{
			retorno = this.em
					.createQuery(
							"Select distinct up.usuario from UsuarioPerfil up where up.unidade = :unidade and up.perfil = :perfil order by up.usuario.nome")
					.setParameter("unidade", unidade).setParameter("perfil", perfil).getResultList();
		}

		return retorno;
	}

	public boolean podeConfigurarParametro(Usuario usuario, TipoMetaAcessoAdministrador... tipo) {
		List<UsuarioPerfil> retorno = this.em
				.createQuery(
						"Select distinct up from UsuarioPerfil up where up.tipoMetaAcessoAdministrador in(:tipo_acesso) and up.usuario = :usuario")
				.setParameter("tipo_acesso", Arrays.asList(tipo)).setParameter("usuario", usuario).getResultList();

		if (retorno != null && !retorno.isEmpty()) {
			return true;
		}
		return false;
	}

	public void salvar(UsuarioPerfil up) {
		if (up.getId() != null)
			this.em.merge(up);
		else
			this.em.persist(up);
	}

	public List<String> findByUnidadesGerenciaveis(Usuario usuarioAutenticado) {
		List<String> str = new ArrayList<String>();
		Query q = this.em.createQuery("Select up from UsuarioPerfil up where up.usuario = :usuarioAutenticado and up.perfil.chave = 'ROLE_GERENTE')");
		q.setParameter("usuarioAutenticado", usuarioAutenticado);
		List<UsuarioPerfil> retorno = q.getResultList();
		
		for (UsuarioPerfil usuarioPerfil : retorno) {
			str.add(usuarioPerfil.getUnidade().getUnidadePK().getCodigo());
		}
		
		return str;
	}
}
