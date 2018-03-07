package com.charts.backend.restful;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/dbtojsonprovider")
public class ServeMySQLDataOnRestApi {

	static java.sql.Statement stmt;
	static Connection connection;
	



	public ServeMySQLDataOnRestApi() {
		if (connection == null) {
			dataBaseConnection();
		} else {
			System.out.println("DB Connection Already Established!");
		}
	}

	@GET
	@Produces("application/json")
	public String readFromDB() throws SQLException {
		ResultSet rs = null;
		
		JSONObject finalResultObject = new JSONObject();
		JSONArray duration = new JSONArray();
		JSONArray plotParameterDepartment = new JSONArray();

		String sql;

		sql = "select prob1,sum(TIME_TO_SEC(duration)/3600)as duration from downtime "
				+ "where duration<>ISNULL(duration) GROUP BY prob1 order by duration desc";
		rs = stmt.executeQuery(sql);

		while (rs.next()) {
			plotParameterDepartment.put(rs.getString("prob1"));
			duration.put(Double.parseDouble(rs.getString("duration")));
		}
		rs.close();
		finalResultObject.put("problem", plotParameterDepartment);
		finalResultObject.put("duration", duration);
		System.out.println("finalResultObject full  :" + finalResultObject);
		return finalResultObject.toString();

	}

	@Path("{c}")
	@GET
	@Produces("application/json")
	public String readFromDbWCustomDateTime(@PathParam("c") String c) throws SQLException {
		
		
		JSONObject dateTimeObject = new JSONObject(c);  // Object Received from Front end
		JSONObject finalResultObject = new JSONObject(); // Returning Final JSON Object
		JSONArray plotParameterDepartment = new JSONArray();
		JSONArray duration = new JSONArray();
		JSONArray plotParameterProblem = new JSONArray();
		JSONArray plotParameterReason1 = new JSONArray();
		JSONArray duration1 = new JSONArray();
		JSONArray durationReason1 = new JSONArray();
		JSONArray plotParameterProblem2 = new JSONArray();
		JSONArray durationProbelm2 = new JSONArray();
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;		
		
		
		
		String sqlQuery = "";
		System.out.println("In parameter");
		System.out.println("Received from Frontend " + c);
		
		///////////////////////////////////////// Pie Chart Query///////////////////////////////////

		sqlQuery = "select d.name as name, sum(TIME_TO_SEC(duration)/3600) as duration from downtime "
				+ "inner join department d on downtime.department = d.id "
				+ "where duration<>ISNULL(duration) and Stop_time between '" + dateTimeObject.getString("from")
				+ "' and  '" + dateTimeObject.getString("to") + "' GROUP BY d.name order by duration desc;";

		rs = stmt.executeQuery(sqlQuery); // Executing Query to Database

		// Parsing Result Sets
		while (rs.next()) {

			plotParameterDepartment.put(rs.getString("name"));
			duration.put(Double.parseDouble(rs.getString("duration")));
		}
		rs.close(); // Closing Resultset Object

		finalResultObject.put("problem", plotParameterDepartment);
		finalResultObject.put("duration", duration);
		System.out.println("finalResultObject Returned to Ajax Call 1 :" + finalResultObject);

		/////// 1st Query execution and parsing ends here
		sqlQuery = "";

		///////////////////////////////////////// Probem1///////////////////////////////////

		sqlQuery = "select prob1, sum(TIME_TO_SEC(duration)/3600)as duration from downtime"
				+ " where duration<>ISNULL(duration)  and Stop_time between '" + dateTimeObject.getString("from")
				+ "' and  '" + dateTimeObject.getString("to") + "' GROUP BY prob1 order by duration desc";

		System.out.println("sqlQuery:   " + sqlQuery);
		rs1 = stmt.executeQuery(sqlQuery);

		while (rs1.next()) {

			plotParameterProblem.put(rs1.getString("prob1"));
			duration1.put(Double.parseDouble(rs1.getString("duration")));

		}

		rs1.close();
		
		finalResultObject.put("problem1", plotParameterProblem);
		finalResultObject.put("duration1", duration1);
		System.out.println("finalResultObject Problem1 added  :" + finalResultObject);

		///////////////////////////////////////// Reason1///////////////////////////////////

		sqlQuery = "select reason1, sum(TIME_TO_SEC(duration)/3600)as duration from downtime"
				+ " where duration<>ISNULL(duration)  and Stop_time between '" + dateTimeObject.getString("from")
				+ "' and  '" + dateTimeObject.getString("to") + "' GROUP BY reason1 order by duration desc";

		System.out.println("sqlQuery:   " + sqlQuery);
		rs2 = stmt.executeQuery(sqlQuery);

		while (rs2.next()) {

			plotParameterReason1.put(rs2.getString("reason1"));
			durationReason1.put(Double.parseDouble(rs2.getString("duration")));

		}

		rs2.close();
		
		finalResultObject.put("reason1", plotParameterReason1);
		finalResultObject.put("durationReason1", durationReason1);
		System.out.println("finalResultObject reason1 added  :" + finalResultObject);

		///////////////////////////////////////// Problem2///////////////////////////////////

		sqlQuery = "select prob2, sum(TIME_TO_SEC(duration)/3600)as duration from downtime"
				+ " where duration<>ISNULL(duration)  and Stop_time between '" + dateTimeObject.getString("from")
				+ "' and  '" + dateTimeObject.getString("to") + "' GROUP BY prob2 order by duration desc";

		System.out.println("sqlQuery:   " + sqlQuery);
		rs3 = stmt.executeQuery(sqlQuery);

		while (rs3.next()) {

			plotParameterProblem2.put(rs3.getString("prob2"));
			durationProbelm2.put(Double.parseDouble(rs3.getString("duration")));

		}

		rs3.close();
		
		finalResultObject.put("problem2", plotParameterProblem2);
		finalResultObject.put("durationProbelm2", durationProbelm2);
		System.out.println("finalResultObject reason1 added  :" + finalResultObject);
		
		
		

		return finalResultObject.toString(); // Returning JSON Object to Front
												// end

	}

	public void dataBaseConnection() {

		System.out.println("-------- MySQL JDBC Connection  ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		System.out.println("MySQL JDBC Driver Registered!");

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "permitsensys");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("Coonection Established with Database Server Successfully!");
		} else {
			System.out.println("Failed to make connection!");
		}
		try {
			stmt = connection.createStatement();
			String useDB = "USE `oee`";
			stmt.executeUpdate(useDB);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
