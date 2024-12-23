/**
File Name:	MineSweeperFrame
Programmer:	Vincent Zhang
Lesson:         U4 Summative, ICS4U
Date:		August 23, 2023
Description:	Extension of the JFrame class, the greatest grandparent container in the component tree, initialized in the Main class
 */

package ovs.u4.summative;

import javax.swing.JFrame;              //import the JFrame class
import javax.swing.SwingUtilities;      //import SwingUtilities class for component tree updating

public class MineSweeperFrame extends JFrame {      
    
    public MineSweeper game;   //declare a MineSweeper application program to link this object with the main application file
    
    /**
     * Constructor for the Frame object
     */
    public MineSweeperFrame() {         
        setTitle("Minesweeper");        //set the title of the application window
        setResizable(false);         //fix the size of the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  //sets the program to quit if the window is closed

        game = new MineSweeper();       //create a new MineSweeper application object and assign it to this variable
        add(game);                  //add the layered pane to this frame
        pack();                         //pack the frame (set the frame size to perfectly match with the elements of the game's display)
        setVisible(true);             //make the frame visible

    }
    
    /**
     * Method used to start a new game
     */
    public void newGame() {     
        remove(game);               //remove the current MineSweeper layered pane object
        game = new MineSweeper();       //create a new MineSweeper layered pane object
//        game.setVisible(true);     //make the new layered pane object visible
        add(game);                  //add it to this frame
        SwingUtilities.updateComponentTreeUI(this); //refresh the screen
    }

}
