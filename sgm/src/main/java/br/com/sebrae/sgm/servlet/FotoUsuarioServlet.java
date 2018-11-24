package br.com.sebrae.sgm.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jmimemagic.Magic;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.sebrae.sgm.model.Usuario;
import br.com.sebrae.sgm.utils.Constants;

@WebServlet(urlPatterns = "/usuario/foto")
public class FotoUsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

	private Logger log = LoggerFactory.getLogger(this.getClass());;

	@Inject
	private EntityManager em;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String nomeArquivo = request.getParameter("nome_arquivo");
		if (!StringUtils.isBlank(id)) {
			Usuario usuario = this.em.find(Usuario.class, Integer.parseInt(id));
			returnImage(request, response, usuario.getFotoFile());
		} else if (!StringUtils.isBlank(nomeArquivo)) {
			returnImage(request, response, new File(Constants.FILES_TMP_DIR + File.separator + nomeArquivo));
		} else {
			returnImage(request, response, null);
		}
	}

	private void returnImage(HttpServletRequest request, HttpServletResponse response, File img) {

		// caso a imagem solicitada nao exista no servidor, sera enviando uma imagem padrao
		if (img == null || !img.exists()) {
			img = new File(getServletContext().getRealPath("/resources/img/sem_foto.jpg"));
		}

		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		String mimeType = "";

		try {
			mimeType = Magic.getMagicMatch(img, false).getMimeType();
		} catch (Exception e) {
			mimeType = "image/png";
			log.error("Erro do definir mime type");
		}
		response.setContentType(mimeType);
		response.setHeader("Content-Length", String.valueOf(img.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + img.getName() + "\"");

		// Prepare streams.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open streams.
			input = new BufferedInputStream(new FileInputStream(img), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			// Write file contents to response.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Gently close streams.
			close(output);
			close(input);
		}
	}

	// Helpers (can be refactored to public utility class) ----------------------------------------

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// Do your thing with the exception. Print it, log it or mail it.
				e.printStackTrace();
			}
		}
	}

}
