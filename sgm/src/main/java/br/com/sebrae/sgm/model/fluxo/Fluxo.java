package br.com.sebrae.sgm.model.fluxo;

import br.com.sebrae.sgm.exception.ValidateException;
import br.com.sebrae.sgm.model.Meta;
import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.model.enums.TipoConfiguracaoCiclo;
import br.com.sebrae.sgm.model.enums.TipoGrupo;

public interface Fluxo {
	
	void isPodeEnviarAprovacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void enviarAprovacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	
	void isPodeAprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void aprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);

	void isPodeCancelarSuperior(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void cancelarSuperior(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);

	void isPodeEnviarObservacaoSuperior(Meta meta) throws ValidateException;
	void enviarObservacaoSuperior(Meta meta, String observacao);

	void isPodeEnviarValidacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void enviarValidacao(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);

	void isPodeEnviarObservacaoComite(Meta meta) throws ValidateException;
	void enviarObservacaoComite(Meta meta, Usuario usuario, String observacao);
	
	void isPodeValidarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void validarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	
	void isPodeEnviarObservacaoAuditor(Meta meta) throws ValidateException;
	void enviarObservacaoAuditor(Meta meta, String observacao);
	
	void isPodeAtestarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void atestarMeta(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	
	void isPodeEnviarObservacaoAuditorGerente(Meta meta) throws ValidateException;
	void enviarObservacaoAuditorGerente(Meta meta, String observacao);
	
	void isPodeEnviarComiteAuditor(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void enviarComiteAuditor(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	
	void isPodeReprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	void reprovar(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	
	void enviarObservacaoColaborador(Meta meta, Usuario usuario, String observacao); 
	void getenteEnviarObservacaoComite(Meta meta, Usuario usuario, String observacao);
	
	void enviarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario);
	void isPodeEnviarComite(Meta meta, TipoConfiguracaoCiclo tipoConfiguracao, TipoGrupo tipoCalendario) throws ValidateException;
	
	
}
