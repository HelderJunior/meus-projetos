package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.SolucaoEducacionalMeta;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.StatusMeta;
import br.com.sebrae.sgm.model.enums.StatusSolucaoEducacional;
import br.com.sebrae.sgm.model.enums.VinculoOcupacional;
import br.com.sebrae.sgm.utils.ValidateUtils;

@Named
public class SolucaoEducacionalMetaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional
	public void inserir(SolucaoEducacionalMeta se) throws ValidateException {
		ValidateUtils.validateThrows(se);
		em.persist(se);
	}

	@Transactional
	public void atualizar(SolucaoEducacionalMeta se) throws ValidateException {
		ValidateUtils.validateThrows(se);
		em.merge(se);
	}

	public List<SolucaoEducacionalMeta> findAll() {
		List<SolucaoEducacionalMeta> retorno = (List<SolucaoEducacionalMeta>) this.em.createNamedQuery(
				"SolucaoEducacionalMeta.findAll").getResultList();
		return retorno;
	}

	@Transactional
	public void delete(SolucaoEducacionalMeta solucaoEducacional) throws ValidateException {
		em.remove(solucaoEducacional);
	}

	public List<SolucaoEducacionalMeta> findByUsuarioAndStatusSolucao(Usuario usuario,
			StatusSolucaoEducacional statusSolucao) {
		List<SolucaoEducacionalMeta> retorno = new ArrayList<SolucaoEducacionalMeta>();

		retorno = this.em
				.createQuery(
						"Select distinct sm from SolucaoEducacionalMeta sm where sm.meta.colaborador = :usuario and sm.status = :statusSolucao")
				.setParameter("usuario", usuario).setParameter("statusSolucao", statusSolucao).getResultList();

		return retorno;
	}

	public List<SolucaoEducacionalMeta> findByUsuarioAndStatusMeta(Usuario usuario, StatusMeta statusMeta) {
		List<SolucaoEducacionalMeta> retorno = new ArrayList<SolucaoEducacionalMeta>();

		retorno = this.em
				.createQuery(
						"Select distinct sm from SolucaoEducacionalMeta sm join sm.meta.status s "
								+ " where sm.meta.colaborador = :usuario and s.status = :statusMeta")
				.setParameter("usuario", usuario).setParameter("statusMeta", statusMeta).getResultList();

		return retorno;
	}

	public BigDecimal getQtdCargaHorariaPrevista(CicloConfiguracao cicloConfiguracao, Usuario usuario) {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetas = this.em
				.createQuery(
						"Select sem from SolucaoEducacionalMeta sem where "
								+ " sem.meta.cicloConfiguracao = :cicloConfiguracao and sem.meta.colaborador = :colaborador")
				.setParameter("cicloConfiguracao", cicloConfiguracao).setParameter("colaborador", usuario)
				.getResultList();
		if (solucoesMetas != null && !solucoesMetas.isEmpty()) {
			for (SolucaoEducacionalMeta sem : solucoesMetas) {
				if (sem.getQuantidadePrevista() != null) {
					retorno = retorno.add(sem.getQuantidadePrevista());
				}
			}
		}

		return retorno;
	}

	public BigDecimal getPontuacaoPrevista(CicloConfiguracao cicloConfiguracao, Usuario usuario) {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetas = this.em
				.createQuery(
						"Select sem from SolucaoEducacionalMeta sem where "
								+ " sem.meta.cicloConfiguracao = :cicloConfiguracao and sem.meta.colaborador = :colaborador")
				.setParameter("cicloConfiguracao", cicloConfiguracao).setParameter("colaborador", usuario)
				.getResultList();
		if (solucoesMetas != null && !solucoesMetas.isEmpty()) {
			for (SolucaoEducacionalMeta sem : solucoesMetas) {
				retorno = retorno.add(sem.getPontuacaoPrevista());
			}
		}
		return retorno;
	}

	public BigDecimal getPontuacaoRealizada(CicloConfiguracao cicloConfiguracao, Usuario usuario) {
		BigDecimal retorno = BigDecimal.ZERO;
		List<SolucaoEducacionalMeta> solucoesMetas = this.em
				.createQuery(
						"Select sem from SolucaoEducacionalMeta sem where "
								+ " sem.meta.cicloConfiguracao = :cicloConfiguracao and sem.meta.colaborador = :colaborador")
				.setParameter("cicloConfiguracao", cicloConfiguracao).setParameter("colaborador", usuario)
				.getResultList();
		if (solucoesMetas != null && !solucoesMetas.isEmpty()) {
			for (SolucaoEducacionalMeta sem : solucoesMetas) {
				retorno = retorno.add(sem.getPontuacaoRealizada());
			}
		}
		return retorno;
	}

	public BigDecimal getPorcentagemHorasExecutadas(CicloConfiguracao cicloConfiguracao, Usuario usuario,
			VinculoOcupacional vinculoOcupacional) {
		BigDecimal qtdHorasPrevistas = BigDecimal.ZERO;
		BigDecimal qtdHorasExecutadas = BigDecimal.ZERO;
		BigDecimal retorno = BigDecimal.ZERO;

		List<SolucaoEducacionalMeta> solucoesMetas = this.em
				.createQuery(
						"Select sem from SolucaoEducacionalMeta sem where "
								+ " sem.meta.cicloConfiguracao = :cicloConfiguracao and sem.meta.colaborador = :colaborador and sem.vinculoOcupacional = :vinculoOcupacional")
				.setParameter("cicloConfiguracao", cicloConfiguracao).setParameter("colaborador", usuario)
				.setParameter("vinculoOcupacional", vinculoOcupacional).getResultList();

		if (solucoesMetas != null && !solucoesMetas.isEmpty()) {
			for (SolucaoEducacionalMeta sem : solucoesMetas) {
				if (sem.getQuantidadePrevista() != null)
					qtdHorasPrevistas = qtdHorasPrevistas.add(sem.getQuantidadePrevista());
				if (sem.getQuantidadeRealizada() != null)
					qtdHorasExecutadas = qtdHorasExecutadas.add(sem.getQuantidadeRealizada());
			}
		}

		if (qtdHorasPrevistas.compareTo(BigDecimal.ZERO) > 0) {
			retorno = qtdHorasExecutadas.divide(qtdHorasPrevistas, 3, RoundingMode.HALF_UP);
		}

		return retorno;
	}

}
