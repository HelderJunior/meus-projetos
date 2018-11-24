package br.com.sebrae.sgm.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.sebrae.sgm.model.Unidade;



	@FacesConverter(value="UnidadeAutocompleteConverter", forClass = br.com.sebrae.sgm.model.Unidade.class)
	public class UnidadeAutocompleteConverter implements Converter {
	    @Override
	    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
	        if (value != null && !value.isEmpty()) {
	            return (Unidade) uiComponent.getAttributes().get(value);
	        }
	        return null;
	    }

	    @Override
	    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
	        if (value instanceof Unidade) {
	            Unidade entity = (Unidade) value;
	            if (entity != null && entity instanceof Unidade && entity.getUnidadePK() != null) {
	                uiComponent.getAttributes().put(entity.getUnidadePK().toString(), entity);
	                return entity.getUnidadePK().toString();
	            }
	        }
	        return "";
	    }
	}

