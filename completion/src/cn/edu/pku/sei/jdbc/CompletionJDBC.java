package cn.edu.pku.sei.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.pku.sei.model.Completion;

public class CompletionJDBC {

	public static Completion findCompletionByClassAndMethod(String classname,
			String method) throws Exception {
		Completion c = findCompletionByProperty("class='" + classname
				+ "' and method='" + method + "'");
		return c;
	}

	public static List<Completion> findCompletionsByClass(String classname)
			throws Exception {
		List<Completion> cl = findCompletionsByProperty("class='" + classname
				+ "'");
		return cl;
	}
	
	public static HashMap<String, Long> findMethodsCountByClass(String classname) throws Exception {
		String sqlString = "select * from completion where class='" + classname + "'";
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sqlString);
		if (list == null)
			return null;
		return Completion.getMethodsCount(list);
	}

	public static Completion findCompletionByProperty(String props)
			throws Exception {
		List<Completion> list = findCompletionsByProperty(props);
		if (list == null || list.size() == 0) {
			return null;
		}
		Completion user = list.get(0);
		return user;
	}

	public static List<Completion> findCompletionsByProperty(String props)
			throws Exception {
		String sqlString = "select * from completion where " + props;
		MySQL mySQL = new MySQL();
		ArrayList<HashMap<String, Object>> list = mySQL.execute(sqlString);
		if (list == null)
			return null;
		return Completion.getCompletions(list);
	}

	public static void update(Completion c) throws Exception {
		String sqlString = "update completion set " + "class='"
				+ c.getClassname() + "', method='" + c.getMethod() + "', count="
				+ c.getCount() + " where id=" + c.getId();
		new MySQL().execute(sqlString);
	}

	public static void insert(Completion c) throws Exception {
		String sqlString = "insert into completion (class, method, count) "
				+ "values ('" + c.getClassname() + "', '" + c.getMethod() + "', "
				+ c.getCount() + ")";
		new MySQL().execute(sqlString);
	}

}
