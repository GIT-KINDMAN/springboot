package com.example.springboot.common.security.handler;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Service("logoutSuccessHandler")
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("CustomLogoutSuccessHandler - onLogoutSuccess() ...");
		if (authentication != null && authentication.getDetails() != null) {
			try {
				request.getSession().invalidate();

				String result = objectMapper.writeValueAsString(ResponseDTO.builder()
																			.status(HttpStatus.OK)
																			.message(Payload.SIGN_OUT_OK)
																			.build());
				result = URLEncoder.encode(result, "UTF-8");
				response.getWriter().write(result);
				return;
			} catch (Exception e) {
				logger.error("SERVER ERROR CustomLogoutSuccessHandler - onLogoutSuccess()", e);
			}
		}
		logger.info("Authentication is Null CustomLogoutSuccessHandler - onLogoutSuccess()");
		response.getWriter().write(
				objectMapper.writeValueAsString(
						ResponseDTO.builder()
									.status(HttpStatus.INTERNAL_SERVER_ERROR)
									.message(Payload.SIGN_OUT_FAIL)
									.build()
				)
		);
	}
}