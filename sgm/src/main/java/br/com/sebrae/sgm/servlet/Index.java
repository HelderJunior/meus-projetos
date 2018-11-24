package br.com.sebrae.sgm.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.sebrae.sgm.model.Perfil;

@WebServlet(urlPatterns = "/pages/index.xhtml")
public class Index extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(getIndex(req));
	}

	public static String getIndex(HttpServletRequest req) {
		if (req.isUserInRole(Perfil.COLABORADOR_CHAVE) 
				|| req.isUserInRole(Perfil.ADM_MASTER)
				|| req.isUserInRole(Perfil.ADM_UF_NACIONAL) 
				|| req.isUserInRole(Perfil.DIRETOR)
				|| req.isUserInRole(Perfil.ASSESSOR)
				|| req.isUserInRole(Perfil.CHEFE_GABINETE)
				|| req.isUserInRole(Perfil.GERENTE)
				|| req.isUserInRole(Perfil.GERENTE_ADJUNTO)) {
			return req.getContextPath() + "/pages/home.xhtml";
		} else if (req.isUserInRole(Perfil.COMITE)) {
			return req.getContextPath() + "/pages/comite/desempenho/gestao-metas.xhtml";
		}else if (req.isUserInRole(Perfil.AUDITOR)) {
			return req.getContextPath() + "/pages/auditor/listar.xhtml";
		}
		return req.getContextPath() + "/error/403.xhtml";
	}

	public static String getIndexJSF(HttpServletRequest req) {
		if (req.isUserInRole(Perfil.COLABORADOR_CHAVE) || req.isUserInRole(Perfil.ADM_MASTER)
				|| req.isUserInRole(Perfil.ADM_UF_NACIONAL)) {
			return "/pages/home?faces-redirect=true";
		} else if (req.isUserInRole(Perfil.COMITE)) {
			return "/pages/comite/desempenho/gestao-metas?faces-redirect=true";
		} else if (req.isUserInRole(Perfil.GERENTE) || req.isUserInRole(Perfil.GERENTE_ADJUNTO)) {
			return "/pages/gestor/desempenho/gestao-metas?faces-redirect=true";
		}
		return req.getContextPath() + "/error/403?faces-redirect=true";
	}
}
