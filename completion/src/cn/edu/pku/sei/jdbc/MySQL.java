package cn.edu.pku.sei.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.edu.pku.sei.utils.EnvironmentProperty;



public class MySQL {
	
	
	private Connection connection;
	
	public MySQL() {
		initConnection();
	}

	private void initConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String addr = EnvironmentProperty.readConf("MysqlAddr");
			connection=DriverManager.getConnection(addr,
					"useradmin", "useradmin");
		}
		catch (Exception e) {
			connection=null;
		}
	}

	public ArrayList<HashMap<String, Object>> execute(String sql) throws Exception {
		String _sql=new String(sql);
		System.out.println(_sql);
		try {
			if (connection==null || connection.isClosed())
				initConnection();
			if (connection==null)
				return null;
			Statement statement=connection.createStatement();
			ArrayList<HashMap<String, Object>> arrayList=new ArrayList<HashMap<String, Object>>();
			if (_sql.toLowerCase(Locale.getDefault()).startsWith("select")) {
				ResultSet resultSet=statement.executeQuery(_sql);
				if (resultSet==null)
					return null;
				ResultSetMetaData metaData=resultSet.getMetaData();
				int colnums=metaData.getColumnCount();
				
				while (resultSet.next()) {
					HashMap<String, Object> map=new HashMap<String, Object>();
					for (int i=1;i<=colnums;i++) {
						String name=metaData.getColumnName(i);
						Object value=resultSet.getObject(i);
						map.put(name, value);
					}
					arrayList.add(map);
				}
				return arrayList.size()==0? null: arrayList;

			}
			else {
				statement.executeUpdate(_sql);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("wrong sql: "  + _sql);
		} finally {
			close();
		}
	}


	public void close() {
		try {
			connection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			connection=null;
		}
	}
	
	public static String convertStr(Object str){
		if(str==null) return "''";
		else return "'" + str.toString().replaceAll(".*([';]+|(--)+).*", " ") + "'";
	}
}