/**
 *   Copyright (C) 2009, 2010 
 *    Nicky Sandhu
 *    State of California,
 *    Department of Water Resources.
 *    This file is part of DSM2 Grid Map
 *    The DSM2 Grid Map is free software: 
 *    you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *    DSM2 Grid Map is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with DSM2 Grid Map.  If not, see <http://www.gnu.org/licenses>.
 */
package gov.ca.bdo.modeling.dsm2.map.server;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RequestAccessServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String email = req.getParameter("email");
		if ((email != null) && email.contains("@")) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			String msgBody = "Access requested by email: " + email;

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress("admin@dsm2grid.appspot.com",
						"DSM2 Grid Map Admin"));
				// FIXME: obtain all admin level users and send to them as well
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						"psandhu@water.ca.gov", "Admins"));
				msg.setSubject("Account authorization request");
				msg.setText(msgBody);
				Transport.send(msg);

			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}
