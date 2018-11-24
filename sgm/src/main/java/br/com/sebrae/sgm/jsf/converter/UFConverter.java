package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.com.sebrae.sgm.model.enums.UF;

@FacesConverter(value = "UFConverter")
public class UFConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent cpn, String str) {
		if (!StringUtils.isBlank(str)) {
			return UF.valueOf(str);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent cpn, Object obj) {
		if (obj instanceof UF) {
			UF uf = (UF) obj;
			return uf.toString();
		}
		return null;
	}

}
