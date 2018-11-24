package br.com.sebrae.sgm.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.sebrae.sgm.utils.CPFUtils;

@FacesValidator("CPFValidator")
public class CPFValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		if (value instanceof String) {
			String cpf = (String) value;

			if (!CPFUtils.validate(cpf)) {
				FacesMessage msg = new FacesMessage("CPF inv\u00E1lido",
						"CPF inv\u00E1lido");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}

	}

}
