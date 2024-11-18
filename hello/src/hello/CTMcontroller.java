package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CTMcontroller {
	private Connection connection;

	private void connectToDatabase() {
		// 数据库URL，用户名和密码

		String url = "jdbc:mysql://localhost:3306/my_db?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8&autoReconnect=true&useSSL=false&allowMultiQueries=true&useAffectedRows=true";
		String user = "root";
		String password = "asd123";
		try {
			// 加载数据库驱动
			Class.forName("com.mysql.cj.jdbc.Driver");
			// 建立数据库连接
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the database successfully.");
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error connecting to the database.");
			e.printStackTrace();
		}
	}

	public String dispatchRequest(Object msg) {

		String message = (String) msg;
		String rMsg = null;
		/*
		 * switch (message) { case "1": break; }
		 */
		if (message.startsWith("GET_USER_BY_ID:")) {
			rMsg = getUser(message);
		}
		return rMsg;

	}

	public String getUser(String message) {

		Long userId = Long.parseLong(message.split(":")[1]);
		String sql = "select * from accountinformation where User_ID = ?";
		String jsonString;
		try {
			// 创建Statement对象
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, String.valueOf(userId));
			// 执行查询
			ResultSet resultSet = statement.executeQuery();
			// 处理结果集
			while (resultSet.next()) {
				UserAccount account = new UserAccount();
				int User_ID = resultSet.getInt("User_ID");
				String User_Name = resultSet.getString("User_Name");
				String User_Password = resultSet.getString("User_Password");
				String Email = resultSet.getString("Email");
				int Level = resultSet.getInt("Level");
				account.setUser_ID(User_ID);
				account.setUser_Name(User_Name);
				account.setUser_Password(User_Password);
				account.setEmail(Email);
				account.setLevel(Level);
				// 将对象转换为JSON字符串
				ObjectMapper mapper = new ObjectMapper();
				jsonString = mapper.writeValueAsString(account);
				// 如果 JSON 字符串中包含多余的 `\`，使用 sanitizeJsonString 方法去除
				String cleanJsonString = sanitizeJsonString(jsonString);
				System.out.println(cleanJsonString);
				// 打印查询结果（可选）
				System.out.println(jsonString);
					

				// 将结果发送回客户端
			}
			// 关闭结果集和Statement对象
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.err.println("Error executing query.");
			e.printStackTrace();
		}
		
		return jsonString;

	}

	public String getRecieveinfo(String message) {
		String rMsg = null;
		return rMsg;
	}

	public String sanitizeJsonString(String jsonString) {
		// 通过去掉反斜杠，得到正常的 JSON 格式 
		return jsonString.replace("\\", "");
	}
}
