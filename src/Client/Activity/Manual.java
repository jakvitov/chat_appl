package Client.Activity;

import java.io.*;
import java.util.Scanner;

/**
 * This class does display basic commands options to the client, this is used only by the CLI client
 * If the file with the manual is not found, we print error message instead of the man
 * on the request
 */
public class Manual {

    private File manFile;
    private Scanner reader;
    private boolean canRead;

    //We open all the streams, if we cannot read from the file, we set the flag
    //and we notify the client
    public void Manual (){
        this.manFile = new File("file:src/Client/Text/manualText");

        if (!manFile.exists() || !manFile.canRead()) {
            this.canRead = false;
            return;
        }

        try {
            this.reader = new Scanner(this.manFile);
        }
        catch (FileNotFoundException FNFE){
            this.canRead = false;
            return;
        }

        this.canRead = true;
    }

    //Show the manual
    public void show (){
        if (this.canRead == true){
            String line = new String();
            while (reader.hasNext()){
                line = reader.nextLine();
                System.out.println(line);
            }
        }
        else {
            System.out.println("The manual text file cannot be found!");
        }
    }

}
