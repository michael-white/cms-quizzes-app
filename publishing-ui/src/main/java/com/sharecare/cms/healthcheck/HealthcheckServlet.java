package com.sharecare.cms.healthcheck;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;

import info.magnolia.context.MgnlContext;

public class HealthcheckServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {

		try {

			MgnlContext.getJCRSession("website");
			response.setContentType(MediaType.APPLICATION_JSON);
			PrintWriter out = response.getWriter();
			out.println("Healthcheck Success");

		} catch (RepositoryException e) {
			response.setContentType(MediaType.APPLICATION_JSON);
			response.setStatus(500);
			PrintWriter out = response.getWriter();
			out.println("Healthcheck Failed");

		}


	}
}
