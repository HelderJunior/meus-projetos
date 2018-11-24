package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Usuario;

@FacesConverter(value = "UsuarioConverter")
public class UsuarioConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Usuario obj = (Usuario) ctx.getExternalContext().getSessionMap().get("objUsuario-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Usuario) {
			Usuario us = (Usuario) obj;
			if (us.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objUsuario-" + us.getId(), obj);
				return String.valueOf(us.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
