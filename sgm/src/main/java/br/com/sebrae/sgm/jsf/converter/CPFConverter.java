package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.com.sebrae.sgm.utils.CPFUtils;

@FacesConverter(value = "CPFConverter")
public class CPFConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		if (!StringUtils.isBlank(str)) {
			return CPFUtils.clean(str);
		}
		return str;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof String) {
			if (!StringUtils.isBlank(obj.toString())) {
				return CPFUtils.formatNoValidation(obj.toString());
			}
			return obj.toString();
		}
		return null;
	}

}
