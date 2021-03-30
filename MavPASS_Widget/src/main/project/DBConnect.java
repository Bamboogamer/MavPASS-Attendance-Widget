package main.project;
import java.sql.*;

// Test of concept using the MySQL Database

public class DBConnect {

    public static void main(String[] args) {
        String host = "jdbc:mysql://localhost:3306/mavpass_project_db";
        String user = "root";
        String pass = "!Wj-@-KPQGWmweRRE63N";

        try {
            Connection connection = DriverManager.getConnection(host, user, pass);
            System.out.println("SUCCESSFULLY CONNECTED TO DATABASE!\n");

            Statement statement = connection.createStatement();
            String sql = "select * from attendance";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()){

                int id_col = rs.getInt("Data_ID");
                String last_name = rs.getString("Student_LastName");
                String first_name = rs.getString("Student_FirstName");
                String instructor = rs.getString("Instructor");
                String mp_leader = rs.getString("MavPASS_Leader");
                String date_str = rs.getString("Session_Date");
                String class_code = rs.getString("Class_Code");


                if (last_name.equals("Le") && first_name.equals("Danny")){
                    System.out.printf(" %s %s %s %s %s %s %s%n",
                            id_col,
                            last_name,
                            first_name,
                            instructor,
                            mp_leader,
                            date_str,
                            class_code);
                }
            }

        } catch (SQLException e) {

            System.out.println("FAILED TO CONNECT TO SERVER");
            e.printStackTrace();
        }

    }
}
