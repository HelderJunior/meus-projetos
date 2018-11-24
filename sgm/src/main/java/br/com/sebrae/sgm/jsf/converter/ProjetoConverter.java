package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Projeto;

@FacesConverter(value = "ProjetoConverter")
public class ProjetoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Projeto obj = (Projeto) ctx.getExternalContext().getSessionMap().get("objProjeto-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Projeto) {
			Projeto us = (Projeto) obj;
			if (us.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objProjeto-" + us.getId(), obj);
				return String.valueOf(us.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
