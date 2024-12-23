/**
File Name:	RoundedButton
Programmer:	Vincent Zhang
Lesson:         U4 Summative, ICS4U
Date:		August 23, 2023
Description:	Extension of the JButton class for custom rounded and bordered buttons for the main GUI
 */

package ovs.u4.summative;

//IMPORTED CLASSES
import javax.swing.JButton;         //JButton; the class which we will extend
import java.awt.Color;              //Color class; used for settings GUI colors
import java.awt.Graphics;           //Graphics class; used for painting the button component
import java.awt.Graphics2D;         //Graphics2D class; also used for painting the component
import java.awt.RenderingHints;     //RenderingHints; used to smooth out graphics rendering
import java.awt.Font;               //Font; used to set font styles and size
import java.awt.event.MouseAdapter; //MouseAdapter; used to process mouse clicks
import java.awt.event.MouseEvent;   //MouseEvent; also used to process mouse clicks

public class RoundedButton extends JButton {
    
    private int radius = 15;        //the radius of the rounded corner, default 15 pixels
    final private Color bgColor;    //the background color of the button
    private Color borderColor;      //the border color of the button
    
    /**
     * Constructor used to create new RoundedButton objects
     * @param text the text of the button
     * @param radius the radius of its corners
     */
    public RoundedButton(String text, int radius) {     
        //Color variable assignments
        bgColor = new Color(30, 136, 56, 200);       //default background color set to dark green
        borderColor = new Color(30, 136, 56);   //default border color set to the same dark green
        
        this.radius = radius;   //assign the radius passed as an argument to the global radius of the button object
        
        setText(text);      //set the button text to whatever was passed as an argument
        
        setFont(new Font("Courier New", Font.PLAIN, 16));  //set the font of the button
        
        setForeground(Color.white);   //set text colour to white
        
        setContentAreaFilled(false);  //call this method to make the transparency of the rounded corners to work
        
        setBackground(bgColor);     //set the background of the button to dark green
        
        setBorderPainted(false);     //remove default border paint
        
        setFocusable(false);   //remove ugly border around text after being clicked
        
        addMouseListener(new MouseAdapter() {           //add a new mouse listener from the following anonymous class
            @Override       
            public void mouseEntered(MouseEvent me) {   //override the mouseEntered event
                borderColor = Color.white;      //set the border color to white
                repaint();          //repaint the component 
            }

            @Override
            public void mouseExited(MouseEvent me) {  //override the mouseExited event
                borderColor = bgColor;          //reset the border color to be the same as the background
                repaint();      //repaint the button
            }
        });
    }

    /**
     * Overrided method used to paint the RoundedButton
     * @param 
     */
    @Override
    protected void paintComponent(Graphics g) {    
        Graphics2D g2 = (Graphics2D) g;   //convert the graphics in the argument to a Graphics2D object
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //turn on anti-aliasing to smooth out the paint
        
        g2.setColor(borderColor);   //set the border color
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);    //paint the outer rectangle
        
        g2.setColor(bgColor);  //set the button background color (to dark green)
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);    //paint the inner rectangle (border will be 2 pixels wide)

        super.paintComponent(g);        //paint the component
    }
}
