package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.UsuarioPerfil;
import br.com.sebrae.sgm.model.enums.TipoUsuario;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.utils.Constants;
import br.com.sebrae.sgm.utils.PropertiesUtils;
import br.com.sebrae.sgm.utils.SenhaUtils;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private MailService mail;

	@Inject
	private PerfilService perfilService;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public Usuario loadByCpf(String cpf) throws Exception {
		try {
			return (Usuario) this.em.createNamedQuery("Usuario.findByCpf").setParameter("cpf", cpf).setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			log.error("Erro ao buscar usuario por email", e);
			throw e;
		}
	}

	public Usuario loadExternoByCpf(String cpf) throws Exception {
		try {
			return (Usuario) this.em.createNamedQuery("Usuario.findByCpfExterno").setParameter("cpf", cpf)
					.setMaxResults(1).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			log.error("Erro ao buscar usuario por email", e);
			throw e;
		}
	}

	public Usuario loadWithPerfis(Integer id) throws Exception {
		try {
			return (Usuario) this.em
					.createQuery("select distinct u from Usuario u join u.perfis p where u.id = :idUsuario")
					.setParameter("idUsuario", id).setMaxResults(1).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			log.error("Erro ao buscar usuario por email", e);
			throw e;
		}
	}

	public Usuario loadByEmail(String email) throws Exception {
		try {
			return (Usuario) this.em.createNamedQuery("Usuario.findByEmail").setParameter("email", email)
					.setMaxResults(1).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (Exception e) {
			log.error("Erro ao buscar usuario por email", e);
			throw e;
		}
	}

	@Transactional
	public Usuario createUpdateFromRM(RMColaborador urm, String novaSenha, Date ultimoAcesso) throws Exception {
		Usuario u = loadByCpf(urm.getRmColaboradorPK().getColaboradorCpf());

		if (u != null) {
			if (!StringUtils.isBlank(novaSenha)) {
				u.setSenha(SenhaUtils.encrypt(novaSenha));
			}
			if (ultimoAcesso != null) {
				u.setUltimoAcesso(ultimoAcesso);
			}
			u.setEmail(urm.getColaboradorEmail());
			u.setNome(urm.getColaboradorNome());
			u.setAtivo(Boolean.TRUE);
			if (u.getMatricula() == null)
				u.setMatricula(urm.getRmColaboradorPK().getColaboradorMatricula());
			u.setTipo(TipoUsuario.I);
			u.setEspacoOcupacional(urm.getRmEspacoOcupacional());
			if (u.getUnidade() == null)
				u.setUnidade(urm.getRmUnidade());
			em.merge(u);
		} else {
			Usuario novoUsuario = new Usuario();
			novoUsuario.setCpf(urm.getRmColaboradorPK().getColaboradorCpf());
			novoUsuario.setSenha(SenhaUtils.encrypt(novaSenha));
			novoUsuario.setEmail(urm.getColaboradorEmail());
			novoUsuario.setNome(urm.getColaboradorNome());
			novoUsuario.setAtivo(Boolean.TRUE);
			novoUsuario.setMatricula(urm.getRmColaboradorPK().getColaboradorMatricula());
			novoUsuario.setTipo(TipoUsuario.I);
			novoUsuario.setEspacoOcupacional(urm.getRmEspacoOcupacional());
			novoUsuario.setUnidade(urm.getRmUnidade());

			Perfil p = perfilService.load(Perfil.COLABORADOR_CHAVE);
			UsuarioPerfil up = new UsuarioPerfil();
			up.setUsuario(novoUsuario);
			up.setPerfil(p);
			up.setUnidade(urm.getRmUnidade());
			novoUsuario.setPerfis(new ArrayList<UsuarioPerfil>());
			novoUsuario.getPerfis().add(up);

			this.em.persist(novoUsuario);
			u = novoUsuario;
		}

		return u;
	}

	@Transactional
	public void reenviarSenha(String cpf) throws ValidateException, Exception {
		try {
			Usuario u = loadByCpf(cpf);
			if (u == null) {
				throw new ValidateException(PropertiesUtils.getInstance(Constants.MSG_FILE).getProperty(
						"msg_usuario_nao_encontrado"));
			} else {
				String novaSenha = SenhaUtils.generatePassword();
				Map<String, String> params = new HashMap<String, String>();
				params.put("url_app", PropertiesUtils.getInstance("app").getProperty("app.url"));
				params.put("nome_usuario", u.getNome());
				params.put("senha_usuario", novaSenha);
				mail.sendEmailForTemplate(u.getEmail(), "Sua senha para acesso ao SGM", "tpl/envio_senha.html", params,
						null);

				u.setSenha(SenhaUtils.encrypt(novaSenha));
				em.merge(u);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Usuario> findByCodUnidadeUF(String codUnidade, UF uf) {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select u from Usuario u where u.unidade.unidadePK.uf = :uf and u.unidade.unidadePK.codigo = :codigo")
				.setParameter("uf", uf).setParameter("codigo", codUnidade).getResultList();

		return retorno;
	}

	public List<Usuario> findByCodUnidadeMesmaDiretoria(String codUnidade) {

		String codigoUnidade[] = codUnidade.split("\\.");
		String codigoBuscar = codigoUnidade[0];

		List<Usuario> retorno = this.em
				.createQuery("Select u from Usuario u where u.unidade.unidadePK.codigo like :codigo")
				.setParameter("codigo", codigoBuscar + ".%").getResultList();

		return retorno;
	}

	public List<Usuario> findByUnidade(Unidade unidade) {
		List<Usuario> retorno = this.em.createQuery("Select u from Usuario u where u.unidade = :unidade")
				.setParameter("unidade", unidade).getResultList();
		return retorno;
	}

	public List<Usuario> findByUnidadeAndTipo(Unidade unidade, TipoUsuario tipo) {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u where u.unidade = :unidade and u.tipo = :tipo order by u.nome")
				.setParameter("unidade", unidade).setParameter("tipo", tipo).getResultList();
		return retorno;
	}

	public List<Usuario> findByTipo(TipoUsuario tipo) {
		List<Usuario> retorno = this.em
				.createQuery("Select distinct u from Usuario u where u.tipo = :tipo order by u.nome")
				.setParameter("tipo", tipo).getResultList();
		return retorno;
	}

	public List<Usuario> findByUFAndTipo(UF uf, TipoUsuario tipo) {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u where u.unidade.unidadePK.uf = :uf and u.tipo = :tipo order by u.nome")
				.setParameter("uf", uf).setParameter("tipo", tipo).getResultList();
		return retorno;
	}

	public List<Usuario> findByUnidades(List<Unidade> unidades) {
		List<Usuario> retorno = this.em.createQuery("Select u from Usuario u where u.unidade in (:unidades)")
				.setParameter("unidades", unidades).getResultList();
		return retorno;
	}

	public List<Usuario> findByUnidadeMesmaDiretoria(List<Unidade> unidades) {
		List<Usuario> retorno = this.em.createQuery("Select u from Usuario u where u.unidade in (:unidades)")
				.setParameter("unidades", unidades).getResultList();
		return retorno;
	}

	public List<Usuario> findByNome(String nome) {
		List<Usuario> retorno = this.em.createQuery("Select u from Usuario u where u.nome like :nome order by u.nome")
				.setParameter("nome", "%" + nome + "%").getResultList();
		return retorno;
	}

	public List<Usuario> findByNomeUF(String nome, UF uf) {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u where u.nome like :nome and u.unidade.unidadePK.uf = :uf order by u.nome")
				.setParameter("nome", "%" + nome + "%").setParameter("uf", uf).getResultList();
		return retorno;
	}

	public List<Usuario> findAll() {
		List<Usuario> retorno = this.em.createNamedQuery("Usuario.findAll").getResultList();
		return retorno;
	}

	public List<Usuario> findAllExternos() {
		List<Usuario> retorno = this.em.createNamedQuery("Usuario.findAllExternos").getResultList();
		return retorno;
	}

	public List<Usuario> findAllInternos() {
		List<Usuario> retorno = this.em.createNamedQuery("Usuario.findAllInternos").getResultList();
		return retorno;
	}

	public List<Usuario> findByDescricaoUsuarioInterno(String nome) {
		List<Usuario> retorno = (List<Usuario>) this.em
				.createQuery("SELECT u FROM Usuario u where u.nome like :nome and u.tipo = 'I'")
				.setParameter("nome", "%" + nome + "%").getResultList();
		return retorno;
	}

	public List<Usuario> findAdministradores() {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u join u.perfis p where p.perfil.chave in(:chaves_administrador)")
				.setParameter("chaves_administrador",
						Arrays.asList(new String[] { Perfil.ADM_MASTER, Perfil.ADM_UF_NACIONAL })).getResultList();
		return retorno;
	}

	public List<Usuario> findByUF(UF uf) {
		List<Usuario> retorno = this.em
				.createQuery("Select u from Usuario u join u.unidade un where un.unidadePK.uf = :uf")
				.setParameter("uf", uf).getResultList();

		return retorno;
	}

	public Usuario findGerente(Unidade unidade) {
		Usuario retorno = null;

		List<Usuario> usuariosChefe = this.em
				.createQuery(
						"select u from Usuario u join u.perfis p where p.perfil.chave = :chave and p.unidade = :unidade")
				.setParameter("chave", Perfil.GERENTE).setParameter("unidade", unidade).getResultList();

		if (usuariosChefe != null && !usuariosChefe.isEmpty()) {
			retorno = usuariosChefe.get(0);
		}

		return retorno;
	}

	public List<Usuario> findByNameLikeAndPerfil(String nome, Perfil perfil) {
		Session s = (Session) this.em.getDelegate();

		Criteria c = s.createCriteria(Usuario.class);

		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));

		if (perfil != null) {
			if (perfil.isExterno()) {
				c.createCriteria("propriedadesUsuarioExterno").createCriteria("perfis")
						.add(Restrictions.eq("chave", perfil.getChave()));
			} else {
				c.createCriteria("perfis").add(Restrictions.eq("perfil", perfil));
			}
		}

		c.addOrder(Order.asc("nome"));

		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return c.list();
	}

	public List<Usuario> findByNameLikeAndPerfilAndUnidades(String nome, Perfil perfil, List<Unidade> unidades) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(Usuario.class);

		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));

		if (perfil != null) {
			if (perfil.isExterno()) {
				c.createCriteria("propriedadesUsuarioExterno").createCriteria("perfil")
						.add(Restrictions.eq("chave", perfil.getChave()));
				c.add(Restrictions.in("unidade", unidades));
			} else {
				c.createCriteria("perfis").add(Restrictions.eq("perfil", perfil))
						.add(Restrictions.in("unidade", unidades));
			}
		}
		c.addOrder(Order.asc("nome"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	public List<Usuario> findByNameLikeAndPerfisAndUnidadesInternos(String nome, List<Perfil> perfis,
			List<Unidade> unidades) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(Usuario.class);
		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));

		Criteria c2 = c.createCriteria("perfis");

		c2.add(Restrictions.in("perfil", perfis));
		c2.add(Restrictions.in("unidade", unidades));

		c.addOrder(Order.asc("nome"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	public List<Usuario> findByNameLikeAndPerfisAndUnidadesExternos(String nome, List<Perfil> perfis,
			List<Unidade> unidades) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(Usuario.class);
		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));

		c.createCriteria("propriedadesUsuarioExterno").add(Restrictions.in("perfil", perfis));

		// c.add(Restrictions.in("unidade", unidades));

		c.addOrder(Order.asc("nome"));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	public List<Usuario> findByPerfil(Perfil perfil) {
		Session s = (Session) this.em.getDelegate();

		Criteria c = s.createCriteria(Usuario.class);

		if (perfil != null) {
			if (perfil.isExterno()) {
				c.createCriteria("propriedadesUsuarioExterno").add(Restrictions.eq("perfil", perfil));
			} else {
				c.createCriteria("perfis").add(Restrictions.eq("perfil", perfil));
			}
		}

		c.addOrder(Order.asc("nome"));

		return c.list();
	}

	public List<Usuario> findExternosByPerfilUF(Perfil perfil, UF uf) {

		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u join u.propriedadesUsuarioExterno p join p.ufs uf where p.perfil = :perfil and uf = :uf order by u.nome asc")
				.setParameter("perfil", perfil).setParameter("uf", uf).getResultList();

		return retorno;
	}

	public List<Usuario> findExternosUF(UF uf) {

		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u join u.propriedadesUsuarioExterno p join p.ufs uf where uf = :uf order by u.nome asc")
				.setParameter("uf", uf).getResultList();

		return retorno;
	}

	public List<Usuario> findInternosByPerfilUF(Perfil perfil, UF uf) {

		List<Usuario> retorno = this.em
				.createQuery(
						"Select u from Usuario u join u.perfis p where p.perfil = :perfil and p.unidade.unidadePK.uf = :uf order by u.nome asc")
				.setParameter("perfil", perfil).setParameter("uf", uf).getResultList();

		return retorno;
	}

	public List<Usuario> findByNameLikeAndUnidade(String nome, Unidade unidade) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(Usuario.class);
		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));

		if (unidade != null) {
			c.createCriteria("unidade").add(Restrictions.eq("unidadePK", unidade.getUnidadePK()));
		}

		c.addOrder(Order.asc("nome"));
		return c.list();
	}

	public List<Usuario> findByNameLikeAndTipo(String nome, TipoUsuario tipoUsuario) {
		Session s = (Session) this.em.getDelegate();
		Criteria c = s.createCriteria(Usuario.class);
		c.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		c.add(Restrictions.eq("tipo", tipoUsuario));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}

	public List<Usuario> findByNameLikeAndTipoUsuarioAndUF(String nome, TipoUsuario tipoUsuario, UF uf) {
		List<Usuario> retorno = this.em
				.createQuery(
						"Select distinct u from Usuario u where u.nome like :nome and u.unidade.unidadePK.uf = :uf and u.tipo = :tipoUsuario order by u.nome")
				.setParameter("nome", "%" + nome + "%").setParameter("uf", uf).setParameter("tipoUsuario", tipoUsuario)
				.getResultList();
		return retorno;
	}

	public Usuario findDiretor(Unidade unidade) {
		Usuario retorno = null;

		List<Usuario> usuariosChefe = this.em
				.createQuery(
						"select u from Usuario u join u.perfis p where p.perfil.chave = :chave and p.unidade = :unidade")
				.setParameter("chave", Perfil.DIRETOR).setParameter("unidade", unidade).getResultList();

		if (usuariosChefe != null && !usuariosChefe.isEmpty()) {
			retorno = usuariosChefe.get(0);
		}

		return retorno;
	}

	@Transactional
	public void save(Usuario u) throws ValidateException {
		ValidateUtils.validateThrows(u);
		if (u.getId() == null) {
			em.persist(u);
		} else {
			em.merge(u);
		}
	}

	public List<Usuario> findByParameters(String cpf, UF uf, Perfil perfil, Unidade unidade, Usuario usuario,
			TipoUsuario tipo) {
		List<Usuario> retorno = new ArrayList<Usuario>();

		Session s = (Session) this.em.getDelegate();

		Criteria c = s.createCriteria(Usuario.class);

		if (!StringUtils.isBlank(cpf)) {
			c.add(Restrictions.eq("cpf", cpf));
		}

		if (usuario != null) {
			c.add(Restrictions.eq("id", usuario.getId()));
		}

		Criteria cUnidades = null;

		if (uf != null && unidade == null) {
			cUnidades = c.createCriteria("unidade");
			cUnidades.add(Restrictions.eq("unidadePK.uf", uf));
		} else if (unidade != null) {
			cUnidades = c.createCriteria("unidade");
			cUnidades.add(Restrictions.eq("unidadePK", unidade.getUnidadePK()));
		}

		if (tipo != null && perfil != null) {
			if (tipo == TipoUsuario.E) {
				c.createCriteria("propriedadesUsuarioExterno").createCriteria("perfis")
						.add(Restrictions.eq("chave", perfil.getChave()));
			} else {
				c.createCriteria("perfis").add(Restrictions.eq("perfil", perfil));
			}
		}

		if (tipo != null) {
			c.add(Restrictions.eq("tipo", TipoUsuario.I));
		}

		c.add(Restrictions.isNotNull("ultimoAcesso"));
		c.add(Restrictions.isNotNull("nome"));
		c.addOrder(Order.asc("nome"));

		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		retorno = c.list();
		return retorno;
	}

	@Transactional
	public void delete(Usuario usuario) {
		Usuario u = this.em.find(Usuario.class, usuario.getId());
		this.em.remove(u);
	}

	public List<Usuario> loadUsuarioPorPerfis(Perfil perfil) {
		List<Usuario> usuarios = this.em
				.createQuery("select distinct u from Usuario u join u.perfis p where p.perfil = :perfil")
				.setParameter("perfil", perfil).getResultList();
		return usuarios;
	}

	public List<Usuario> findAllUsuarioPorUnidade(String nome, Unidade unidade) {
		List<Usuario> retorno = new ArrayList<Usuario>();
		String qry = "Select u from Usuario u where (u.unidade = :unidade) and u.nome like :nome and u.tipo = 'I'";
		retorno = this.em.createQuery(qry).setParameter("nome", "%" + nome + "%").setParameter("unidade", unidade)
				.getResultList();
		return retorno;
	}

	public List<Usuario> findAllOrderNome(String nome) {
		List<Usuario> usuarios = this.em
				.createQuery("select distinct u from Usuario u where u.nome like :nome order by u.nome")
				.setParameter("nome", "%" + nome + "%").getResultList();
		return usuarios;
	}

	public List<Usuario> findByPerfilAndUnidades(Perfil perfil, List<Unidade> unidades) {
		List<Usuario> retorno = new ArrayList<Usuario>();
		retorno = this.em
				.createQuery(
						"Select distinct up.usuario from UsuarioPerfil up where up.unidade in(:unidades) and up.perfil = :perfil order by up.usuario.nome")
				.setParameter("unidades", unidades).setParameter("perfil", perfil).getResultList();
		return retorno;
	}

}
