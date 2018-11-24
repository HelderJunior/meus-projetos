package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Unidade;

@FacesConverter(value = "UnidadeConverter")
public class UnidadeConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Unidade obj = (Unidade) ctx.getExternalContext().getSessionMap().get("objUnidade-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Unidade) {
			Unidade us = (Unidade) obj;
			// us = HibernateUtils.initializeAndUnproxy(us);
			if (us.getUnidadePK() != null) {
				ctx.getExternalContext().getSessionMap().put("objUnidade-" + us.getUnidadePK(), us);
				return String.valueOf(us.getUnidadePK());
			} else {
				return null;
			}
		}
		return null;
	}

}
