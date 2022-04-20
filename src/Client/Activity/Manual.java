package Client.Activity;

import java.io.*;
import java.util.Scanner;

/**
 * This class does display basic commands options to the client
 */
public class Manual {

    private File manFile;
    private Scanner reader;

    public void Manual () throws FileNotFoundException {
        this.manFile = new File("Text/manualText");

        if (!manFile.exists() || !manFile.canRead()){
            System.out.println("Error while reading from the manual text file!");
            throw new FileNotFoundException("Error while reading from the file!");
        }

        this.reader = new Scanner(this.manFile);
    }

    public void show (){
        String line = new String();
        while (line != null){
            line = reader.nextLine();
            System.out.println(line);
        }
    }

}
