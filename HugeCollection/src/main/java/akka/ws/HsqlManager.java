package akka.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.hsqldb.Server;

public class HsqlManager {

	Server hsqlServer = null;
	Connection connection = null;

	public HsqlManager() {
		initServer();
	}

	private void initServer() {
		// stub to get in/out of embedded db
		hsqlServer = new Server();
		hsqlServer.setLogWriter(null);
		hsqlServer.setSilent(true);
		hsqlServer.setDatabaseName(0, "passwords");
		hsqlServer.setDatabasePath(0, "file:passb");

		hsqlServer.start();

		// making a connection
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/passwords", "sa", "");
			connection.prepareStatement("drop table passwords if exists;")
					.execute();
			connection.prepareStatement(
					"create table passwords (password varchar);").execute();

		} catch (SQLException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}

		// end of stub code for in/out stub
	}

	public void addPassword(String password) throws Exception {
		try {

			connection.prepareStatement(
					"insert into passwords (password) values ('"
							+ StringEscapeUtils.escapeSql(password) + "');")
					.execute();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			throw new Exception("failed to write password in the database");
		}

	}

	public List<String> getPasswords(Integer pageIndex, Integer pageSize)
			throws Exception {
		try {
			int offset = pageIndex * pageSize;

			PreparedStatement st = connection
					.prepareStatement("select password from passwords as p limit "
							+ pageSize + " offset " + offset + ";");
			ResultSet rs = st.executeQuery();
			List<String> passList = new ArrayList<String>(pageSize);
			while (rs.next()) {
				passList.add(rs.getString(1));
			}
			return passList;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new Exception("failed to read passwords from database");
		}
	}

	public void stopServer() {
		hsqlServer.stop();
		hsqlServer = null;
	}

	public int getPasswordNumber() throws Exception {
		try {
			PreparedStatement st = connection
					.prepareStatement("select count(password) from passwords;");

			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new Exception("failed to read passwords from database");
		}
	}
}
