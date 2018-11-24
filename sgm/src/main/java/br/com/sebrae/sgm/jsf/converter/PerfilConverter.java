package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Perfil;

@FacesConverter(value = "PerfilConverter")
public class PerfilConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Perfil obj = (Perfil) ctx.getExternalContext().getSessionMap().get("objPerfil-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Perfil) {
			Perfil us = (Perfil) obj;
			if (us.getChave() != null) {
				ctx.getExternalContext().getSessionMap().put("objPerfil-" + us.getChave(), obj);
				return String.valueOf(us.getChave());
			} else {
				return null;
			}
		}
		return null;
	}

}
