package br.com.sebrae.sgm.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import br.com.sebrae.sgm.model.AvaliadorCiclo;
import br.com.sebrae.sgm.model.Ciclo;
import br.com.sebrae.sgm.model.CicloConfiguracao;
import br.com.sebrae.sgm.model.Perfil;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.Fase;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;

@Named
public class PerfilService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	public List<Perfil> bindByChaves(List<String> chaves) {
		Session session = (Session) em.getDelegate();
		Criteria criteria = session.createCriteria(Perfil.class);
		criteria.add(Restrictions.in("chave", chaves));
		return criteria.list();
	}

	public Perfil load(String chave) {
		return this.em.find(Perfil.class, chave);
	}

	public List<Perfil> findByUsuario(Usuario user) {
		List<Perfil> retorno = this.em
				.createQuery("select distinct p from Perfil p join p.usuarioPerfilList up where up.usuario = :usuario")
				.setParameter("usuario", user).getResultList();
		
		return (List<Perfil>) (retorno == null ? Collections.emptyList() : retorno);
	}

	public boolean isGerente(Ciclo ciclo, Usuario usuario, TipoConfiguracaoCiclo tipoConfiguracao) {
		if (tipoConfiguracao == null) {
			List<CicloConfiguracao> configuracoes = ciclo.getConfiguracoes();
			if (configuracoes != null) {
				for (CicloConfiguracao cicloConfiguracao : configuracoes) {
					if(isGerente(cicloConfiguracao, usuario, null)){
						return true;
					}
				}
			}
		} else {
			CicloConfiguracao cicloConfiguracao = ciclo.getConfiguracaoByTipo(tipoConfiguracao);
			if(cicloConfiguracao != null){
				return isGerente(cicloConfiguracao, usuario, null);
			}
		}

		return false;
	}

	public boolean isGerente(CicloConfiguracao cicloConfiguracao, Usuario usuario, Fase fase) {
		if (cicloConfiguracao.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESEMP) {
			if (cicloConfiguracao.getAvaliadores() != null && !cicloConfiguracao.getAvaliadores().isEmpty()) {
				List<AvaliadorCiclo> avaliadores = cicloConfiguracao.getAvaliadores();
				for (AvaliadorCiclo ac : avaliadores) {
					if (ac.getUsuario().equals(usuario)) {
						if (ac.getPerfil().getChave().equals(Perfil.GERENTE)
								|| ac.getPerfil().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
							return true;
						}
					}
				}
			}
		}
		if (cicloConfiguracao.getTipoConfiguracao() == TipoConfiguracaoCiclo.DESENV) {
			if (cicloConfiguracao.getAvaliadores() != null && !cicloConfiguracao.getAvaliadores().isEmpty()) {
				List<AvaliadorCiclo> avaliadores = cicloConfiguracao.getAvaliadores();
				for (AvaliadorCiclo ac : avaliadores) {
					if (ac.getUsuario().equals(usuario)) {
						if (ac.getPerfil().getChave().equals(Perfil.GERENTE)
								|| ac.getPerfil().getChave().equals(Perfil.GERENTE_ADJUNTO)) {
							if (fase == Fase.P) {
								if (ac.getFasePactuacao()) {
									return true;
								}
							}
							if (fase == Fase.R) {
								if (ac.getFaseRepactuacao()) {
									return true;
								}
							}
							if (fase == Fase.R) {
								if (ac.getFaseRepactuacao()) {
									return true;
								}
							}
							if (fase == Fase.A) {
								if (ac.getFaseAjustes()) {
									return true;
								}
							}
							if (fase == Fase.V) {
								if (ac.getFaseValidacao()) {
									return true;
								}
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
