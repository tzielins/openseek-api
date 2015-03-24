package org.fairdom;

import java.io.IOException;
import java.util.Properties;

public class TestHelper {
	private static Credentials credentials = null;

	public static Credentials readCredentials() throws IOException {
		if (credentials == null) {
			Properties props = new Properties();
			props.load(TestHelper.class
					.getResourceAsStream("/test-credentials.properties"));
			credentials = new Credentials(props.getProperty("username"),
					props.getProperty("password"),
					props.getProperty("endpoint"));
		}
		return credentials;
	}

	public static class Credentials {
		private String username;
		private String password;
		private String endpoint;

		public Credentials(String username, String password, String endpoint) {
			this.setUsername(username);
			this.setPassword(password);
			this.setEndpoint(endpoint);

		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getEndpoint() {
			return endpoint;
		}

		private void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		private void setUsername(String username) {
			this.username = username;
		}

		private void setPassword(String password) {
			this.password = password;
		}
	};

}
