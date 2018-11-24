package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.ArvoreUnidadesDTO;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.UnidadePK;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.UF;

@Named
public class UnidadeService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public Unidade load(UnidadePK pk) {
		return this.em.find(Unidade.class, pk);
	}

	public List<Unidade> findAll() {
		List<Unidade> retorno = (List<Unidade>) this.em.createNamedQuery("Unidade.findAll").getResultList();
		return retorno;
	}

	public List<Unidade> findByUf(UF uf) {
		List<Unidade> retorno = (List<Unidade>) this.em.createNamedQuery("Unidade.findByUf").setParameter("uf", uf)
				.getResultList();
		return retorno;
	}

	public List<Unidade> findByDescricao(String descricao) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery("SELECT u FROM Unidade u where u.descricao like :descricao order by u.descricao asc")
				.setParameter("descricao", "%" + descricao + "%").getResultList();
		return retorno;
	}

	public List<Unidade> findByDescricaoOrCodigoAndUF(String valor, UF uf) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery(
						"SELECT u FROM Unidade u where (u.descricao like :valor or u.unidadePK.codigo like :valor) and u.unidadePK.uf = :uf order by u.unidadePK.codigo asc")
				.setParameter("valor", "%" + valor + "%").setParameter("uf", uf).getResultList();
		return retorno;
	}

	public List<Unidade> findByDescricaoOrCodigoAndUFs(String valor, List<UF> ufs) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery(
						"SELECT u FROM Unidade u where (u.descricao like :valor or u.unidadePK.codigo like :valor) and u.unidadePK.uf in(:ufs) order by u.descricao asc")
				.setParameter("valor", "%" + valor + "%").setParameter("ufs", ufs).getResultList();
		return retorno;
	}

	public List<Unidade> findByDescricaoOrCodigo(String valor) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery(
						"SELECT u FROM Unidade u where u.descricao like :valor or u.unidadePK.codigo like :valor order by u.descricao asc")
				.setParameter("valor", "%" + valor + "%").getResultList();
		return retorno;
	}

	public List<Unidade> findByDescricao1(String descricao) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery("SELECT u FROM Unidade u where u.descricao like :descricao")
				.setParameter("descricao", descricao + "%").getResultList();
		return retorno;
	}

	public List<Unidade> findByUfUsuario(UF uf, Integer idUsuario) {
		List<Unidade> retorno = (List<Unidade>) this.em.createNamedQuery("Unidade.findByUfUsuario")
				.setParameter("uf", uf).setParameter("idUsuario", idUsuario).getResultList();
		return retorno;
	}

	public List<UF> findUfsByCpf(String cpf) {
		List<UF> retorno = (List<UF>) this.em
				.createQuery("Select un.unidadePK.uf from Unidade un join un.usuarios u where u.cpf = :cpf")
				.setParameter("cpf", cpf).getResultList();
		return retorno;
	}

	public List<Unidade> findByTipoPerfilColaborador(String chavePerfil, Usuario usuario) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery(
						"Select up.unidade from UsuarioPerfil up where up.perfil.chave = :chave and up.usuario = :usuario")
				.setParameter("chave", chavePerfil).setParameter("usuario", usuario).getResultList();
		return retorno;
	}

	public List<Unidade> findByMeta(Meta meta) {
		List<Unidade> retorno = (List<Unidade>) this.em
				.createQuery("Select un from Unidade un join un.metasUnidadesVinculadas m where m.id = :idMeta")
				.setParameter("idMeta", meta.getId()).getResultList();
		return retorno;
	}

	public ArvoreUnidadesDTO getUnidadesTree(UF uf) {

		ArvoreUnidadesDTO retorno = new ArvoreUnidadesDTO();
		TreeNode root = new DefaultTreeNode("ROOT", null);
		retorno.setRoot(root);

		List<Unidade> unidadesPai = this.em
				.createQuery(
						"Select un from Unidade un where un.unidadePK.codigo = '11' and un.unidadePK.uf = 'NA' and un.ativo = true order by un.unidadePK.codigo")
				.getResultList();

		List<Unidade> todasUnidades = this.em
				.createQuery(
						"Select un from Unidade un where un.unidadePK.codigo like '11%' or un.unidadePK.codigo like '011%' and un.ativo = true order by un.unidadePK.codigo")
				.getResultList();
		retorno.setUnidades(todasUnidades);

		Unidade unidadeSebrae = unidadesPai.get(0);

		TreeNode noUnidadeSebrae = null;
		if (uf == UF.NA) {
			noUnidadeSebrae = new DefaultTreeNode(unidadeSebrae, root);
			noUnidadeSebrae.setExpanded(true);
		}

		List<Unidade> filhasDiretasUnidadePai = getDirectChildrenByCodigoUf(unidadeSebrae.getUnidadePK().getCodigo(),
				uf);

		for (Unidade unidade : filhasDiretasUnidadePai) {
			TreeNode t1 = null;
			if (uf == UF.NA) {
				t1 = new DefaultTreeNode(unidade, noUnidadeSebrae);
			} else {
				t1 = new DefaultTreeNode(unidade, root);
			}
			t1.setExpanded(true);
			addChildren(t1);
		}

		return retorno;
	}

	public void addChildren(TreeNode t) {
		Unidade un = (Unidade) t.getData();
		List<Unidade> unidades = getDirectChildrenByCodigoUf(un.getUnidadePK().getCodigo(), un.getUnidadePK().getUf());
		if (!unidades.isEmpty()) {
			for (Unidade unidade : unidades) {
				TreeNode t1 = new DefaultTreeNode(unidade);
				t.getChildren().add(t1);
				addChildren(t1);
			}
		}
	}

	public List<Unidade> getUnidadesMesmaDiretoria(List<Unidade> unidades) {
		Set<Unidade> retorno = new HashSet<Unidade>();
		if (unidades != null) {
			for (Unidade un : unidades) {
				List<Unidade> unidadesMesmaDiretoria = new ArrayList<Unidade>();
				if (un.getUnidadePK().getCodigo().equals(un.getCodigoDiretoria())) {
					unidadesMesmaDiretoria = this.em
							.createQuery(
									"Select un from Unidade un where un.unidadePK.codigo = :codigo and un.ativo = true")
							.setParameter("codigo", un.getCodigoDiretoria()).getResultList();
				} else {
					unidadesMesmaDiretoria = this.em
							.createQuery(
									"Select un from Unidade un where un.unidadePK.codigo like :codigo and un.ativo = true")
							.setParameter("codigo", un.getCodigoDiretoria() + ".%").getResultList();
				}
				retorno.addAll(unidadesMesmaDiretoria);
			}
		}
		return new ArrayList<Unidade>(retorno);

	}

	private List<Unidade> getDirectChildrenByCodigo(String codigo) {
		List<Unidade> retorno = new ArrayList<Unidade>();

		retorno = this.em
				.createQuery(
						"Select un from Unidade un where un.unidadePK.codigo like :codigo and un.ativo = true order by un.unidadePK.codigo")
				.setParameter("codigo", "%" + codigo + ".%").getResultList();

		if (retorno != null) {
			Iterator<Unidade> it = retorno.iterator();

			while (it.hasNext()) {
				Unidade un = it.next();

				int expectecdSize = codigo.split("\\.").length + 1;
				int size = un.getUnidadePK().getCodigo().split("\\.").length;
				if (expectecdSize != size)
					it.remove();
			}
		}

		return retorno;
	}

	private List<Unidade> getDirectChildrenByCodigoUf(String codigo, UF uf) {
		List<Unidade> retorno = new ArrayList<Unidade>();

		retorno = this.em
				.createQuery(
						"Select un from Unidade un where un.unidadePK.codigo like :codigo and un.unidadePK.uf = :uf and un.ativo = true order by un.unidadePK.codigo")
				.setParameter("codigo", "%" + codigo + ".%").setParameter("uf", uf).getResultList();

		if (retorno != null) {
			Iterator<Unidade> it = retorno.iterator();

			while (it.hasNext()) {
				Unidade un = it.next();

				int expectecdSize = codigo.split("\\.").length + 1;
				int size = un.getUnidadePK().getCodigo().split("\\.").length;
				if (expectecdSize != size)
					it.remove();
			}
		}

		return retorno;
	}

	public List<Unidade> findByUnidadesPorColaborador(String chave, Usuario usuario) {
		List<Unidade> retorno = new ArrayList<Unidade>();

		retorno = this.em.createQuery("Select up.unidade from UsuarioPerfil up where up.perfil.chave = :chave and up.usuario = :usuario")
				.setParameter("chave", chave).setParameter("usuario", usuario).getResultList();

		return retorno;

	}
}
