package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Ciclo;

@FacesConverter(value = "CicloConverter")
public class CicloConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Ciclo obj = (Ciclo) ctx.getExternalContext().getSessionMap().get("objCiclo-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Ciclo) {
			Ciclo us = (Ciclo) obj;
			if (us.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objCiclo-" + us.getId(), obj);
				return String.valueOf(us.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
