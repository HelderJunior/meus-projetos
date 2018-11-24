package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.FormaAquisicao;

@FacesConverter(value = "FormaAquisicaoConverter", forClass = br.com.sebrae.sgm.model.FormaAquisicao.class)
public class FormaAquisicaoConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if (value != null && !value.isEmpty()) {
			return (FormaAquisicao) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value instanceof FormaAquisicao) {
			FormaAquisicao entity = (FormaAquisicao) value;
			if (entity != null && entity instanceof FormaAquisicao && entity.getId() != null) {
				uiComponent.getAttributes().put(entity.getId().toString(), entity);
				return entity.getId().toString();
			}
		}
		return "";
	}
}