package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.HashMap;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;


public class AspireOAuthMultileggedClient extends Thread{

	private static SecureRandom random = new SecureRandom();
	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	private static final String host = "http://localhost:9000";
	
	//private static int count = 0;
	String tenantId = "RASE";
	//private static String env = "prod";
	//private static Long ipTenantId = 3L;
	String baseURL;
	
	public AspireOAuthMultileggedClient(String baseURL) {
		this.baseURL = baseURL;
	}
	
	public AspireOAuthMultileggedClient(String baseURL, String tenantId) {
		this.baseURL = baseURL;
		this.tenantId = tenantId;
	}
		
	private String initOAuthAccessConnection(String tenantId, String score) {
		//final String tokenCheckURL = baseURL+ "/oauth/"+ tenantId + "/checktoken";
		String responseString = "";
		try {
			OAuthClient client = new OAuthClient(new HttpClient4());
			boolean validAccessor = false;
			OAuthMessage responseCheck = null; 
			OAuthAccessor accessor = null;

			
			if (!validAccessor) {
				System.out.println("Generate token for Start >> " + tenantId);

				String requestTokenURL = baseURL + "/oauth/" + tenantId
						+ "/auth/request_token";
				String accessTokenURL = baseURL + "/oauth/" + tenantId
						+ "/auth/access_token";
				String authorizeURL = baseURL + "/oauth/" + tenantId
						+ "/auth/authorize";
				
				OAuthServiceProvider provider = new OAuthServiceProvider(requestTokenURL, authorizeURL, accessTokenURL);
				OAuthConsumer consumer = new OAuthConsumer(null, "yJM4cX9HeK13ldEG", "Pjw2uzTBTx2edJZk", provider);
				accessor = new OAuthAccessor(consumer);
				
				// Get a RequestToken
				client.getRequestToken(accessor, OAuthMessage.POST, null);
				
				System.out.println("Generate token for Start >> 333" + accessor + " ; " + accessor.requestToken);
				
				// Authorize the RequestToken, receive a Verifier
				OAuthMessage authResponse = client.invoke(accessor,
						OAuthMessage.POST, provider.userAuthorizationURL,
						OAuth.newList(OAuth.OAUTH_TOKEN, accessor.requestToken, OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret));
				System.out.println("Generate token for Start >> 444" + tenantId);
				
				String verifier = authResponse.getParameter(OAuth.OAUTH_VERIFIER);
				authResponse.requireParameters(OAuth.OAUTH_VERIFIER);
				System.out.println("Generate token for Start >> 444" + "verifier ; " +  verifier);
				
				// Use the Verifier to trade the authorized RequestToken for an AccessToken
				client.getAccessToken(accessor, OAuthMessage.POST, OAuth.newList(OAuth.OAUTH_VERIFIER, verifier));
				System.out.println("Generate token for End >> " + tenantId);

				// OAuth authentication done, call service url
				String serviceURL = baseURL + "/aspireapi/" + tenantId + "/student/scoreresult";
				HashMap<String, String> map = new HashMap<String,String>();
				map.put("score",score);
				
				if (client != null) {
					responseCheck = client.invoke(accessor, OAuthMessage.POST,serviceURL, map.entrySet());
				}
				
				if (responseCheck != null) {
					responseString = getResponseString(responseCheck);
				}
			}
		} catch (URISyntaxException uriSyntaxException) {
			uriSyntaxException.printStackTrace();
			System.out.println("Error in Generate token for - uriSyntaxException >> " + tenantId);
		} catch (OAuthException oAuthException) {
			oAuthException.printStackTrace();
			System.out.println("Error in Generate token for - oAuthException >> " + tenantId);
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.out.println("Error in Generate token for - ioException >> " + tenantId);
		}
		
		return responseString;
	}
	
	private static String getResponseString(OAuthMessage responseCheck) {
		final StringBuffer jsonOut = new StringBuffer("");
		
		try {
			final InputStream is1 = responseCheck.getBodyAsStream();
			final BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));
			String line1;
			while ((line1 = br1.readLine()) != null) {
				jsonOut.append(line1);
			}
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
		
		return jsonOut.toString();
	}
	
	
	public String saveScore(String tenantId, String score) {
		String responseString = "";
		/*final String serviceURL = baseURL + "/aspireapi/" + tenantId
				+ "/student/scoreresult";*/

		responseString = initOAuthAccessConnection(tenantId, score);

		return responseString;
	}

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(random.nextInt(AB.length())));
		return sb.toString();
	}
	
	/*class AspireDBConstantsDev {
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		
		static final String DB_URL = "jdbc:mysql://aspire-dev.ctvi1qht8mzy.us-east-1.rds.amazonaws.com/aspiredev";
		
		static final String USER = "root";
		
		static final String PASS = "mypassword";
	}
	
	class AspireDBConstantsTest {
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		
		static final String DB_URL = "jdbc:mysql://awstactmdbm01-ext.hosts.pearsondev.com/aspiretest";
		
		static final String USER = "aspire";
		
		static final String PASS = "mypassword";
	}
	
	class AspireDBConstantsProd {
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		
		static final String DB_URL = "jdbc:mysql://awspactmdbm01-ext.hosts.pearsondev.com/aspirestudy";
		
		static final String USER = "aspirestudy";
		
		static final String PASS = "Pwaspire!";
	}*/
	
	//private static Connection connection = null;

	/*private static String getSQLQuery(Long iptenantID) {
		String sqlQueryAllTenants = "";
		System.out.println("iptenantID iptenantID" + iptenantID);
		if (iptenantID == null) {
//			sqlQueryAllTenants = 
//					  "select assRult.scoreXML, t.id from Assessment_Result assRult, " +
//					  "Tenant t, BarCodeFormMapping bcfm, StudentFormData sfd, " +
//					  "Student s where assRult.externalLoginKey = bcfm.serialNumber " +
//					  "and sfd.id = bcfm.studentForm_id " +
//					  "and sfd.student_id = s.id and s.tenant_id = t.id";
		} else {
//			sqlQueryAllTenants = 
//					"select assRult.scoreXML, t.id from Assessment_Result assRult, " +
//					  "Tenant t, BarCodeFormMapping bcfm, StudentFormData sfd, " +
//					  "Student s where assRult.externalLoginKey = bcfm.serialNumber " +
//					  "and sfd.id = bcfm.studentForm_id " +
//					  "and sfd.student_id = s.id and s.tenant_id = t.id  " +
//					//  "and assRult.externalLoginKey = '8867597068' " +
//					  "and t.id = " + iptenantID + " order by assRult.assessment_result_id limit 40000 , 4000";
//			
			
			sqlQueryAllTenants = 
					"Select distinct(ss_tmp.externalLoginKey) logins " +
					"from " +
					"(select ss.externalLoginKey, sss.id " +
					"from studentsessionstatus ss, studentsessionscore sss, " +
					"(select ar.externalloginkey " +
					"from assessment_result ar " +
					"where testmapId not in ('BH000000000000003176','BH000000000000003177','BH000000000000003178','BH000000000000003179','BH000000000000003180','JW000000000000002153','JW000000000000002154','JW000000000000002155')) ar_tmp " +
					"where ss.tenant_id = 20 and sss.externalloginkey = ss.externalloginkey " +
					"and ss.externalloginkey = ar_tmp.externalloginkey) ss_tmp left join studentitemscore sis " +
					"on ss_tmp.id = sis.studentSessionScore_id " +
					"Where sis.id is null;";
			
		}
		return sqlQueryAllTenants;
	}
	
	
	private static void connectDB(String URL, String userName, String pwd) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(URL, userName, pwd);
	}
	
	
	private static void closeDB() throws SQLException {
		if(connection!=null)
			connection.close();
	}*/
	
	/*public static void main (String args[]) {
		AspireOAuthMultileggedClient clientAccess = new AspireOAuthMultileggedClient(
				host, tenantId);
	
		try {
			if (env.equals("dev")) {
				connectDB(AspireDBConstantsDev.DB_URL, 
						  AspireDBConstantsDev.USER,
						  AspireDBConstantsDev.PASS);
			} else if (env.equals("test")) {
				connectDB(AspireDBConstantsTest.DB_URL, 
						AspireDBConstantsTest.USER,
						AspireDBConstantsTest.PASS);
			} else if (env.equals("prod")) {
				connectDB(AspireDBConstantsProd.DB_URL, 
						AspireDBConstantsProd.USER,
						AspireDBConstantsProd.PASS);
			}
			
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(getSQLQuery(ipTenantId));
			System.out.println("resultSet " + resultSet);
			while (resultSet.next()) {
				System.out.println("resultSet 2222" + resultSet);
				String login = resultSet.getString("logins");
				String sqlQueryAllTenants = "select assRult.scoreXML from Assessment_Result assRult where assRult.externalLoginKey ='" + login + "'";
				Statement statement2 = connection.createStatement();
				ResultSet resultSet2 = statement2.executeQuery(sqlQueryAllTenants);
				System.out.println("resultSet " + resultSet2);
				while (resultSet2.next()) {
					System.out.println("resultSet 8888" + resultSet2);
					String score = resultSet2.getString("assRult.scoreXML");
					System.out.println("score score   " + score);
					if(score != null){
						System.out.println(clientAccess.saveScore(tenantId, score));
					}
				}
				
			}
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			try {
				closeDB();
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				// Handle errors for Class.forName
				e.printStackTrace();
			}
		}
	}*/
	
	public static void main (String args[]) {
		AspireOAuthMultileggedClient clientAccess = new AspireOAuthMultileggedClient(
				host, "RASE");
		System.out.println(clientAccess.saveScore("RASE", "Check"));
	}
}
