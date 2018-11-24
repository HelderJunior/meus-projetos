package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Validacao;

@FacesConverter(value = "VConverter")
public class ValidacaoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		Validacao obj = (Validacao) ctx.getExternalContext().getSessionMap().get("objValidacao-" + str);
		return obj;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof Validacao) {
			Validacao us = (Validacao) obj;
			if (us.getId() != null) {
				ctx.getExternalContext().getSessionMap().put("objValidacao-" + us.getId(), obj);
				return String.valueOf(us.getId());
			} else {
				return null;
			}
		}
		return null;
	}

}
