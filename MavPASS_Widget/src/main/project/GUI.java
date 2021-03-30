package main.project;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class GUI implements ActionListener{

    public static void main(String[] args) {
        try{
            new GUI();
        }catch (Exception e){
            System.out.println("NO CSV");
        }
    }

    // All the frames for the GUI and the Program's main data (as a ProgramData object)
    private final JFrame frame_main;
    private JFrame frame_generated;
    private JFrame frame_no_csv;
    private JFrame progress_frame;
    private ProgramData programData;


    /**
     * Creates the main GUI components and main Frame of the executable file. Essentially makes the main screen of the
     * program and is what the USER sees when executing the program.
     */
    public GUI(){

        // Sets the "look and feel" of the windows to look like the default OS window interface
        // This is to make the program look a little more "familiar" to the USER
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException
                | ClassNotFoundException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }

        // Main JFrame
        frame_main = new JFrame("MavPASS Attendance Program");
        frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_main.setPreferredSize(new Dimension(720,360));
        frame_main.setMinimumSize(new Dimension(720,360));

        // Buttons
        JButton btn_UPLOAD = new JButton("Select Excel Files and Create CSV");
        btn_UPLOAD.addActionListener(this);
        btn_UPLOAD.setActionCommand("CREATE CSV");

        JButton btn_GEN_EMAILS = new JButton("Generate Files");
        btn_GEN_EMAILS.addActionListener(this);
        btn_GEN_EMAILS.setActionCommand("GENERATE FILES");

        JButton btn_EXIT = new JButton("EXIT");
        btn_EXIT.addActionListener(this);
        btn_EXIT.setActionCommand("EXIT_1");

        // Labels
        JLabel label_welcome = new JLabel("Welcome to the MavPASS Attendance Program!");
        label_welcome.setHorizontalAlignment(SwingConstants.CENTER);
        label_welcome.setFont(new Font("Consolas", Font.BOLD, 26));

        JLabel label_welcome_2 = new JLabel("by Danny Le");
        label_welcome_2.setFont(new Font("Consolas", Font.BOLD | Font.ITALIC, 18));
        label_welcome_2.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel / Component Container
        JPanel panel_welcome = new JPanel();
        panel_welcome.setLayout(new GridLayout(5, 1));
        panel_welcome.setSize(new Dimension(700, 340));

        panel_welcome.add(label_welcome);
        panel_welcome.add(label_welcome_2);
        panel_welcome.add(btn_UPLOAD);
        panel_welcome.add(btn_GEN_EMAILS);
        panel_welcome.add(btn_EXIT);

        frame_main.add(panel_welcome);
        frame_main.setLocationRelativeTo(null);
        frame_main.setVisible(true);

    }

    /**
     * Generates the a text file in the '/Generated Lists Here/RAFFLE-ATTENDANCE DATA FILES' Directory.
     * Based on the 'mode' parameter, program will generate specific types of files
     *
     * Integer [mode]
     * 0 - UNFILTERED: COMPLETE LIST, containing all departmental student attendance data,
     * 1 - FILTERED: 'SO CLOSE' LIST, containing only students who have attended 5 - 9 sessions but are close to
     * earning one entry into the raffle
     * 2 - FILTERED: DEPARTMENT SPECIFIC, Containing only college specific student information with 10+ sessions
     * 3 - UNFILTERED: DEPARTMENT SPECIFIC, Containing all college specific student information
     *
     *
     * String [dept]
     * Leave empty if you are not sorting by department
     *
     * CHANGE TO: "A&H", "COB", "SBS", or "CSET" for the specific College Department
     *
     */
    private void generate_list_1(Integer mode, String dept){
        PrintWriter new_list;
        String file_name = "";
        String end_msg = "ERROR";
        try {
            IN_PROGRESS();
            String college = "";
            switch(dept){
                case "A&H":
                    college = "College of Allied Health and Nursing";
                    break;
                case "COB":
                    college = "College of Business";
                    break;
                case "SBS":
                    college = "College of Social and Behavioral Sciences";
                    break;
                case "CSET":
                    college = "College of Science, Engineering and Technology";
                    break;
            }

            // Creates the dictionary (if it doesn't exist)
            // Then creates a text file that is to be written into called: ATTENDANCE BY CLASS CODE TOTAL.txt
            switch (mode){

                case 0:
                    file_name = "\\1. COMPLETE RAFFLE-ATTENDANCE INFORMATION.txt";
                    end_msg = file_name.substring(1) +" ---- COMPLETE!";
                    break;
                case 1:
                    file_name = "\\2. CLOSE TO HAVING ONE ENTRY --- STUDENTS FILE.txt";
                    end_msg = file_name.substring(1) +" ---- COMPLETE!";
                    break;
                case 2:
                    file_name = String.format("\\3a. (COMPLETE) (%s) %s --- RAFFLE INFORMATION.txt", dept, college);
                    end_msg = file_name.substring(1) +" ---- COMPLETE!";
                    break;

                case 3:
                    file_name = String.format("\\3b. (FILTERED) (%s) %s --- RAFFLE INFORMATION.txt", dept, college);
                    end_msg = file_name.substring(1) +" ---- COMPLETE!";
                    break;
            }

            new_list = new PrintWriter(create_directory(true) + file_name);

            // Timestamps the generated text file with the date and time (on a 24 hour clock)
            DateTime today = DateTime.now();
            String first_line = String.format("Generated on: %s%n%n", today.toDate());
            new_list.write(first_line);

            if(mode == 0 || mode == 1){
                for (String dept_ : new String[]{"A&H", "COB", "SBS", "CSET"}){
                    for(String data_line : ProgramData.entries_by_college(mode, dept_)){
                        new_list.write(data_line+"\n");
                    }
                }
            }
            else{
                for(String data_line : ProgramData.entries_by_college(mode, dept)){
                    new_list.write(data_line+"\n");
                }
            }



//            String second_line = "Student Attendance Data by Class Code w/ Number of Entries\n" +
//                    "########################################################################################################################";
//            new_list.write(second_line);
//
//            // Variables to check if there are available students to be written into the text file
//            boolean no_students = true;
//            String na_line = "\n< Not Available >";
//
//            // Valid Emails
//            // Prints out every student who has 10 or more sessions in any specific class session (class code)
//            // AND includes their number of entries into Department specific raffle(s)
//            for (StudentObject.Student student : programData.getStudents()){
//
//                ArrayList<Hashtable> student_data = ProgramData.attendance_by_code();
//                int last_idx = student_data.size() - 1;
//
//                    if(student_data.size() > 0){
//
////                        // Skip student if they have NO ENTRIES
////                        if(filtered && student_data.get(last_idx).contains("NO ENTRIES")){
////                            continue;
////                        }
//
//                        no_students = false;
//                        new_list.write("\n" +student_data.get(last_idx));
//                    }
//
//            }
//
//            // No students could be found to generate this category
//            if(no_students){
//                new_list.write(na_line+"\n");
//            }

            // Done with file, garbage collection
            new_list.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        progress_frame.dispose();
//        System.out.println(end_msg);
    }

    /**
     * Generates the a text file in the 'Generated Lists Here' Directory.
     * Text file should be called 'NEW ATTENDANCE MILESTONES.txt' and contains:
     * NEW Student milestones (5 / 10 / 20+) that have been achieved in the past week (7 days)
     */
    private void generate_list_2(Boolean complete_list){

        PrintWriter new_list;
        String file_name;
        String end_msg = "ERROR";
        try {

            IN_PROGRESS();

            if (complete_list){
                file_name = "\\LIST OF MILESTONES (COMPLETE).txt";
                end_msg = "LIST OF MILESTONES (COMPLETE) --------- COMPLETE";
            }
            else{
                file_name = "\\LIST OF MILESTONES (WEEKLY).txt";
                end_msg = "LIST OF MILESTONES (WEEKLY) ----------- COMPLETE";
            }

            // Creates the dictionary (if it doesn't exist)
            // Then creates a text file that is to be written into called: NEW ATTENDANCE MILESTONES.txt
            new_list = new PrintWriter(create_directory(false)+file_name);

            // Timestamps the generated text file with the date and time (on a 24 hour clock)
            DateTime today = DateTime.now();
            String first_line = String.format("Generated on: %s%n%n", today.toDate());

            // Title
            String second_line = "Student Attendance Milestones\n" +
                    "########################################################################################################################";
            new_list.write(first_line);
            new_list.write(second_line);

            // Variables to check if there are available students to be written into the text file
            boolean no_students = true;
            String na_line = "< Not Available >";

            // Five (5) sessions milestone that was achieved in the past seven (7) days
            new_list.write("\n\nStudents that achieved the five (5) sessions milestone\n" +
                    "-------------------------------------------------------------\n");

            for(StudentObject.Student student : programData.getStudents())
            {
                int st_atn_total = student.getSessions().size();
                SessionObject.Session milestone_session;

                if ((st_atn_total >= 5))
                {
                    milestone_session = student.getSessions().get(4);

                    no_students = milestones_lists(complete_list, new_list, no_students, student, milestone_session);
                }
            }

            // No students could be found to generate this category
            if(no_students){
                new_list.write(na_line+"\n");
            }
            // Resets the boolean for next condition
            no_students = true;

            // Ten (10) sessions milestone that was achieved in the past seven (7) days
            new_list.write("\nStudents that achieved the ten (10) sessions milestone\n" +
                    "-------------------------------------------------------------\n");

            for(StudentObject.Student student : programData.getStudents())
            {
                int st_atn_total = student.getSessions().size();
                SessionObject.Session milestone_session;

                if ((st_atn_total >= 10))
                {
                    milestone_session = student.getSessions().get(9);

                    no_students = milestones_lists(complete_list, new_list, no_students, student, milestone_session);
                }
            }

            // No students could be found to generate this category
            if(no_students){
                new_list.write(na_line+"\n");
            }
            no_students = true;

            // Twenty (20) sessions milestone that was achieved in the past seven (7) days
            new_list.write("\nStudents that achieved the twenty (20) sessions milestone\n" +
                    "-------------------------------------------------------------\n");

            for(StudentObject.Student student : programData.getStudents())
            {
                int st_atn_total = student.getSessions().size();
                SessionObject.Session milestone_session;

                if ((st_atn_total >= 20))
                {
                    milestone_session = student.getSessions().get(19);
                    no_students = milestones_lists(complete_list, new_list, no_students, student, milestone_session);
                }
            }

            // No students could be found to generate this category
            if(no_students){
                new_list.write(na_line+"\n");
            }

            // Done with the file, garbage collection
            new_list.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        progress_frame.dispose();
//        System.out.println(end_msg);
    }

    private boolean milestones_lists(Boolean complete_list, PrintWriter new_list, boolean no_students,
                                     StudentObject.Student student, SessionObject.Session milestone_session) {
        if(complete_list){
            no_students = false;
            String data_line;

            if (student.email_valid()){
                data_line = String.format("<%s> %s%n    Milestone Achievement Date: %s (%s days ago)%n",
                        student.getName(),
                        student.getEmail(),
                        milestone_session.getDate_str(),
                        milestone_session.days_since());
            }
            else{
                data_line = String.format("<%s> %s <--- ************************************* INVALID EMAIL **********"+
                                "   %n    Milestone Achievement Date: %s (%s days ago)%n",
                        student.getName(),
                        student.getEmail(),
                        milestone_session.getDate_str(),
                        milestone_session.days_since());
            }


            new_list.write(data_line);
        }
        else{
            if (milestone_session.days_since() <= 7){
                no_students = false;
                String data_line;
                if (student.email_valid()){
                    data_line = String.format("<%s> %s%n    Milestone Achievement Date: %s (%s days ago)%n",
                            student.getName(),
                            student.getEmail(),
                            milestone_session.getDate_str(),
                            milestone_session.days_since());
                }
                else{
                    data_line =
                            String.format("<%s> %s <--- ************************************* INVALID EMAIL **********"+
                                    "  %n    Milestone Achievement Date: %s (%s days ago)%n",
                            student.getName(),
                            student.getEmail(),
                            milestone_session.getDate_str(),
                            milestone_session.days_since());
                }

                new_list.write(data_line);
            }
        }
        return no_students;
    }

    /**
     * Generates the a text file in the 'Generated Lists Here' Directory.
     * Text file should be called 'EMAIL LIST (THANK YOUs).txt' and contains all the emails of students that need to be
     * contacted with a THANK YOU email.
     */
    private void generate_list_3(){

        PrintWriter new_list;
        try {

            IN_PROGRESS();

            // Creates the dictionary (if it doesn't exist)
            // Then creates a text file that is to be written into called: EMAIL LIST (THANK YOUs).txt
            new_list = new PrintWriter(create_directory(false)+"\\EMAIL LIST (THANK YOUs).txt");

            // Timestamps the generated text file with the date and time (on a 24 hour clock)
            DateTime today = DateTime.now();
            String first_line = String.format("Generated on: %s%n%n", today.toDate());
            new_list.write(first_line);

            // Variables to check if there are available students to be written into the text file
            boolean no_students = true;
            String na_line = "\n< Not Available >";

            // Valid Emails
            // Generates a list of students who needs a "THANK YOU" email
            String second_line = "Complete list of students that need to be sent a \"Thank You\" E-Mail\n" +
                    "########################################################################################################################";

            new_list.write(second_line);

            // Creates a list of students for specific MavPASS Leaders to contact for the "Thank You" Email
            for(String mp_leader: mavpass_leaders()){
                ArrayList<String> student_lines = new ArrayList<>();
                for(StudentObject.Student student:programData.getStudents()){
                    String mpl = student.getLatestSession().getMP_leader()  + " - " + student.getLatestSession().getClass_code();
                    if(student.getSessions().size() == 1
                            && student.getLatestSession().days_since() <= 7
                            && mpl.equals(mp_leader))
                    {
                        no_students = false;
                        String email;
                        if (student.email_valid()){
                            email = String.format("%n<%s %s> %s", student.getFirst_name(),
                                    student.getLast_name(),
                                    student.getEmail());
                        }
                        else {
                            email = String.format("%n<%s %s> %s  <---- ***** INVALID EMAIL *****", student.getFirst_name(),
                                    student.getLast_name(),
                                    student.getEmail());
                        }


                        student_lines.add(email);
                    }
                }

                // Prints into file ONLY if MavPASS Leader has at least 1 student that needs a "Thank You" email
                mp_leader_line(new_list, mp_leader, student_lines);
            }

            // No students could be found to generate this category
            if(no_students){
                new_list.write(na_line+"\n");
            }

            // Done with file, garbage collection
            new_list.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        progress_frame.dispose();
//        System.out.println("THANK YOU EMAILS ---------------------- COMPLETE");
    }

    /**
     * Generates the a text file in the 'Generated Lists Here' Directory.
     * Text file should be called 'EMAIL LIST (CHECK-UPs).txt' and contains all the emails of students that need to be
     * contacted with a Check-Up emails.
     */
    private void generate_list_4(){

        PrintWriter new_list;
        try {

            IN_PROGRESS();

           // Console message variables to show progress of this method running
//            System.out.println("Generating the EMAIL LIST (CHECK-UPs).txt file...");

            // Creates the dictionary (if it doesn't exist)
            // Then creates a text file that is to be written into called: EMAIL LIST (CHECK-UPs).txt
            new_list = new PrintWriter(create_directory(false)+"\\EMAIL LIST (CHECK-UPs).txt");

            // Timestamps the generated text file with the date and time (on a 24 hour clock)
            DateTime today = DateTime.now();
            String first_line = String.format("Generated on: %s%n%n", today.toDate());
            new_list.write(first_line);

            // Variables to check if there are available students to be written into the text file
            boolean no_students = true;
            String na_line = "\n< Not Available >";

            String second_line = "Complete list of students that need to be sent a \"Check-Up\" E-Mail\n" +
                    "########################################################################################################################";

            new_list.write(second_line);

            // Valid Emails
            // Generates a list of students that may need to a check up email that have been missing from
            // sessions for more than two weeks, or 14 days
//            System.out.print("PROGRESS:[");
            for (String mp_leader : mavpass_leaders()){

                // PROGRESS BAR INCREMENT
//                System.out.print("#");

                ArrayList<String> student_lines = new ArrayList<>();
                for(StudentObject.Student student:programData.getStudents()){
                    String mpl = student.getLatestSession().getMP_leader()  + " - " + student.getLatestSession().getClass_code();

                    if(!student.getStatus()
                            && mp_leader.equals(mpl))
                    {
                        no_students = false;
                        String email;

                        if (student.email_valid()){
                            email = String.format("%n<%s> %s%n  - LAST ATTENDANCE: %s, DAYS SINCE: %s", student.getName(),
                                    student.getEmail(),
                                    student.getLatestSession().getDate_str(),
                                    student.getLatestSession().days_since());
                        }
                        else {
                            email = String.format("%n<%s> %s <-- ***** INVALID EMAIL *****%n  - LAST ATTENDANCE: %s, DAYS SINCE: %s", student.getName(),
                                    student.getEmail(),
                                    student.getLatestSession().getDate_str(),
                                    student.getLatestSession().days_since());
                        }

                        student_lines.add(email);
                    }
                }
                mp_leader_line(new_list, mp_leader, student_lines);
            }

            // No students could be found to generate this category
            if(no_students){
                new_list.write(na_line+"\n");
            }

            // Done with file, garbage collection
            new_list.close();


        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        progress_frame.dispose();
//        System.out.println("] <- DONE!");
//        System.out.println("CHECK UP EMAILS FILE ------------------ COMPLETE");
    }

    /**
     * Repeated code that was found when generating Thank-you And Check-up Emails.
     */
    private void mp_leader_line(PrintWriter new_list, String mp_leader, ArrayList<String> student_lines) {
        if (student_lines.size() != 0){
            String line = String.format("%n%nMavPASS Leader: %s%n", mp_leader);
            new_list.write(line);

            for(int i = 0; i < line.length()-3;i++){

                new_list.write("=");
            }

            for(String l: student_lines){
                new_list.write(l);
            }
        }
    }

    /**
     * Returns a list of all the possible MavPASS leaders from the given CSV data file.
     * @return ArrayList, an ArrayList of all the possible MavPASS leaders in the CSV
     */
    private ArrayList<String> mavpass_leaders(){

        // Creates a unique ArrayList of MavPASS Leader names
        ArrayList<String> mp_leaders = new ArrayList<>();
        for(StudentObject.Student student: programData.getStudents()){
            String mp_lead = student.getLatestSession().getMP_leader() + " - " + student.getLatestSession().getClass_code();
            if(!mp_leaders.contains(mp_lead)){
                mp_leaders.add(mp_lead);
            }
        }

        return mp_leaders;
    }

    /**
     * Repeated code that was found in the generate_list_X() methods.
     * Creates a dictionary "Generated Files HERE" if it doesn't exist.
     * Then gives the file path of said file to be used to create other files inside of the directory.
     * @return String, file path of the created (or existing) "Generated Files HERE" directory
     */
    private String create_directory(Boolean raffle){
        File generated_files;
        if (!raffle){
            generated_files = new File("Generated Files HERE");
        }
        else{
            generated_files = new File("Generated Files HERE\\RAFFLE DATA FILES");
        }


        // ONLY generates the directory if it does not exit
        if(!generated_files.exists()){
            boolean dir_made = generated_files.mkdirs();
            if (dir_made){
                // System message
                System.out.println("Directory made!");
            }
        }

        // returns the file Path of Generated Files HERE directory
        return generated_files.getPath();
    }

    /**
     * UI for a "In-Progress" window to let the USER know the program did not 'freeze" and is working in the background
     * I tried to implement a "progressbar" but I just can't figure it out yet.
     */
    private void IN_PROGRESS(){

        progress_frame = new JFrame("IN PROGRESS... PLEASE WAIT");
        progress_frame.setSize(400,75);
        progress_frame.setMinimumSize(new Dimension(400, 75));
        progress_frame.setPreferredSize(new Dimension(400, 75));
        progress_frame.setMaximumSize(new Dimension(400, 75));
        progress_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        progress_frame.add(new JLabel("PLEASE WAIT"));

        progress_frame.setLocationRelativeTo(null);
        progress_frame.setVisible(true);

    }

    /**
     * Repeated code when creating a pop-up windows for the USER.
     * @param pop_up_panel - JPanel, pop-up JPanel, container for the components
     * @param pop_up_label - JLabel, pop-up Label, Message to the USER
     * @param pop_up_btn_close - JButton, pop-up JButton, Button to close the pop-up
     * @param pop_up_frame - JFrame, pop-up main JFrame
     */
    private void GENERATED_UI(JPanel pop_up_panel, JLabel pop_up_label, JButton pop_up_btn_close, JFrame pop_up_frame) {
        pop_up_btn_close.addActionListener(this);

        pop_up_panel.setLayout(new GridLayout(3, 1));
        pop_up_panel.add(pop_up_label);
        pop_up_panel.add(pop_up_btn_close);

        pop_up_frame.setMinimumSize(new Dimension(720, 180));
        pop_up_frame.add(pop_up_panel);

        pop_up_frame.setLocationRelativeTo(null);
        pop_up_frame.setVisible(true);
        pop_up_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Generates a UI pop-up that confirms that the MavPASS_MasterCSV.csv has been generated
     */
    private void GENERATED(){
        frame_generated = new JFrame("CSV GENERATED!");
        JPanel panel_generated = new JPanel();
        JLabel label_generated = new JLabel("CSV has database has been generated!", SwingConstants.CENTER);

        JButton btn_close_msg = new JButton("DONE");
        btn_close_msg.setActionCommand("DONE");
        GENERATED_UI(panel_generated, label_generated, btn_close_msg, frame_generated);
    }

    /**
     * Creates a UI pop-up message saying Program cannot continue if there is not CSV database
     */
    private void NO_CSV(){

        frame_no_csv = new JFrame("ERROR! NO CSV!");
        JPanel panel_no_csv = new JPanel();
        JLabel label_complete = new JLabel("Program could not find a CSV database! Please try again", SwingConstants.CENTER);

        JButton btn_no_csv = new JButton("Try Again");
        btn_no_csv.setActionCommand("TRY_AGAIN");
        GENERATED_UI(panel_no_csv, label_complete, btn_no_csv, frame_no_csv);

    }

    /**
     * Generates a UI pop-up confirming the completed generation of the text files and closes the program
     */
    private void COMPLETE(){

        frame_main.setVisible(false);
        JFrame frame_complete = new JFrame("ALL DONE! :D");
        JPanel panel_complete = new JPanel();
        JLabel label_complete = new JLabel("Program has completed generating the files, you can find them in the 'Generated Files Here' directory!", SwingConstants.CENTER);
        JLabel label_complete_1 = new JLabel("You may now close the program!", SwingConstants.CENTER);

        JButton btn_ext_program = new JButton("Exit Program");
        btn_ext_program.setActionCommand("EXIT_PROGRAM");
        btn_ext_program.addActionListener(this);

        panel_complete.setLayout(new GridLayout(3, 1));
        panel_complete.add(label_complete);
        panel_complete.add(label_complete_1);
        panel_complete.add(btn_ext_program);

        frame_complete.setMinimumSize(new Dimension(720, 180));
        frame_complete.add(panel_complete);

        frame_complete.setLocationRelativeTo(null);
        frame_complete.setVisible(true);
        frame_complete.toFront();
        frame_complete.repaint();
        frame_complete.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * The main action function. All actions from the User Interface is performed here.
     * @param e - Action from User Interface window(s)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        switch(e.getActionCommand()){

            // When no CSV could be created
            case "TRY_AGAIN":
                frame_no_csv.dispose();
                break;

            // When a CSV has been generated correctly
            case "DONE":
                frame_generated.dispose();
                break;

            // Clears any and all frames when these buttons are pressed
            case "EXIT_PROGRAM":
            case "EXIT_1":
                for(Frame frame: Frame.getFrames()){
                    frame.dispose();
                }
                break;

            // Generates the data text files, is a 'DO EVERYTHING' button.
            // This can be improved so user can select specific data files to generate
            case "GENERATE FILES":

                String curr_dir = System.getProperty("user.dir");
                File[] curr_dir_files = new File(curr_dir).listFiles();

                // Checks if MavPASS_MasterCSV has been generated or provided
                boolean has_file = false;
                assert curr_dir_files != null;
                for (File file : curr_dir_files) {
                    if(file.getName().contains("MavPASS_MasterCSV")){
                        has_file = true;
                        break;
                    }
                }

                // Only generates the list if a CSV file is created or provided
                if (has_file){
                    programData = new ProgramData();

                    System.out.print("PROGRESS: [ ##########");
                    generate_list_1(0,"");
                    generate_list_1(1, "");


                    for (String d: new String[]{"A&H", "COB", "SBS", "CSET"}){
                        generate_list_1(2, d);
                        generate_list_1(3, d);
                        System.out.print("#####");
                    }
                    generate_list_2(true);
                    System.out.print("#####");
                    generate_list_2(false);
                    System.out.print("#####");
                    generate_list_3();
                    System.out.print("#####");
                    generate_list_4();
                    System.out.print("##### ] - DONE!");
                    COMPLETE();
                }
                // Gives an error message to the USER via a pop-up menu
                else{
                    System.out.println("PROGRAM NEEDS A CSV TO CONTINUE, PLEASE TRY AGAIN");
                    NO_CSV();
                }
                break;

            // Generates the MavPASS_MasterCSV.csv file as a "database"
            case "CREATE CSV":

                // Creates the File Chooser and temporary frame that will open
                JFrame temp_frame = new JFrame();
                temp_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                chooser.showOpenDialog(temp_frame);

                // Stores whatever files selected by the USER
                File[] files = chooser.getSelectedFiles();

                // Will only create the Master CSV file if ONLY '.xlsx' (Microsoft Excel) files were selected
                boolean files_okay = true;
                for(File file : files){
                    if(!file.getName().endsWith(".xlsx")){
                        files_okay = false;
                    }
                }

                // As long as more than 1 '.xlsx' file has been selected, program will create the Master CSV file
                if (files.length >= 1 && files_okay) {
                    try {
                        CreateMasterCSV.create(files);
                        System.out.println("CSV CREATED SUCCESSFULLY!");
                    } catch (IOException | InvalidFormatException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else {
                    System.out.println("NO FILES SELECTED --- NO CSV was CREATED");
                    break;
                }

                temp_frame.dispose();
                GENERATED();
                break;
        }
    }
}
