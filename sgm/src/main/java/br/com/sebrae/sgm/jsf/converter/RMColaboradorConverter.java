package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.RMColaborador;
import br.com.sebrae.sgm.model.Usuario;

@FacesConverter(value = "RMColaboradorConverter")
public class RMColaboradorConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		RMColaborador obj = (RMColaborador) ctx.getExternalContext().getSessionMap().get("objColaborador-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof RMColaborador) {
			RMColaborador us = (RMColaborador) obj;
			if (us.getRmColaboradorPK() != null) {
				ctx.getExternalContext().getSessionMap().put("objColaborador-" + us.getRmColaboradorPK(), obj);
				return String.valueOf(us.getRmColaboradorPK());
			} else {
				return null;
			}
		}
		return null;
	}

}
