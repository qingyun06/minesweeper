/**
File Name:	ScoreRecordForm
Programmer:	Vincent Zhang
Lesson:         U4 Summative, ICS4U
Date:		August 23, 2023
Description:	Extension of the JFrame class, displays the score record when requested
 */

package ovs.u4.summative;

//IMPORTED CLASSES
import java.awt.Color;                  //Color: for setting the colors of various components
import java.io.File;                    //File: used for reading the record txt file
import java.io.FileNotFoundException;   //FileNotFoundException: may arise when looking for file
import java.util.Scanner;               //Scanner: used to parse the txt file
import javax.swing.JFrame;              //JFrame: the class to be extended
import javax.swing.JTextArea;           //JTextArea: the output text field component
import javax.swing.JScrollPane;         //JScrollPane: used to make the text area scrollable
import java.awt.Font;                   //Font: used to set the font of the component
import java.awt.Dimension;              //Dimension: used to set the size of the component

public class ScoreRecordForm extends JFrame {
    
    private final JTextArea textArea;     //the text area for output
    
    public ScoreRecordForm() {              //the constructor for the JFrame
        setTitle("Score Record");       //set the title of the JFrame
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  //set the default close operation
        
        setResizable(false);    //fix the size of the frame
                
        textArea = new JTextArea();     //assign a new value for the textArea variable
                
        textArea.setBackground(new Color(30, 136, 56));  //set the bcakground of the text area to dark green
        
        textArea.setForeground(Color.white);        //set the text color to white
        
        textArea.setFont(new Font("Courier", Font.PLAIN, 16));  //set the font
                
        textArea.setEditable(false);        //make the text area uneditable
        
        textArea.setCaretColor(new Color(30, 136, 56, 0));     //make the caret invisible
        
        getData(); //get the data from the text file (see below)
        
        JScrollPane scroll = new JScrollPane(textArea);     //make a new scroll pane object
        
        scroll.setPreferredSize(new Dimension(300, 200));       //set the size of the scroll pane

        scroll.setBorder(null);         //remove the scroll pane border
        
        add(scroll);    //add the scroll pane (and the text area) to the main JFrame
                
        pack();     //pack the frame (make everything fit perfectly)
    }
    
    /**
     * Method used to convert record data from the text file into GUI output
     */
    private void getData() {
        try {       //try the following code
            File file = new File("record.txt");     //find the record.txt file

            if (!file.exists()) {           //if the file doesn't exist
                textArea.setText("nothing to see here...");     //inform user
                return;         //return the function
            }

            Scanner scanner = new Scanner(file);    //create a scanner for the text file  

            String t = "SCORE RECORD" + System.lineSeparator();     //initialize a temporary string variable

            while (scanner.hasNextLine()) {             //while the scanner has next lines
                t += scanner.nextLine() + System.lineSeparator();   //add the contents of each line to the temporary string
            }

            textArea.setText(t);        //set the text area to the temporary string

        } catch (FileNotFoundException e) {}     //catch FileNotFoundExceptions
    }
}
