package main.project;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class ProgramData {

    private static final String path = "MavPASS_MasterCSV.csv";

    private final static ArrayList<StudentObject.Student> students = student_list();

    public static void main(String[] args) throws IOException {
        for (String line: entries_by_college(1,"CSET")){
            System.out.println(line);
        }
    }

    public ProgramData() {
        super();
    }

    public ArrayList<StudentObject.Student> getStudents() {
        return students;
    }

    public static ArrayList<String[]> csv_data(String file_name) {
        ArrayList<String[]> data = new ArrayList<>();

        try {
            // Going through the text file using Scanner
            File file = new File(file_name);

            Scanner balance_file = new Scanner(file);
            // Skip the First line in the text file
            balance_file.nextLine();

            while (balance_file.hasNextLine()) {
                String line = balance_file.nextLine();
                String[] data_point = line.split(",");
                data.add(data_point);
            }
        } catch (SecurityException | FileNotFoundException error) {
            error.printStackTrace();
        }
        return data;
    }

    public static ArrayList<StudentObject.Student> student_list() {
        ArrayList<StudentObject.Student> return_arr = new ArrayList<>();
        ArrayList<String[]> data = csv_data(path);

        ArrayList<String> unique_students = new ArrayList<>();

        // Creates an ArrayList of UNIQUE student names
        for (String[] s : data) {

            String a_student = s[2] + "_" + s[1];
            if (!unique_students.contains(a_student)) {
                unique_students.add(a_student);
            }
        }

        // Sorts the list of students
        java.util.Collections.sort(unique_students);

        // Creates a new instance of a Student Object and adds it to the final return list
        // Gets names from the unique_students ArrayList
        for (String student : unique_students) {
            String[] name = student.split("_");
            StudentObject.Student student_obj = new StudentObject.Student(name[0], name[1]);
            return_arr.add(student_obj);
        }

        // Goes thorough every student in the list and adds all the sessions they attended to their specific lists
        for (StudentObject.Student s : return_arr) {

            // Goes thorough CSV file data for every student, this could be optimized a bit
            for (String[] dp : data) {

                // Storing the line of data as data types for easy access
                String dp_firstName = dp[2];
                String dp_lastName = dp[1];
                String dp_instructor = dp[3];
                String dp_mpLeader = dp[4];
                String dp_date = dp[5];
                String dp_classCode = dp[6];

                // If the data line contains the student's full name, the session will be created then
                // added to the student's list of sessions
                if (dp_firstName.equals(s.getFirst_name())
                        && dp_lastName.equals(s.getLast_name()
                )) {
                    SessionObject.Session a_session =
                            new SessionObject.Session(
                                    dp_date,
                                    dp_instructor,
                                    dp_mpLeader,
                                    dp_classCode);

                    s.addSession(a_session);
                }
            }
        }

        return return_arr;
    }

    public static Hashtable<String, String> code_by_department(){
        // ADD new class codes under here

        // College of Allied Health and Nursing
        String[] a_h = new String[]{
                "ENG101"
        };

        // College of Business
        String[] cob = new String[]{
                "ACCT200"
        };

        // College of Social and Behavioral Sciences
        String[] sbs = new String[]{
                "ANTH101", "ANTH102", "ECON201",
                "ECON202", "SOC202"
        };

        // College of Science, Engineering and Technology
        String[] cset = new String[]{
                "BIOL105", "BIOL220", "CIS121", "CIS122",
                "MATH112", "MATH121", "MATH122", "PHYS211",
                "PHYS221", "PHYS222", "STAT154"
        };

        // Hashtable / Dictionary for all class codes to their respective departments
        Hashtable<String, String> c2d = new Hashtable<>();

        // Links all classes to their respective colleges
        for (String s: a_h){
            c2d.put(s, "A&H");
        }
        for (String s: cob){
            c2d.put(s, "COB");
        }
        for (String s: sbs){
            c2d.put(s, "SBS");
        }
        for (String s: cset){
            c2d.put(s, "CSET");
        }

        return c2d;
    }

    public static ArrayList<String> entries_by_college(Integer mode, String dept){
        ArrayList<String> final_data = new ArrayList<>();

        // Generates the current TITLE of the department being generated
        String title_line = "";
        switch (dept){

            case "A&H":
                title_line = "RAFFLE ATTENDANCE DATA: College of Allied Health and Nursing\n" +
                        "======================================================================\n";
                break;
            case "COB":
                title_line = "RAFFLE ATTENDANCE DATA: College of Business\n" +
                        "======================================================================\n";
                break;
            case "SBS":
                title_line = "RAFFLE ATTENDANCE DATA: College of Social and Behavioral Sciences\n" +
                        "======================================================================\n";
                break;
            case "CSET":
                title_line = "RAFFLE ATTENDANCE DATA: College of Science, Engineering and Technology\n" +
                        "======================================================================\n";
                break;
        }
        final_data.add(title_line);

        // Goes through every student and gets the attendance data for current department
        for (StudentObject.Student student : students){

            StringBuilder student_line = new StringBuilder();

            // Student Attendance Counts / Entries
            int session_count = student.get_Dept_sessions(dept).size();
            int entries = session_count - 9;

            // If student has NO sessions for this department, skip this Student
            if (session_count <= 0){
                continue;
            }

            // Switches between the modes based on the inputted mode value
            switch (mode){


                // 1 - "So close" list, for students with 5 - 9 sessions close to having 1 entry for a raffle
                case 1:
                    if (!(session_count >= 5 && session_count <= 9)){
                        continue;
                    }
                    else {
                        entries = 0;
                    }
                    break;

                // 3 - DEPARTMENT SPECIFIC, UNFILTERED Department data
                case 3:
                    // Skip student if less than 10 sessions
                    if (session_count < 10){
                        continue;
                    }
                    break;

                // BOTH FILTERED AND UNFILTERED
                // 0 - COMPLETE, UNFILTERED LIST of ALL Students
                // 2 - DEPARTMENT SPECIFIC, FILTERED Department data
                case 0:
                case 2:
                    if (session_count < 10){
                        entries = 0;
                    }
                    break;

            }


            // EMAIL VALIDITY MARKER
            String invalid_email_marker = "";
            if (!student.email_valid()){
                invalid_email_marker = " <===== ########## INVALID EMAIL ##########";
            }

            // STUDENT LINE START

            // "Student Name" <student.name@mnsu.edu> {EMAIL VALIDITY MARKER}
            // {DEPARTMENT} RAFFLE ENTRIES: {ENTRIES}
            // TOTAL {DEPARTMENT} Department Sessions Attended: {TOTAL ATTENDANCE}
            // ATTENDANCE BY CODE:
            // {CODE1}: {CODE1_count}
            // {CODE2}: {CODE2_count}
            // {CODE3}: {CODE3_count}
            // ...
            student_line.append(String.format("\"%s\" <%s> %s\n" +
                            "%s RAFFLE ENTRIES: %s\n" +
                    "TOTAL %s Department Sessions Attended: %s\n" +
                            "ATTENDANCE BY CODE:\n",
                    student.getName(),
                    student.getEmail(),
                    invalid_email_marker,
                    dept,
                    entries,
                    dept,
                    session_count)
            );

            // Getting Student's CLASS CODEs and ATTENDANCE COUNT PER CLASS CODE
            ArrayList<String> stu_codes = new ArrayList<>();
            ArrayList<Integer> stu_codes_counts = new ArrayList<>();
            for (SessionObject.Session s: student.get_Dept_sessions(dept)){
                if(!stu_codes.contains(s.getClass_code())){
                    stu_codes.add(s.getClass_code());
                    stu_codes_counts.add(1);
                }
                else{
                    int code_idx = stu_codes.indexOf(s.getClass_code());
                    int code_count = stu_codes_counts.get(code_idx);
                    stu_codes_counts.set(code_idx, code_count+1);
                }
            }

            // Adds to the student line the CLASS CODE and COUNTS
            for(int i = 0; i < stu_codes.size(); i++){
                student_line.append(String.format(" - %s: %s\n", stu_codes.get(i), stu_codes_counts.get(i)));
            }
            final_data.add(student_line.toString());
        }
        return final_data;
    }
}
