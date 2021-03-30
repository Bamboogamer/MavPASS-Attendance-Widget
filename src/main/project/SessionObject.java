package main.project;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class SessionObject {

    public static void main(String[] args) {}

    /**
     * Superclass of Session Object class.
     *
     * Constructor Attribute(s):
     *  + date_str ==>  String, Specific date of Session
     *  + class_code ==> String, Specific Class code of Session
     *  + mp_leader ==> MP_Leader Object, MavPASS leader that lead the Session
     *
     *  HIDDEN Attribute(s):
     *  + students ==> ArrayList, Attendance of specific Session
     */
    public static class Session {

        // Class level variables //
        private final String date_str;
        private final String class_code;
        private final String instructor;

        private final String mp_leader;
        private final ArrayList<StudentObject.Student> students = new ArrayList<>();

        /**
         * Constructor for Session Object.
         * @param date_str - String, Session's Date
         * @param mp_leader - MP_Leader Object, Session's MavPASS Leader
         * @param class_code - String, Session's Class Code
         * @param instructor - String, Student's Instructor for the Class
         */
        public Session(String date_str, String instructor, String mp_leader, String class_code){
            this.instructor = instructor;
            this.date_str = date_str;
            this.class_code = class_code;
            this.mp_leader = mp_leader;
        }

        //**********// Session Object's Getter Methods //**********//

        /**
         * Getter for Session's Date as a String
         * @return String, Session's Date
         */
        public String getDate_str() {
            return date_str;
        }

        /**
         * Getter for Session's Instructor as a String
         * @return String, Student's Instructor for the Class Code
         */
        public String getInstructor() {
            return instructor;
        }

        /**
         * Getter for Session's Date as DateTime Object
         * @return DateTime Object, Session's Date
         */
        public DateTime getDate(){

            DateTimeFormatter date_format = DateTimeFormat.forPattern("dd-MMM-yyyy");
            return date_format.parseDateTime(getDate_str());

        }

        /**
         * Getter for Session's MavPASS Leader
         * @return MP_Leader Object, Session's MavPASS Leader
         */
        public String getMP_leader() {
            return mp_leader;
        }

        /**
         * Getter for Session's Class Code
         * @return String, Session's Class Code
         */
        public String getClass_code() {
            return class_code;
        }

        /**
         * Returns the Session Object's list of Students that attended the session as an ArrayList of Student Objects
         * @return ArrayList<>, List of Student Objects
         */
        public ArrayList<StudentObject.Student> get_list() {
            return students;
        }

        /**
         * Returns the Session's UNIQUE ID as "[date]-[instructor]-[class_code]-[MP_leaderLastName]"
         * @return String, "[date]-[instructor]-[class_code]-[MP_leaderLastName]"
         */
        public String get_session_ID(){

            String[] instructor_split = getInstructor().split(" ");
            String[] mpLead_split = getMP_leader().split(" ");

            return String.format("%s_%s_%s_%s",
                    getDate_str(),
                    instructor_split[instructor_split.length-1],
                    getClass_code(),
                    mpLead_split[mpLead_split.length-1]);
        }

        ///**********/// Session Object's Void/Setter/Misc Methods ///**********///

        /**
         * Returns an Integer of days since (FROM TODAY) since the Session's date
         * @return Integer, Days since the session's date from TODAY
         */
        public Integer days_since(){

            DateTime dt_today = DateTime.now();

            return Days.daysBetween(getDate(), dt_today).getDays();

        }

        /**
         * Adds a Student Object to Session's students ArrayList
         * @param student Student Object, a Student as an Object
         */
        public void addStudent(StudentObject.Student student){

            // TODO - This may need a unique ID (Maybe use EMAIL?), instead of the Student Object itself
            if(!students.contains(student)){
                students.add(student);
            }
        }

        @Override
        public String toString() {
            return "Session{" +
                    "date_str='" + date_str + '\'' +
                    ", mp_leader='" + mp_leader + '\'' +
                    ", class_code='" + class_code + '\'' +
                    ", students=" + students +
                    '}';
        }
    }
}
