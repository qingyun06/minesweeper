/**
File Name:	TileButton
Programmer:	Vincent Zhang
Lesson:         U4 Summative, ICS4U
Date:		August 23, 2023
Description:	Extension of the JButton class to create custom tile button objects for the main application
 */

package ovs.u4.summative;

//IMPORTED CLASSES
import java.awt.Color;                          //Color; used for GUI color display
import java.awt.Dimension;                      //Dimension; used for setting sizes of the TileButton object
import javax.swing.JButton;                     //JButton; the class we will extend
import javax.swing.border.LineBorder;           //LineBorder; used for button border
import java.awt.Font;                           //Font; used to set font metrics
import java.awt.event.MouseAdapter;             //MouseAdapter; used to process mouse events (clicks and what not)
import java.awt.event.MouseEvent;               //MouseEvent; used for the same purpose
import javax.swing.SwingUtilities;              //SwingUtilities; used to process which mouse button was clicked
import javax.swing.ImageIcon;                   //ImageIcon; used to assign png icons to buttons
import javax.swing.plaf.metal.MetalButtonUI;    //MetalButtonUI; overrided to set the disabled text font color


public class TileButton extends JButton {    //custom extension and modification of the JButton class
    
    MineSweeper application;  //declare a MineSweeper variable (for communication between the application and the TileButton object)
    
    int row, column;                //the row and column of the TileButton
    boolean coveredState = true;    //whether or not the tile is covered
    boolean flaggedState = false;   //whether or not the tile is flagged
    Color tileColour;               //the color of the tile
    
    final static Color COVERED_COLOR_1 = new Color(170, 215, 81);  //light green tile
    final static Color COVERED_COLOR_2 = new Color(162, 209, 73);  //dark green tile
    final static Color UNCOVERED_COLOR_1 = new Color(229, 194, 159); //light brown
    final static Color UNCOVERED_COLOR_2 = new Color(213, 183, 152); //dark brown

    final private static Color COLOR_1 = Color.BLUE;                    //blue
    final private static Color COLOR_2 = new Color(99, 155, 84);    //green
    final private static Color COLOR_3 = Color.red;     //red
    final private static Color COLOR_4 = Color.magenta;     //magenta
    final private static Color COLOR_5 = new Color(219, 73, 20);    //orange
    final private static Color COLOR_6 = new Color(204, 255, 255);  //light blue
    
    public TileButton(int row, int column) {    //constructs the TileButton object using JButton superclass methods
        
        this.row = row;         //the row of the tile
        this.column = column;   //the column of the tile

        setPreferredSize(new Dimension(30, 30));        //set the size to 30 x 30 pixels
        setBorderPainted(false);      //remove button border    
        setFocusable(false);   //remove focusable attribute to remove ugly border around text after being clicked
        setFocusPainted(false);         //remove focus painted for the same reason
        setBorder(new LineBorder(Color.white));     //give the button a border
        setFont(new Font("Courier New", Font.PLAIN, 22));   //set the button font

        paintTile();        //paint the tile
        
        tileColour = getBackground();   //get the tile button color
        
        addMouseListener(new MouseAdapter() {       //add a custom Mouse Listener
            MineSweeper application;    //get ready to link the TileButton object to the main application
            boolean pressed;            //update the boolean pressed variable
            
            @Override
            public void mousePressed(MouseEvent e) {  //override the mousePressed method
                getModel().setArmed(true);      //arm the button
                getModel().setPressed(true);    //press the button
                pressed = true;                     //update the pressed boolean
            }

            @Override
            public void mouseReleased(MouseEvent e) {       //override the mouseReleased method
                TileButton tb = (TileButton) e.getSource();         //get the TileButton object and store it in a variable
                application = (MineSweeper) (tb.getParent()).getParent();   //get the main application object (MineSweeper class)
                
                getModel().setArmed(false);         //disarm the button
                getModel().setPressed(false);       //unpress the button
                
                ImageIcon flagIcon = new ImageIcon(  //create a new flag icon 
                        application.flagIcon.getImage().getScaledInstance(  //from the main application
                                30, 30, java.awt.Image.SCALE_SMOOTH));  //scale to 30 x 30 pixels
                
                if (pressed) {          //if the button was pressed, 
                    if (SwingUtilities.isRightMouseButton(e)) {    //and was right click pressed
                        if (isEnabled()) {                  //if the button is enabled (hasn't been cleared or flagged)
                            if (application.flags >= 1) {   //and if the flag counter is greater than 1, 
                                setEnabled(false);          //disable the button
                                setIcon(flagIcon);          //set the icon to the flag icon
                                setDisabledIcon(flagIcon);  //(only works if the icon and the disabled icon are the same for some reason)
                                flaggedState = true;                    //update the flaggedState boolean
                                application.flagCountLabel.setText(Integer.toString(--application.flags));  //update the header flag counter on the main application
                            }
                        } else {                         //if the button is disabled 
                            if (coveredState) {           //and the button is covered, (i.e. flagged)
                                setEnabled(true);           //enable the button again
                                setIcon(null);      //remove the flag icon
                                flaggedState = false;           //update the flaggedState boolean
                                application.flagCountLabel.setText(Integer.toString(++application.flags));  //add the flag back to the inventory
                            }
                        }
                        
                    } else if (SwingUtilities.isLeftMouseButton(e)) {   //if the button was left clicked, 
                        if (isEnabled()) {                          //and if the button is enabled (i.e. not flagged)
                            if (application.firstClick == false) {  //if the user has already started the round,    
                                if (application.newGame) {              //and if it is a new game (not continuing from save file)
                                    application.generateField(row, column); //generate the playing field
                                    application.startTimer();           //start the timer
                                }
                                application.firstClick = true;      //update the main application's firstClick boolean 
                            }   
                            uncover();      //uncover the tile and all adjacent tiles recursively (pretty cool)
                            application.checkWinState();            //check if user has won
                        }
                    }
                }
                pressed = false;        //update the pressed boolean of this anonymous class
            }

            @Override
            public void mouseExited(MouseEvent e) {     //override the mouseExited method
                if (coveredState) {         //if the tile is covered, 
                    setBackground(tileColour);  //restore the tile color
                    pressed = false;                //update the pressed boolean
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {    //override the mouseEntered method 
                if (coveredState) {             //if the tile is covered, 
                    setBackground(new Color(191, 225, 125));    //set the color (cool hover indicator look)
                    pressed = true;     //update the pressed boolean
                }
            }
        });         //end anonymouse class declaration
    }
    
    /**
     * Method used to paint the tile (final keyword makes overriding this method impossible)
     */
    public final void paintTile() {   
        if (coveredState) {         //if the tile is covered, 
            switch (row % 2) {      //the color is determined depending on the location of the tile, 
                case 0 -> {             //if the row is even
                    if (column % 2 == 0) {      //and the column is even
                        setBackground(COVERED_COLOR_1);  //set the tile color to light green
                    } else {                    //if the row is even and the column is odd, 
                        setBackground(COVERED_COLOR_2);     //set the tile color to dark green
                    }
                    break;      //break out of the switch
                }
                case 1 -> {     //if the row is odd, 
                    if (column % 2 == 1) {          //and the column is odd
                        setBackground(COVERED_COLOR_1);  //set the tile color to light green
                    } else {                    //if the row is odd and the column is even, 
                        setBackground(COVERED_COLOR_2);     //set the tile color to dark green
                    }
                }
            }
            
            if (flaggedState) {         //if the tile is flagged, 
                ImageIcon flagIcon = new ImageIcon(         //create a new image icon
                    application.flagIcon.getImage().getScaledInstance(    //of the flag icon png
                        30, 30, java.awt.Image.SCALE_SMOOTH));  //scale to 30 x 30 pixels
                setEnabled(false);          //disable the button
                setIcon(flagIcon);          //set the button icon to the flag icon  
                setDisabledIcon(flagIcon);    //set the disabled icon to the flag icon as well
            }
            
        } else {            //if the tile is not covered (i.e. has been cleared)
            int value = application.gameboard[row][column];     //get the value of the tile (0, 1, 2, etc.)
            
            if (value > 0) {        //if the value is not 0
                
                String s = Integer.toString(value); //parse the value into a string
                
                switch (s) {        //set the color of the text depending on the number
                    
                    case "1" -> {           //if the value is 1
                        setUI(new MetalButtonUI() {     //set the UI to a new anonymous MetalButtonUI (this is probably very inefficient but it works)
                            @Override
                            protected Color getDisabledTextColor() {    //override the getDisabledTextColor method
                                return COLOR_1;         //set the disabled text color to blue
                            }
                        });         //end anonyomus class
                        break;  //break out of the switch
                    }
                    case "2" -> {           //if the value is 2
                        setUI(new MetalButtonUI() {     //set the UI to a new anonymous MetalButtonUI
                            @Override
                            protected Color getDisabledTextColor() {    //override the getDisabledTextColor method  
                                return COLOR_2;   //set the disabled text color to green
                            } 
                        });
                        break;   //break out of the switch
                    }
                    case "3" -> {
                        setUI(new MetalButtonUI() {  //set the UI to a new anonymous MetalButtonUI
                            @Override
                            protected Color getDisabledTextColor() {   //override the getDisabledTextColor method
                                return COLOR_3;    //set the disabled text color to red
                            }
                        });
                        break;    //break out of the switch
                    }
                    case "4" -> {
                        setUI(new MetalButtonUI() {  //set the UI to a new anonymous MetalButtonUI
                            @Override
                            protected Color getDisabledTextColor() {  //override the getDisabledTextColor method
                                return COLOR_4;   //set the disabled text color to magenta
                            }
                        });
                        break;    //break out of the switch
                    }
                    case "5" -> {
                        setUI(new MetalButtonUI() {   //set the UI to a new anonymous MetalButtonUI
                            @Override
                            protected Color getDisabledTextColor() {   //override the getDisabledTextColor method
                                return COLOR_5;    //set the disabled text color to orange
                            }
                        });
                        break;    //break out of the switch
                    }
                    case "6" -> {
                        setUI(new MetalButtonUI() {  //set the UI to a new anonymous MetalButtonUI
                            @Override
                            protected Color getDisabledTextColor() {   //override the getDisabledTextColor method
                                return COLOR_6;   //set the disabled text color to light blue
                            }
                        });
                        break;    //break out of the switch
                    }
                    default -> {
                        setForeground(Color.black);   //default to black (may be buggy and set to grey)
                    }
                }
                setText(s);  //set the button text to whatever number the tile is
            }

            switch (row % 2) {      //set the color of the tile depending on its location on the board (checkered look)
                case 0 -> {             //if the row is even
                    if (column % 2 == 0) {      //and the column is even
                        setBackground(UNCOVERED_COLOR_1);  //set background to light brown
                    } else {                                //if the row is even and the column is odd
                        setBackground(UNCOVERED_COLOR_2);       //set the background to dark brown
                    }
                    break;          //break out of the switch statement
                }
                case 1 -> {                 //if the row is odd
                    if (column % 2 == 1) {          //and the column is odd
                        setBackground(UNCOVERED_COLOR_1);  //set background to light brown
                    } else {                        //if the row is odd and the column is even, 
                        setBackground(UNCOVERED_COLOR_2);       //set background to dark brown
                    }
                }
            }
        }
    }
    
    /**
     * Method used to get the covered state of the tile
     * @return boolean covered state; true if covered, false if not
     */
    public boolean getCoveredState() {      
        return coveredState;        //return the coveredState boolean
    }
    
    /**
     * method used to set the covered state of a tile object
     * @param b true if want to set the covered state to true, false if false
     */
    public void setCoveredState(boolean b) {    
        coveredState = b;   //set the covered state to the specified boolean
        setEnabled(b);      //enable or disable the tile
        paintTile();        //update the tile's paint job
    }
    
    /**
     * Recursive method used to uncover tiles
     */
    public void uncover() {
        
        if (application.gameboard[row][column] == -1) {   //if the tile is a mine and the user has clicked on it, 
            application.gameOver(false);        //call the gameOver method from the main application class, let it know the player lost
            return;         //exit out of the function
        }
        
        if (flaggedState) {               //also, if the tile is flagged (but doesn't have a mine)  
            flaggedState = false;           //unflag the tile
            setIcon(null);          //remove the flag icon from the tile
            setDisabledIcon(null);  //remove the flag icon from the tile (for some reason only works with these two lines)
            application.flags += 1;             //add the flag back to the inventory
            application.flagCountLabel.setText(Integer.toString(application.flags));    //update the header counter
        }
        
        setCoveredState(false);     //uncover the tile
        
        if (application.gameboard[row][column] == 0) {      //if the tile's number is zero (no mines in any adjacent tiles)
            
            for (int x = row - 1; x <= row + 1; x++) {          //for the current row and the adjacent rows, 
                
                for (int y = column - 1; y <= column + 1; y++) {    //and for the current column and the adjacent columns, 
                    
                    if (x < 0               //if the selected row does not exist (out of left upper bound)
                            || y < 0                //or the selected column does not exist (out of left bound)
                            || x == application.rowCount    //or if the selected row is out of the lower bound
                            || y == application.columnCount) {  //or if the selected column is out of the right bound, 
                        continue;           //keep going
                    }
                    
                    if (application.gameboard[x][y] == -1) {    //if the adjacent tile is a mine, 
                        continue;               //keep going
                    }
                    
                    if (application.tiles[x][y].coveredState == false) { //if the adjacent tile is already uncovered, 
                        continue;           //keep going
                    }  
                    
                    application.tiles[x][y].uncover();  //otherwise, uncover that tile and all its adjacent tiles as well (RECURSION!!)
                }
            }
        }        
    }
}
