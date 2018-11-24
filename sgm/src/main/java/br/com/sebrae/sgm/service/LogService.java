package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.enums.TipoLog;
import br.com.sebrae.sgm.model.enums.TipoMeta;

public class LogService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	private static final Logger log = LoggerFactory.getLogger(LogService.class);

	public List<Object[]> findHistoricoMeta(TipoLog tipoLog, TipoMeta tipoMeta, String codigoMeta, String cpfUsuario,
			Ciclo ciclo) {
		AuditReader reader = AuditReaderFactory.get(em);

		AuditQuery query = reader.createQuery().forRevisionsOfEntity(Meta.class, false, true);

		if (tipoMeta != null) {
			query.add(AuditEntity.property("tipo").eq(tipoMeta));
		}

		if (!StringUtils.isBlank(codigoMeta)) {
			query.add(AuditEntity.property("codigo").eq(codigoMeta));
		}

		if (!StringUtils.isBlank(cpfUsuario)) {
			query.add(AuditEntity.revisionProperty("usuario").eq(cpfUsuario));
		}

		if (tipoLog != null) {
			query.add(AuditEntity.revisionProperty("tipoLog").eq(tipoLog));
		}

		query.addOrder(AuditEntity.revisionNumber().desc());
		List<Object[]> results = query.getResultList();
		return results;
	}

	public Object[] findMetaByRevision(Number revisionNumber) {
		AuditReader reader = AuditReaderFactory.get(em);
		Object[] results = null;

		AuditQuery query = reader.createQuery().forRevisionsOfEntity(Meta.class, false, true);
		query.add(AuditEntity.revisionNumber().eq(revisionNumber));

		try {
			results = (Object[]) query.getSingleResult();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}

		return results;
	}

	public Object[] findMetaByPreviousRevision(Integer revisaoAtual, Serializable idEntidade) {
		AuditReader reader = AuditReaderFactory.get(em);
		Number prevRevision = (Number) reader.createQuery().forRevisionsOfEntity(Meta.class, false, true)
				.addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(idEntidade))
				.add(AuditEntity.revisionNumber().lt(revisaoAtual)).getSingleResult();
		if (prevRevision != null) {
			return findMetaByRevision(prevRevision);
		} else {
			return null;
		}
	}

	public List<Object[]> findByIdMeta(Integer idMeta) {
		AuditReader reader = AuditReaderFactory.get(em);
		List<Object[]> results = null;

		AuditQuery query = reader.createQuery().forRevisionsOfEntity(Meta.class, false, true);
		query.add(AuditEntity.property("id").eq(idMeta));
		query.addOrder(AuditEntity.revisionNumber().desc());

		results = query.getResultList();

		return results;
	}
}
