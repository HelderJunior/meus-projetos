package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Indicador;

@FacesConverter(value = "IndicadorConverter")
public class IndicadorConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Indicador obj = (Indicador) ctx.getExternalContext().getSessionMap().get("objIndicador-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Indicador) {
			Indicador us = (Indicador) obj;
			if (us.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objIndicador-" + us.getId(), obj);
				return String.valueOf(us.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
