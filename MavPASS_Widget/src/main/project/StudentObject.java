package main.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class StudentObject {

    public static void main(String[] args) { }
    /**
     * Superclass for a Student.
     *
     * Constructor Attribute(s):
     *  + first_name ==> String, Student's first name
     *  + last_name ==> String, Student's last name
     *  + professor ==> String, Student's Professor's name
     *
     * HIDDEN Attribute(s):
     *  + contacted_fc   ==> Boolean, FIRST CONTACT email has been sent
     *  + contacted_mia  ==> Boolean, Contacted after student has been MIA for more than 14 days
     *  + status         ==> Boolean, TRUE if student has been attending, FALSE if student has been missing from sessions 14+ days
     *  + sessions       ==> ArrayList, list of Sessions (Objects) that the Student attended
     */
    public static class Student {

        // Class level variables //
        private final String first_name;
        private final String last_name;
        private final ArrayList<SessionObject.Session> sessions = new ArrayList<>();
        private Boolean status = false;

        /**
         * Constructs a Student Object.
         *
         * @param first_name - Student's First Name
         * @param last_name  - Student's Last Name
         */
        public Student(String first_name, String last_name) {
            this.first_name = first_name;
            this.last_name = last_name;
        }

        ///**********/// Student Object's Getter Methods ///**********///

        /**
         * Getter for Student's First Name
         *
         * @return Student's First Name
         */
        public String getFirst_name() {
            return first_name;
        }

        /**
         * Getter for Student's Last Name
         *
         * @return String, Student's Last Name
         */
        public String getLast_name() {
            return last_name;
        }

        /**
         * Getter for Student's full name
         *
         * @return String, First name + Last name
         */
        public String getName() {
            return String.format("%s %s", first_name, last_name);
        }

        /**
         * Getter for Student's student email for MNSU
         *
         * @return String, "first_name" + "last_name" + "@mnsu.edu"
         */
        public String getEmail() {
            String first_new = first_name.replaceAll("[' ]", "");
            String last_new = last_name.replaceAll("[' ]", "");

            return String.format("%s.%s@mnsu.edu", first_new.toLowerCase(), last_new.toLowerCase());
        }

        /**
         * Getter for Student's complete, SORTED list of Sessions (Objects) attended
         *
         * @return ArrayList, SORTED (from first to last session) List of all Session Objects
         */
        public ArrayList<SessionObject.Session> getSessions() {

            ArrayList<SessionObject.Session> sorted_sessions = new ArrayList<>();

            // Sorts the days_since() from every session in session ArrayList
            ArrayList<Integer> ds_list = new ArrayList<>();
            for (SessionObject.Session s : sessions) {
                ds_list.add(s.days_since());
            }
            Collections.sort(ds_list);
            Collections.reverse(ds_list);

            ArrayList<String> unique_sessions = new ArrayList<>();

            // Creates a new ArrayList that is sorted by days_since()
            for (int i : ds_list){
                for(SessionObject.Session s: sessions){
                    if (s.days_since().equals(i) && !unique_sessions.contains(s.get_session_ID())){
                        sorted_sessions.add(s);
                        unique_sessions.add(s.get_session_ID());
                    }
                }
            }

            return sorted_sessions;
        }

        /**
         * Getter for Student's DEPARTMENT SPECIFIC, SORTED list of Sessions (Objects) attended
         *
         * @return ArrayList, SORTED (from first to last session) List of DEPT SPECIFIC Session Objects
         */
        public ArrayList<SessionObject.Session> get_Dept_sessions(String dept){

            ArrayList<SessionObject.Session> end_list = new ArrayList<>();

            Hashtable<String, String> code_department = ProgramData.code_by_department();
            for (SessionObject.Session s : getSessions()){
                String session_code = s.getClass_code();
                if (code_department.get(session_code).equals(dept)){
                    end_list.add(s);
                }
            }
            return end_list;
        }

        /**
         * Gives the Student's Latest Session as a Session Object
         *
         * @return Session Object, Latest Session student has attended
         */
        public SessionObject.Session getLatestSession() {
            int last_idx = getSessions().size() - 1;
            return getSessions().get(last_idx);
        }

        /**
         * Student's attendance status. If the student has attended at least one session, but the student has been
         * missing from sessions for more than 14 days (2 weeks).
         *
         * @return Boolean, TRUE if Student has been attending recently, FALSE if Student has been MIA for more than 14 days
         */
        public Boolean getStatus() {

            int days_since = getLatestSession().days_since();

            // If the Students have yet to attend a session, return False
            // No sessions OR days since latest session is less than 14 days
            // ** DO NOT SHOW ON GENERATED LIST **
            if (sessions.isEmpty() || (days_since >= 0 && days_since < 14)) {
                status = true;
            }

            // If student has attended at least 1 MavPASS session...
            else {

                // If days since the latest session is between 14 and 20 days (Week 2)
                // OR if days since the latest session is between 28 and 34 (Week 4)
                // ** SHOW ON GENERATED LIST **
                if (days_since >= 14 && days_since < 21
                        || days_since >= 28 && days_since < 35) {
                    status = false;
                }

                // If days since the latest session is between 21 and 28 days (Week 3)
                // OR if days since the latest session is more than 35 days (Week 5+)
                // ** DO NOT SHOW ON GENERATED LIST **
                else if (days_since >= 21 && days_since <= 28
                        || days_since >= 35){
                    status = true;
                }

            }
            return status;
        }

        ///**********/// Student Object's Void/Setter/Misc Methods ///**********///

        /**
         * Adds a Session Object to Session's sessions ArrayList
         * @param session Sessions Object, a Session as an Object
         */
        public void addSession(SessionObject.Session session){
            ArrayList<String> unique_ids = new ArrayList<>();
            for(SessionObject.Session s : getSessions()){
                if(!unique_ids.contains(s.get_session_ID())){
                    unique_ids.add(s.get_session_ID());
                }
            }

            if(!unique_ids.contains(session.get_session_ID())){
                this.sessions.add(session);
            }

        }

        /**
         * Checks the computer generated email. If email can be found in the master list of emails, return True
         * If generated email is invalid, return false
         * @return Boolean, Status of computer generated email. True if valid, False if not
         */
        public Boolean email_valid(){

            ArrayList<String> emails = new ArrayList<>();
            try {
                String dir_path = System.getProperty("user.dir");
                File file = new File(dir_path+"\\valid_emails.csv");

                Scanner balance_file = new Scanner(file);
                balance_file.nextLine();

                while (balance_file.hasNextLine()) {
                    String email = balance_file.nextLine();
                    emails.add(email);
                }
            }
            catch (SecurityException | FileNotFoundException error){
                error.printStackTrace();
            }
            return emails.contains(getEmail());
        }


        @Override
        public String toString() {
            return "Student{" +
                    "first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", sessions=" + sessions +
                    ", status=" + status +
                    '}';
        }
    }
}
