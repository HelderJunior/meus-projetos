package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.com.sebrae.sgm.utils.CEPUtils;

@FacesConverter(value = "CEPConverter")
public class CEPConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		if (!StringUtils.isBlank(str)) {
			return CEPUtils.clean(str);
		}
		return str;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof String) {
			String retorno = ((String) obj);
			if (!StringUtils.isBlank(retorno))
				return CEPUtils.format(retorno);
		}
		return null;
	}

}
