package br.com.sebrae.sgm.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.controller.dto.ArvoreUnidadesDTO;
import br.com.sebrae.sgm.listener.EMF;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.UF;
import br.com.sebrae.sgm.utils.UnidadeUtils;

public class PopularArvoreUnidadesJob implements Job {

	protected Logger log = LoggerFactory.getLogger(PopularArvoreUnidadesJob.class);

	private EntityManager em;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		em = EMF.createEntityManager();
		try {
			UnidadeUtils.arvoreUnidades = getUnidadesTree();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			em.close();
		}
	}

	public ArvoreUnidadesDTO getUnidadesTree() {

		ArvoreUnidadesDTO retorno = new ArvoreUnidadesDTO();
		TreeNode root = new DefaultTreeNode("ROOT", null);
		retorno.setRoot(root);

		List<Unidade> unidadesPai = this.em
				.createQuery(
						"Select un from Unidade un where un.unidadePK.codigo = '11' and un.unidadePK.uf = 'NA' and un.ativo = true order by un.unidadePK.codigo")
				.getResultList();

		List<Unidade> todasUnidades = this.em.createQuery(
				"Select un from Unidade un where un.unidadePK.codigo like '11%' or un.unidadePK.codigo like '011%' and un.ativo = true order by un.unidadePK.codigo")
				.getResultList();
		retorno.setUnidades(todasUnidades);

		Unidade unidadeSebrae = unidadesPai.get(0);
		TreeNode noUnidadeSebrae = new DefaultTreeNode(unidadeSebrae, root);
		noUnidadeSebrae.setExpanded(true);
		List<Unidade> filhasDiretasUnidadePai = getDirectChildrenByCodigo(unidadeSebrae.getUnidadePK().getCodigo());

		for (Unidade unidade : filhasDiretasUnidadePai) {
			TreeNode t1 = new DefaultTreeNode(unidade, noUnidadeSebrae);
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

}
