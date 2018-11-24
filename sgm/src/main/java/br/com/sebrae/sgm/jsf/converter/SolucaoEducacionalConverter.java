package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.SolucaoEducacional;

@FacesConverter(value = "SEConverter")
public class SolucaoEducacionalConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		SolucaoEducacional obj = (SolucaoEducacional) ctx.getExternalContext().getSessionMap().get("objSE-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof SolucaoEducacional) {
			SolucaoEducacional se = (SolucaoEducacional) obj;
			if (se.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objSE-" + se.getId(), obj);
				return String.valueOf(se.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
