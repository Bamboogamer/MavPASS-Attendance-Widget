package main.project;

//import com.mysql.cj.util.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class CreateMasterCSV {

    // "Unique" data ID starts at 1
    public static int data_id = 1;

    public static void main(String[] args) {}

    /**
     * Executes the code, taking from a directory with EXCEL FILES you want to concatenate into a master csv file.
     * Creates a master csv file into the same directory, then runs excel2csv() for every EXCEL sheet in the directory.
     *
     * Should create a MavPASS_MasterCSV.csv file with all the relevant information needed for this program to work
     */
    public static void create(File[] files) throws IOException, InvalidFormatException {

        PrintWriter master_csv = new PrintWriter("MavPASS_MasterCSV.csv");

        // Writes the first line of the CSV master file
        String first_line = "Data_ID,Last_Name,First_Name,Instructor,MavPASS_Leader,Session_Date,Class_Code\n";
        master_csv.write(first_line);

        // Goes through entire directory for all files that end with ".xlsx" and converts it into a string of useful
        // information and adding it to the master CSV file
        assert files != null;
        for (File excel_file: files){
            if(excel_file.getName().endsWith(".xlsx")){
                excel2csv(excel_file, master_csv);
            }
        }
        master_csv.close();
    }

    /**
     * Takes a single excel sheet and adds the information from it into a csv file
     * @param excel - File, an Excel file (preferably a Microsoft Office Excel file) with ONE (1) sheet on it
     * @param csv - PrintWriter, the master CSV file you want to write into
     */
    public static void excel2csv(File excel, PrintWriter csv) throws IOException, InvalidFormatException {

        XSSFWorkbook myExcelBook = new XSSFWorkbook(excel);
        XSSFSheet myExcelSheet = myExcelBook.getSheet("Sheet1");

        // Goes through entire Excel sheet by ROW
        for (Row cells : myExcelSheet) {
            XSSFRow row = (XSSFRow) cells;

            // Relevant Data
            String last_name = row.getCell(5).toString();
            String first_name = row.getCell(6).toString();

            // Format names to catch as may errors as possible
            last_name = format_name(last_name);
            first_name = format_name(first_name);

            // Getting the info from the individual cells (specific columns)
            String instructor = row.getCell(8).toString();
            String mavpass_leader = row.getCell(9).toString();
            String session_date_str = row.getCell(10).toString();
            String class_code = row.getCell(11).toString();

            // Actual line of data stored as:
            // [data_id],[last_name],[first_name],[instructor],[mp_leader],[session_date],[class_code]
            String data_line = String.format("%s,%s,%s,%s,%s,%s,%s%n",
                    data_id,
                    last_name,
                    first_name,
                    instructor,
                    mavpass_leader,
                    session_date_str,
                    class_code);

            // Skips the "Column Names" Row
            if (data_line.contains("class code") || data_line.contains("course code")){
                continue;
            }

            csv.write(data_line);
            data_id++;
        }
    }

    /**
     * Takes a String name and removes any case sensitivity, extra spaces, or special characters that may cause issues
     * in the future. Formats names into a more readable and in more "valid" format for easy email-generation.
     * @param name - String, inputted name
     * @return String, formatted name that is "prettier" to look at and easier to work with
     */
    public static String format_name(String name){

        // Ignore case-sensitivity
        name = name.toLowerCase();

        // Names with special characters AND spaces
        if ((name.contains("-") || name.contains("'")) && name.contains(" ")){
            String[] split_name = name.split("\\s+");

            for(int i = 0; i < split_name.length; i++){

                // Capitalizes letters after dashes (-)
                split_name[i] = split_name[i].replace('-', ' ');
                split_name[i] = WordUtils.capitalize(split_name[i]);
                split_name[i] = split_name[i].replace(' ', '-');

                // Capitalizes letters after apostrophes (')
                split_name[i] = split_name[i].replace('\'', ' ');
                split_name[i] = WordUtils.capitalize(split_name[i]);
                split_name[i] = split_name[i].replace(' ', '\'');
            }

            name = String.join(" ", split_name);
        }

        // Names with special characters but NO spaces
        else if ((name.contains("-") || name.contains("'")) && !name.contains(" ")){
            // Capitalizes letters after dashes (-)
            name = name.replace('-', ' ');
            name = WordUtils.capitalize(name);
            name = name.replace(' ', '-');

            // Capitalizes letters after apostrophes (')
            name = name.replace('\'', ' ');
            name = WordUtils.capitalize(name);
            name = name.replace(' ', '\'');
        }

        // Capitalize the name
        name = WordUtils.capitalize(name);

        StringBuilder sb_name = new StringBuilder();

        // Removes any ' (X minutes)' from name (from office hours)
        for (Character c : name.toCharArray()){
            if(c.equals('(')){
                break;
            }
            sb_name.append(c);
        }
        return sb_name.toString().strip();
    }
}
