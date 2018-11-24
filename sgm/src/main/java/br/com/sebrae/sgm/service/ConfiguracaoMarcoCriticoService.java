package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.ConfiguracaoMarcoCritico;
import br.com.sebrae.sgm.model.Unidade;
import br.com.sebrae.sgm.model.enums.TipoMeta;

@Named
public class ConfiguracaoMarcoCriticoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());


	public List<ConfiguracaoMarcoCritico> findByTipoUnidadeCicloConfiguracao(TipoMeta tipoMeta, Unidade unidade, CicloConfiguracao cicloConfiguracao){
		List<ConfiguracaoMarcoCritico> retorno = this.em.createQuery("select cmc from ConfiguracaoMarcoCritico cmc "
				+ " where cmc.tipo = :tipoMeta and cmc.unidade = :unidade and cmc.cicloConfiguracao = :cicloConfiguracao")
				.setParameter("tipoMeta", tipoMeta).setParameter("unidade", unidade)
				.setParameter("cicloConfiguracao", cicloConfiguracao).getResultList();
		
		return retorno;
	}

}
