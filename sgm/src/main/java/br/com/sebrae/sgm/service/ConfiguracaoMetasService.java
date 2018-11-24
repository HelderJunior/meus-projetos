package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.ConfiguracaoMetas;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.TipoMeta;
import br.com.sebrae.sgm.utils.FacesUtil;

@Named
public class ConfiguracaoMetasService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public ConfiguracaoMetas findByCicloUnidadeTipoMeta(Ciclo ciclo, Unidade unidade, TipoMeta tipoMeta) {
		List<ConfiguracaoMetas> retorno = this.em
				.createQuery(
						"select distinct cm from ConfiguracaoMetas cm join cm.unidades un where un = :unidade and cm.cicloConfiguracao.ciclo = :ciclo "
								+ " and cm.tipoMeta = :tipoMeta").setParameter("unidade", unidade)
				.setParameter("ciclo", ciclo).setParameter("tipoMeta", tipoMeta).getResultList();

		if (!retorno.isEmpty()) {
			return retorno.get(0);
		}
		
		return null;
	}
	
	public String mensagemSucesso(){
		FacesUtil.addInfoMessage("Par\u00E2metros salvos com sucesso!");
		return "/pages/ciclo/manter.xhtml";
}



}
