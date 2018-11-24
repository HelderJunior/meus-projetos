package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.EspacoOcupacional;

@FacesConverter(value = "EspacoOcupacionalConverter")
public class EspacoOcupacionalConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		EspacoOcupacional obj = (EspacoOcupacional) ctx.getExternalContext().getSessionMap()
				.get("objEspacoOcupacional-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof EspacoOcupacional) {
			EspacoOcupacional us = (EspacoOcupacional) obj;
			if (us.getEspacoOcupacionalPK() != null) {
				ctx.getExternalContext().getSessionMap()
						.put("objEspacoOcupacional-" + us.getEspacoOcupacionalPK(), obj);
				return String.valueOf(us.getEspacoOcupacionalPK());
			} else {
				return null;
			}
		}
		return null;
	}

}
