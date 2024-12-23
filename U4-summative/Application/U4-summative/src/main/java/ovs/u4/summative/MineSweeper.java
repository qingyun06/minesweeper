/**
File Name:	MineSweeper
Programmer:	Vincent Zhang
Lesson:         U4 Summative, ICS4U
Date:		August 23, 2023
Description:	MineSweeper game application; demonstrates a wide variety of programming techniques, including
                * Algorithms - linear search, quick sort, randomization, recursion; 
                * Object Orientation and Encapsulation - program is divided into multiple files; 
                * User Input; 
                * External file reading and writing
                * Graphical User Interface (GUI) design and implementation
 */

package ovs.u4.summative;

//IMPORTED PACKAGES AND CLASSES
import java.util.Random;            //Random; used to randomly generate the locations of mines
import java.awt.Color;              //Color; used for color implementation in the GUI
import java.awt.Dimension;          //Dimension; used to set the sizes of various components
import java.awt.FlowLayout;         //FlowLayout; component layout manager for components aligned left to right
import java.awt.Font;               //Font; used to set font metrics of various components
import java.awt.GridLayout;         //GridLayout; component layout manager for uniform grids
import java.util.ArrayList;         //ArrayList; used to store, sort, and manage data
import javax.swing.*;               //swing.*; all swing components (e.g. buttons, panels, etc.)
import java.awt.*;                  //awt.*; awt utilities and constants
import javax.imageio.ImageIO;       //ImageIO; image processing
import java.io.File;                //File; used for external file reading
import java.io.IOException;         //IOException; exceptions that might arise from reading files
import javax.swing.BoxLayout;       //BoxLayout; component layout manager used for top to bottom component alignment
import javax.swing.Timer;           //Timer; used to keep track of game time
import java.awt.event.*;            //event.*; all events, such as mouse clicks, timer events, button events, etc. 
import java.io.FileWriter;          //FileWriter; used for writing to external files
import java.io.FileNotFoundException;   //FileNotFoundException; exception when FileWriter or File cannot find the specificed file
import java.util.Scanner;           //Scanner; used to read external files
import java.time.format.DateTimeFormatter;  //DateTimeFormatter; used to format dates
import java.time.LocalDateTime;         //LocalDateTime; used to get the local time and date


public class MineSweeper extends JLayeredPane {    //Class to create application GUI frame object
        
    boolean firstClick = false;   //boolean to keep track of when user first clicks on board
    boolean newGame = true;       //boolean to indicate if the application should start a new round
    boolean gameOver = false;     //boolean to indicate if the game is over
    
    int[][] gameboard;     //2d integer array to store gameboard data (i.e. location of each mine)
    TileButton[][] tiles;  //parallel array of graphical buttons
    
    int rowCount;       //integer to store number of rows
    int columnCount;    //integer to store number of columns
    int flags = 30;     //integer to keep track of number of flags
    int timer = 0;      //integer variable for timer
    boolean settingsMenuShown = false;  //boolean to keep track of if the settings menu is being displayed
    
    ImageIcon flagIcon;         //ImageIcon object for the flag icon
    ImageIcon clockIcon;        //ImageIcon object for the clock icon
    ImageIcon settingsIcon;     //ImageIcon object for the settings icon
    ImageIcon mineIcon;         //ImageIcon object for the mine icon
    
    final static Font MAIN_FONT = new Font("Courier New", Font.PLAIN, 22);   //main font of the GUI
    
    ArrayList<Integer> recordArray;         //array list variable for the score record
    ArrayList<String> recordDatesArray;     //parallel array list for the dates of each record
    
    Timer t;   //timer object
    
    public MineSweeper() {   //main JLayeredPane application constructor (called in Main class)
        
        gameboard = new int[14][18];     //declare new 14 x 18 integer array for the location of each mine
        tiles = new TileButton[14][18];  //declare parallel GUI button array
        
        rowCount = gameboard.length;        //assign value to rowCount
        columnCount = gameboard[0].length;  //assign value to columnCount
        
        File saveFile = new File("saved.txt");  //find "saved.txt" in the directory
        if (saveFile.isFile()) {        //if save file exists, 
            newGame = false;            //set newGame to false
        }
        
        //ASSIGN VALUES TO IMAGE ICON VARIABLES
        try {                                   //try the following variable assignments and catch any IOExceptions

            flagIcon = new ImageIcon(ImageIO.read(new File("flag.png")));       //assign flag icon to flagIcon variable
            clockIcon = new ImageIcon(ImageIO.read(new File("clock.png")));     //assign clock icon to clockIcon variable
            settingsIcon = new ImageIcon(ImageIO.read(new File("settings.png")));  //assign settings icon to settingsIcon variable
            mineIcon = new ImageIcon(ImageIO.read(new File("mine.png")));          //assign mine icon to settingsIcon variable 

            
        } catch (IOException ioe) {    //catch IO exceptions (whatever that means)
            return;                    //safely exit out of function
        }
        
        generateForm();  //call initializer method (see below)
    }

    /**
     * Method used to construct the components of the JFrame application form
     */
    private void generateForm() {
        
        setPreferredSize(new Dimension(columnCount * 30, rowCount * 30 + 60)); //set preferred size of the JFrame form
                
        initializeSplashPanel();    //initialize the splash (welcome screen) panel
        initializeGlassPanel();     //initialize the glass panel for mouse click intercepting
        initializeHeaderPanel();    //initialize the header panel
        initializePlayingField();   //initialize the main playing field panel
        initializeSettingsButton(); //initialize the settings button
        initializeSettingsPanel();  //initialize the settings menu panel
        initializeGameOverPanel();  //initialize the game over notification panel
        
        add(headerPanel, (Integer) 1);          //add the header to the Layered Pane (1 indicates bottom-most layer)
        add(playingFieldPanel, (Integer) 1);    //add the playing field to the Layered Pane
        add(glassPanel, (Integer) 4);           //add the glass panel to the layered pane (the higher the second parameter, the closer it is to the top of the layered pane)
        add(splashPanel, (Integer) 5);          //add the splash panel to the layered pane
        add(settingsLabel, (Integer) 5);        //add the settings button to the layered pane
        add(settingsPanel, (Integer) 5);        //add the settings panel to the layered pane
        add(gameOverPanel, (Integer) 5);        //add the game over notification panel to the layered pane
    
        setVisible(true);           //make the layered pane visible
    }
    
    /**
     * Method used to initialize the header panel
     */
    private void initializeHeaderPanel() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 10));  //create a new JPanel object with the FlowLayout manager
        headerPanel.setBackground(new Color(74, 117, 44));  //set background colour of the header panel to dark green

        flagCountLabel = new JLabel(             //create a new JLabel object for the flag counter
                new ImageIcon(                   //create a new ImageIcon object
                    flagIcon.getImage()     //of the flagIcon png in the package directory
                        .getScaledInstance(           //scale the flagIcon png in the package directory to 
                            40, 40, java.awt.Image.SCALE_SMOOTH))   //40 x 40 pixels 
        );
        
        flagCountLabel.setText(Integer.toString(flags));                         //set label text to the number of flags
        flagCountLabel.setFont(new Font("Courier New", Font.PLAIN, 25));  //set the label font
        flagCountLabel.setForeground(Color.white);                                  //set the label font colour to white
        flagCountLabel.setPreferredSize(new Dimension(75, 40));             //set the preferred size of the label

        timerLabel = new JLabel(        //create a new JLabel object for the timer
                new ImageIcon(                  //create a new ImageIcon object
                    clockIcon.getImage()    //of the clockIcon png in the package directory
                        .getScaledInstance(         //scale the clockIcon png in the package directory to 
                                40, 40, java.awt.Image.SCALE_SMOOTH))  //40 x 40 pixels
        );
        timerLabel.setText(Integer.toString(timer));            //set label text to the timer (seconds counter variable)
        timerLabel.setFont(new Font("Courier New", Font.PLAIN, 25));    //set the label font
        timerLabel.setForeground(Color.white);                                    //set the label font colour
        timerLabel.setHorizontalAlignment(JLabel.LEADING);                   //align the timer to the left of the header            
        timerLabel.setPreferredSize(new Dimension(columnCount * 30 - 140, 40));  //set the preferred size of the label

        headerPanel.add(flagCountLabel);    //add the flagCount label to the header panel
        headerPanel.add(timerLabel);        //add the timer label to the header panel

        headerPanel.setBounds(0, 0, columnCount * 30, 60);  //set the size and location of the header panel
    }
    
    /**
     * Method used to initialize the glass panel
     */
    private void initializeGlassPanel() {
        glassPanel = new JPanel();      //create a new JPanel object
        glassPanel.setSize(new Dimension(columnCount * 30, rowCount * 30 + 60));  //set the size of the panel to be equal to the size of the frame
        glassPanel.setOpaque(false);        //make panel transparent (glass)
        glassPanel.setVisible(true);          //make panel "visible"
        glassPanel.addMouseListener(new MouseAdapter() {  //add an anonymous mouse listener object to the panel to intercept mouse clicks
            @Override
            public void mouseReleased(MouseEvent e) {   //execute function when user clicks on glass panel 
                if (splashPanel.isVisible()) {          //if splash panel is visible, 
                    return;                             //do nothing
                }
                if (settingsMenuShown) {      //if settings menu is shown on screen, 
                    settingsPanel.setVisible(false);   //hide the settings menu
                    settingsMenuShown = false;              //update the boolean variable accordingly
                    if (!gameOver) {                        //if the game isn't over, 
                        glassPanel.setVisible(false);      //make glass panel invisible
                    }
                } 
            }
        });
        
        glassPanel.setFocusable(true);    //make glass panel focusable (to intercept mouse clicks)
    }
    
    /**
     * Method used to initialize the splash (welcome screen) panel 
     */
    private void initializeSplashPanel() {
        JPanel textPanel = new JPanel() {   //create a new JPanel object using the following anonymous class
            @Override
            protected void paintComponent(Graphics g) {     //override the paint component method
                Graphics2D graphics = (Graphics2D) g;       //cast the Graphics parameter into a Graphics2D object
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //turn on anti-aliasing (makes it smoother)

                graphics.setColor(Color.white);  //set the color of the border
                graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);  //paint the outer rectangle
                
                graphics.setColor(new Color(30, 136, 56));  //set the color of the inside rectangle
                graphics.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);  //paint rectangle on top of the previous rectangle
            }
        };
        
        textPanel.setLayout(new GridLayout(12, 1, 0, 0));  //set the panel's layout manager to GridLayout manager with 12 rows and 1 column
        textPanel.setPreferredSize(new Dimension(400, 250)); //set the size of the panel
        textPanel.setOpaque(false);     //make transparent for rounded borders to work
        
        //SET SPLASH SCREEN TEXT USING CENTER-ALIGNED JLABELS
        JLabel splashTitle = new JLabel("MineSweeper", JLabel.CENTER);              
        JLabel splashSubtitle = new JLabel("By Vincent Zhang", JLabel.CENTER); 
        JLabel splashText1 = new JLabel("LEFT CLICK a tile to clear it.", JLabel.CENTER);
        JLabel splashText2 = new JLabel("RIGHT CLICK a tile to flag it.", JLabel.CENTER);
        JLabel splashText3 = new JLabel("The numbers on cleared tiles indicate how", JLabel.CENTER);        //all repeating code...
        JLabel splashText4 = new JLabel("many mines are adjacent to that tile.", JLabel.CENTER);
        JLabel splashText5 = new JLabel("Find all mines as fast as you can!", JLabel.CENTER);
        JLabel splashText6 = new JLabel("*Load last save?", JLabel.CENTER);
        
        //SET JLABEL ALIGNMENTS TO CENTER
        splashTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);         //see above, all repeating code
        splashSubtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        splashText1.setAlignmentX(JLabel.CENTER_ALIGNMENT); 
        splashText2.setAlignmentX(JLabel.CENTER_ALIGNMENT);  
        splashText3.setAlignmentX(JLabel.CENTER_ALIGNMENT); 
        splashText4.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        splashText5.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        splashText6.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        //SET JLABEL FONTS
        splashTitle.setFont(new Font("Courier New", Font.PLAIN, 30));       //all repeating code
        splashSubtitle.setFont(new Font("Courier New", Font.PLAIN, 12));
        splashText1.setFont(new Font("Courier New", Font.PLAIN, 14));
        splashText2.setFont(new Font("Courier New", Font.PLAIN, 14));
        splashText3.setFont(new Font("Courier New", Font.PLAIN, 14));
        splashText4.setFont(new Font("Courier New", Font.PLAIN, 14));
        splashText5.setFont(new Font("Courier New", Font.PLAIN, 14));
        splashText6.setFont(new Font("Courier New", Font.PLAIN, 14));

        //SET JLABEL FONT COLORS
        splashTitle.setForeground(Color.white);         //repeating code
        splashSubtitle.setForeground(Color.white);
        splashText1.setForeground(Color.white); 
        splashText2.setForeground(Color.white); 
        splashText3.setForeground(Color.white); 
        splashText4.setForeground(Color.white);
        splashText5.setForeground(Color.white); 
        splashText6.setForeground(Color.yellow);
        
        //ADD JLABELS TO SPLASH PANEL
        textPanel.add(Box.createRigidArea(new Dimension(1, 15)));  //padding material
        textPanel.add(splashTitle); 
        textPanel.add(splashSubtitle);              //repeating code, don't worry about it
        textPanel.add(splashText1);
        textPanel.add(splashText2);
        textPanel.add(Box.createRigidArea(new Dimension(1, 10)));  //more padding
        textPanel.add(splashText3);
        textPanel.add(splashText4);
        textPanel.add(Box.createRigidArea(new Dimension(1, 10)));  //even more padding
        textPanel.add(splashText5); 
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 0, 0));  //create a new JPanel object for buttons
        buttonPanel.setOpaque(false);           //make transparent for rounded borders to work
        
        if (!newGame) {                      //if save file detected, 
            textPanel.add(splashText6);  //add "Load last save?" text prompt
            
            RoundedButton continueButton = new RoundedButton("Continue", 15);  //create a new RoundedButton object
            continueButton.setHorizontalAlignment(JButton.CENTER);  //center button text

            continueButton.addActionListener((ActionEvent e) -> {  //add an anonymous functional class (detect when button clicked)
                splashPanel.setVisible(false);     //make splash panel disappear
                glassPanel.setVisible(false);       //make glass panel disappear
                getSavedData();                             //get data from save file
                flagCountLabel.setText(Integer.toString(flags));    //update the header flagcount
                timerLabel.setText(Integer.toString(timer));        //update the header timer
                startTimer();       //start the timer
                repaint();          //update the form
            });

            buttonPanel.add(continueButton);  //add the continue button to the button panel
        }

        RoundedButton newGameButton = new RoundedButton("Start New Game", 15);  //create a new RoundedButton object
        newGameButton.setHorizontalAlignment(JButton.CENTER);       //center-align button text
        newGameButton.addActionListener((ActionEvent e) -> {    //add an anonymous functional class to detect mouse clicks
            splashPanel.setVisible(false);  //make splash panel disappear
            glassPanel.setVisible(false);   //make glasspanel disappear
            newGame = true;     //set newgame boolean variable to true
        });
        
        buttonPanel.add(newGameButton); //add button to buttonPanel
        
        splashPanel = new JPanel();   //create a new JPanel object

        splashPanel.setLayout(new BoxLayout(splashPanel, BoxLayout.PAGE_AXIS));  //set panel layout manager to BoxLayout manager
        splashPanel.setOpaque(false);       //make invisible
        
        splashPanel.add(textPanel);         //add text panel to splash panel
        splashPanel.add(Box.createRigidArea(new Dimension(1, 10)));  //add a spacer
        splashPanel.add(buttonPanel);   //add the button panel

        splashPanel.setBounds((columnCount * 30 - 400) / 2, 100, 400, 300);  //set location and size of panel
        splashPanel.setVisible(true);   //make splash panel appear
    }
    
    /**
     * Method used to initialize the settings button
     */
    private void initializeSettingsButton() {
        settingsLabel = new JLabel(                //create a new JLabel object
                new ImageIcon(                      //set its icon to a new ImageIcon object
                    settingsIcon                //that is the settings.png in the package directory
                            .getImage()                //get the image
                            .getScaledInstance(          //scale it to 
                                40, 40, java.awt.Image.SCALE_SMOOTH))); //40 x 40 pixels
        
        settingsLabel.addMouseListener(new MouseAdapter() {  //add an anonymous mouse adapter class to the button
            @Override
            public void mouseReleased(MouseEvent e) {   //override the mouse released method
                if (splashPanel.isVisible()                 //if the splash panel is visible
                        || gameOverPanel.isVisible()) {     //or if the game over panel is visible     
                    return;                                 //do nothing
                }
                if (settingsMenuShown) {                    //if the settings menu is shown,  
                    settingsPanel.setVisible(false);   //make it disappear
                    glassPanel.setVisible(false);      //make the glass panel disappear
                    settingsMenuShown = false;              //update this boolean variable accordingly
                } else {                                    //otherwise 
                    settingsPanel.setVisible(true);    //make the settings panel visible
                    glassPanel.setVisible(true);       //make the glass panel visible
                    settingsMenuShown = true;               //update the boolean variable accordingly
                }
            }
        });
        
        settingsLabel.setBounds(columnCount * 30 - 50, 10, 40, 40);  //set location and shape of the settings menu
    }
    
    /**
     * Method used to initialize the settings menu panel
     */
    private void initializeSettingsPanel() {
        settingsPanel = new JPanel(new GridLayout(4, 1)) {  //create a new anonymous JPanel object
            @Override
            protected void paintComponent(Graphics g) {  //override the paintComponent method
                Graphics2D graphics = (Graphics2D) g;    //convert the Graphics object into a Graphics2D object (better)
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  //set rendering hints (whatever that means)

                graphics.setColor(new Color(30, 136, 56));  //set the color to dark green
                graphics.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);  //draw a round rectangle
            }
        };
        
        settingsPanel.addMouseListener(new MouseAdapter() {});    //intercept mouse clicks to prevent panel from closing when clicked
        settingsPanel.setVisible(false);        //set default visibility to invisible
        
        //CREATE SETTINGS MENU BUTTONS
        RoundedButton scoreRecordButton = new RoundedButton("Score Record", 0);  //repeating code
        RoundedButton aboutButton = new RoundedButton("How To Play", 0);
        RoundedButton quitButton = new RoundedButton("Save and Quit", 0);
        
        //SET BUTTON TEXT ALIGNMENTS TO LEFT
        scoreRecordButton.setHorizontalAlignment(JButton.LEFT);     //repeating code
        aboutButton.setHorizontalAlignment(JButton.LEFT); 
        quitButton.setHorizontalAlignment(JButton.LEFT);
        
        //ADD BUTTON ACTION LISTENERS TO EACH BUTTON 
        scoreRecordButton.addActionListener((ActionEvent) -> {  //add anonymous functional class to the first button
            new ScoreRecordForm().setVisible(true);         //create and display a new score record window
        });
        
        aboutButton.addActionListener((ActionEvent) -> {        //add anonymous functional class to the second button
            try {       //try the following code and catch any exceptions
                File file = new File("how_to_play.txt");     //look for how_to_play.txt
                if (!Desktop.isDesktopSupported()) {        //check if Desktop is supported by Platform or not  
                    System.out.println("not supported");    //print message to console if not
                    return;         //exit
                }
                Desktop desktop = Desktop.getDesktop();     //create a new Desktop object
                if (file.exists()) {            //checks file exists or not  
                    desktop.open(file);         //opens the specified file (file opening depends on OS)
                }
            } catch (Exception e) {} //catch any exceptions
        }); 
        
        quitButton.addActionListener((ActionEvent e) -> {  //add anonymous functional action listener to the "save and quit" button
            if (!gameOver && firstClick) {       //if the user has started the round and the round hasn't ended, 
                saveGameData();     //save the game data
            }
            ((MineSweeperFrame) (((RoundedButton) e.getSource())  //dispose of the application
                    .getParent()        //parent of the button is the settings menu panel
                    .getParent()        //parent of the settings menu panel is the layered pane
                    .getParent()        //parent of the layered pane is the content frame 
                    .getParent()        //the parent of the layered pane ... i actually don't know 
                    .getParent()        //trust me, the great great great... 
                    .getParent()))      //great great grandparent turns out to be the JFrame
                    .dispose();         //which we dispose using the dispose method
        });

        settingsPanel.add(new JLabel());         //add a filler component to the GridLayout
        settingsPanel.add(scoreRecordButton);   //add the score record button to the gridlayout
        settingsPanel.add(aboutButton);         //add the about button to the gridlayout
        settingsPanel.add(quitButton);          //add the save andquit button to the gridlayout
        
        settingsPanel.setBounds(columnCount * 30 - 210, 10, 200, 200); //set the size and location of the settings panel
    }
    
    /**
     * Method used to initialize the game end notification panel
     */
    private void initializeGameOverPanel() {
        gameOverPanel = new JPanel();           //create a new JPanel object
        gameOverPanel.setLayout(new FlowLayout(FlowLayout.CENTER));     //use the FlowLayout layout manager (center-aligned)
        gameOverPanel.setOpaque(false);     //make transparent
        gameOverPanel.setBorder(null);       //remove border
        gameOverPanel.setBounds((columnCount * 30 - 200) / 2, 100, 200, 200);  //set size and location
        gameOverPanel.setVisible(false);    //make invisible
    }
    
    /**
     * Method used to retrieve data from the save file
     */
    private void getSavedData() {
        try {           //try the following code
            File file = new File("saved.txt");  //look for saved.txt in the package directory
            Scanner scanner = new Scanner(file);    //create a new Scanner object for the save file
            
            flags = Integer.parseInt(scanner.nextLine());   //the first line is the number of flags
            timer = Integer.parseInt(scanner.nextLine());   //the second line is the timer
            
            String[] gameboardRows = scanner.nextLine().split(",");  //the third and forth lines represent gameboard data
            String[] row;       //temporary string array for each row
            
            for (int x = 0; x < gameboardRows.length; x++) {  //for each row in the gameboard data
                row = gameboardRows[x].split(" ");  //split each entry by spaces
                for (int y = 0; y < row.length; y++) {              //for each charcater split by spaces
                    gameboard[x][y] = Integer.parseInt(row[y]);   //assign value to the current gameboard
                }
            }
            
            gameboardRows = scanner.nextLine().split(",");  //the fourth line represents the coveredState of all tiles
            for (int x = 0; x < gameboardRows.length; x++) {     //for each row
                row = gameboardRows[x].split(" ");          //split the data by space
                for (int y = 0; y < gameboard[0].length; y++) {   //for each character (either T or F) split by a space
                    if (row[y].equals("F")) {               //if the character is F, 
                        tiles[x][y].setCoveredState(false);      //uncover the tile
                    }
                }
            }
            
            while (scanner.hasNextLine()) {         //all following lines represent the locations of flags
                String[] coordinates = scanner.nextLine().split(" ");   //split each line by space
                if (coordinates.length > 1) {               //if the line contains a coordinate (is not empty)
                    int x = Integer.parseInt(coordinates[0]); //get the row
                    int y = Integer.parseInt(coordinates[1]); //get the column
                    tiles[x][y].flaggedState = true;          //set the flagged state of the corresponding tile
                    tiles[x][y].paintTile();                //update the tile paint
                }
            }
        
            scanner.close();  //close the scanner
            
        } catch (FileNotFoundException e) {}   //catch any FileNotFoundExceptions
    }
    
    /**
     * method used to initialize the main playing field
     */
    private void initializePlayingField() {
        playingFieldPanel = new JPanel(new GridLayout(rowCount, columnCount, 0, 0));  //create a new JPanel object managed by the GridLayout layout manager
        
        for (int x = 0; x < tiles.length; x++) {  //for each row,  
            for (int y = 0; y < tiles[0].length; y++) {  //and for each column,        
                tiles[x][y] = new TileButton(x, y);  //create a new TileButton object
                playingFieldPanel.add(tiles[x][y]);  //add the TileButton object to the GridLayout
                tiles[x][y].application = this;     //let the TileButton know who is its parent
            }
        }
        
        playingFieldPanel.setBounds(0, 60, columnCount * 30, rowCount * 30);  //set the location and size of the playing field
    }
    
    /**
     * Method used to start the global timer variable t
     */
    public void startTimer() {
        t = new Timer(1000, (ActionEvent) -> {      //create a new Timer object that triggers an event every 1000 milliseconds (1 second)
            timerLabel.setText(Integer.toString(++timer)); //update the header timer text label
        });
        t.start();      //start the timer
    }
    
    /**
     * method used to check if the user has won
     */
    public void checkWinState() {
        for (int x = 0; x < gameboard.length; x++) {            //for each row
            for (int y = 0; y < gameboard[0].length; y++) {         //and for each column
                if (gameboard[x][y] != -1) {                        //if the tile is not a mine
                    if (tiles[x][y].coveredState == true) return;   //but if it is uncovered, return
                }
            }
        }
        gameOver(true);         //if loop gets through all tiles without returning, trigger the gameOver method
    }
    
    /**
     * Method used to generate the game board when user clicks the board for the first time
     * (makes sure that user does not click on a mine on the first click
     * @param startRow the row of the starting tile
     * @param startColumn the column of the starting tile
     */
    public void generateField(int startRow, int startColumn) {
        Random r = new Random();        //create a new Random object for random number generation
        
        int mines = 30;  //assign a temporary counter variable to be equal to the number of mines
        
        int x, y; //declare integer variables x and y (as coordinates)
        
        //RANDOMLY ASSIGN MINES TO TILES (indicate by -1)
        while (mines > 0) {                 //while there are still mines, randomly place them on the board
            
            x = r.nextInt(rowCount);        //pick a random row
            y = r.nextInt(columnCount);     //pick a random column
            
            for (int adjRow = x - 1; adjRow <= x + 1; adjRow++) {    //for the selected row, the row above, and the row below
                
                for (int adjCol = y - 1; adjCol <= y + 1; adjCol ++) {  //and for the selected column and the adjacent columns
                    
                    if (adjRow < 0                      //if the adjacent row is less than 0
                            || adjRow == rowCount       //or greater than the row count
                            || adjCol < 0               //or if the adjacent column is less than 0
                            || adjCol == columnCount) continue;     //or is greater than the column count, continue
                    
                    if (adjRow == startRow && adjCol == startColumn) {   //if the randomly selected square is the square the user clicked on, 
                        x = r.nextInt(rowCount);            //select another random row
                        y = r.nextInt(columnCount);         //and another random column
                    }
                }
            }
            
            if (gameboard[x][y] == -1) continue;    //if the algorithm has already placed a mine on the randomly selected tile, continue
            else gameboard[x][y] = -1;      //otherwise, place the mine
            mines--;                        //subtract one mine from the inventory
        }
        
        //UPDATE NUMERIC VALUES OF EACH TILE (numbers indicate how many mines are adjacent to that tile)
        for (int row = 0; row < rowCount; row++) {                  //for each row 
            
            for (int column = 0; column < columnCount; column++) {  //and for each column, 
            
                if (gameboard[row][column] == -1) {    //if the tile in the row and column of the current iteration contains a mine (-1)
                
                    for (x = row - 1; x <= row + 1; x++) {   //for the current row and the rows above and below, 
                    
                        for (y = column - 1; y <= column + 1; y++) {   //and for the current column and the columns to the left and right, 
                        
                            if (x < 0 
                                || y < 0                 //if the tile does not exist, 
                                || x >= rowCount         //(i.e.
                                || y >= columnCount) {   //is outside of the playing field boundaries)
                                continue;                //continue
                            }   
                            
                            if (gameboard[x][y] == -1) { //if the tile is a mine (-1), don't add 1; 
                                continue;                //continue
                            }
                            
                            gameboard[x][y] += 1;   //otherwise, add 1 to the tile number
                        }
                    }
                }
            }
        }
        
        //print board data  (FOR TESTING PURPOSES ONLY)
        System.out.println();       //line break
        for (x = 0; x < rowCount; x++) {                //for each row
            for (y = 0; y < columnCount; y++) {         //and for each column
                if (y == columnCount - 1) {             //if at the end of the column, 
                    if (gameboard[x][y] == -1) {        //if the tile is a mine
                        System.out.println("x");        //print x and a new line
                    } else {                                    //if not a mine
                        System.out.println(gameboard[x][y]);    //print the number and a new line
                    }
                } else {                                //if not at the end of a column
                    if (gameboard[x][y] == -1) {        //if tile is a mine, 
                        System.out.print("x ");         //print x and a space
                    } else {                                    //if tile is not a mine
                        System.out.print(gameboard[x][y] + " ");        //print the number and a space
                    }
                }
            }
        }
    }
    
    /**
     * Method used to save game data (upon clicking "Save and Quit" in the settings menu)
     */
    private void saveGameData() {
        try {   //try the following code: 
            FileWriter writer = new FileWriter("saved.txt");    //create a new file writer object with the target set to saved.txt
            writer.write(flags + System.lineSeparator() + timer + System.lineSeparator());   //write the flag count and the timer, separated by line breaks
            
            for (int x = 0; x < rowCount; x++) {        //for each row, 
                for (int y = 0; y < columnCount; y++) {     //and for each column
                    if (y == columnCount - 1) {                 //if at the end of the column, 
                        writer.write(Integer.toString(gameboard[x][y]) + ",");  //write the tile data, followed by a comma
                    } else {                                                    //for all other tiles, 
                        writer.write(Integer.toString(gameboard[x][y]) + " ");  //write the tile data followed by a space
                    }
                }
            }
            
            writer.write(System.lineSeparator());   //write a line break
            
            for (int x = 0; x < rowCount; x++) {            //for each row, 
                for (int y = 0; y < columnCount; y++) {     //and for each column   
                    if (y == columnCount - 1) {             //if at the end of the column
                        if (tiles[x][y].coveredState) {     //write T followed by a comma 
                            writer.write("T,");         //if the tile is covered
                        } else {                            //otherwise, 
                            writer.write("F,");         //write an F followed by a comma (comma indicates new row)
                        }
                    } else {                                //for all other tiles, 
                        if (tiles[x][y].coveredState) {     //if the tile is covered,
                            writer.write("T ");         //write T followed by a space
                        } else {                            //if the tile is uncovered, 
                            writer.write("F ");         //write F followed by a space
                        }
                    }
                }
            }
            
            writer.write(System.lineSeparator());       //line break
            
            for (int x = 0; x < rowCount; x++) {            //finally, for each row
                for (int y = 0; y < columnCount; y++) {         //and for each column
                    if (tiles[x][y].flaggedState) {         //if the current tile in the iteration is flagged, 
                        writer.write(x + " " + y + System.lineSeparator());     //write the coordinates of that tile
                    }
                }
            }
            
            writer.close();     //close the writer
            
        } catch (IOException e) {           //catch IOExceptions
            System.out.println("An error occurred.");  //print an error message that no one will see
        }
    }
    
    /**
     * Processes that occur when the game ends
     * @param win true if the player has won, false if the player has lost
     */
    public void gameOver(boolean win) {
        t.stop();    //stop the timer
        
        JPanel textPanel = new JPanel() {       //create a new JPanel object from the following anonymous class
            @Override
            protected void paintComponent(Graphics g) {     //override the paint component method (for rounded borders0
                Graphics2D graphics = (Graphics2D) g;       //convert the Graphics object argument into a Graphics2D object (superior)
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      //turn on anti-aliasing (makes graphic smoother)

                graphics.setColor(new Color(0, 0, 0, 50));         //set the border color
                graphics.fillRoundRect(0, 0, 200, getHeight(), 15, 15);   //paint the border (larger outer rectangle)

                graphics.setColor(new Color(30, 136, 56, 200));      //set the panel color (dark green)
                graphics.fillRoundRect(1, 1, 200 - 2, getHeight() - 2, 15, 15);  //paint the inner rectangle on top of the outer white rectangle painted above
            }
        };
        
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS)); //set the layout manager to the boxlayout (vertical alignment)
        textPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);           //align the inner panel to the center of the outer panel
        textPanel.setPreferredSize(new Dimension(200, 100));      //set the preferred size of the inner panel
        textPanel.add(Box.createVerticalGlue());        //add some expandable vertical glue (to vertically center the panel text)
        
        RoundedButton yesButton = new RoundedButton("Yes", 15); //create a new RoundedButton object
        yesButton.addActionListener((ActionEvent e) -> {            //add an ActionEvent listener to know when button is pressed
            ((MineSweeperFrame) (((RoundedButton) e.getSource())    //eventually get the frame
                    .getParent()      //the parent of the RoundedButton is the inner text panel
                    .getParent()      //the parent of the inner text panel is the outer game over panel
                    .getParent()      //the parent of the outer game over panel is the layered pane
                    .getParent()      //the parent of the layered pane ...
                    .getParent()      //etc. 
                    .getParent()      //etc. 
                    .getParent()))    //eventually, the great great... great grandparent is the JFrame (trust me)
                    .newGame();       //call the newGame method in the MineSweeperFrame.java class
        });
        
        JPanel buttonPanel = new JPanel();      //create a new JPanel object for the buttons
        buttonPanel.setLayout(new GridLayout(1, 2, 0, 15)); //set the layout manager to GridLayout manager
        buttonPanel.setOpaque(false);       //make transparent so the rounded button borders work
        
        RoundedButton noButton = new RoundedButton("No", 15);  //create a new RoundedButton object
        noButton.addActionListener((ActionEvent e) -> {     //add an anonymous ActionListener
            gameOverPanel.setVisible(false);          //which closes the gameOverPanel when clicked
            glassPanel.setVisible(true);                //make the glass panel visible so user can't click on tiles
        });
        
        buttonPanel.add(yesButton);     //add the two buttons
        buttonPanel.add(noButton);      //to the button panel
        
        buttonPanel.setPreferredSize(new Dimension(200, 30));  //set the preferred size of the button panel
        
        buttonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);     //center align the button panel on the outer JPanel
        
        if (win) {      //if the player won, 
            try {           //try the following code: 
                FileWriter writer = new FileWriter("record.txt", true); //create a new FileWriter object, with the target set to record.txt and append set to true
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  //create a date formatter object
                LocalDateTime now = LocalDateTime.now();            //get the current date and time
                
                writer.write(Integer.toString(timer) + " on " + dtf.format(now) +  System.lineSeparator());  //write the timer and the date to the text file
                writer.close();         //close the FileWriter
                
                
            } catch (IOException e) {               //catch IOExceptions
                return;     //safely exit out of function
            }
            
            sortRecord();       //sort the record (see method below)
            
            if (recordArray.get(0) == timer    //if the first element in the sorted array is equal to the timer   
                 && !(recordArray.size() > 1 && recordArray.get(1) == timer)) {     //and there are no repeats of that timer record, 
                    JLabel newRecordLabel = new JLabel("New Record!", JLabel.CENTER);   //create a new JLabel object
                    newRecordLabel.setFont(MAIN_FONT);           //set the font to the main Courier font
                    newRecordLabel.setForeground(Color.white);          //set the font color to white
                    newRecordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);  //horizontally align the text to the center

                    textPanel.add(newRecordLabel);  //add the JLabel to the text panel
            } else {
                JLabel winLabel = new JLabel("You Win!");    //create a new JLabel object for the notification
                winLabel.setForeground(Color.white);                    //set the font color to white
                winLabel.setFont(MAIN_FONT);                            //set the font to the main font 
                winLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);    //set the component alignment to center

                textPanel.add(winLabel);        //add JLabel to the text panel
            }

            JLabel scoreLabel = new JLabel(Integer.toString(timer), JLabel.CENTER); //create a new JLabel object for the score
            scoreLabel.setForeground(Color.white);                                  //set the font color to white
            scoreLabel.setFont(new Font("Courier", Font.BOLD, 25));     //set the font 
            scoreLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);      //horizontally align the component

            textPanel.add(scoreLabel);      //add JLabel to the text panel
            
        } else {        //if the player lost, 
            ImageIcon mine = new ImageIcon(     //create a new ImageIcon object
                                mineIcon.getImage() //of the flagIcon png in the package directory
                                        .getScaledInstance( //scale the flagIcon png in the package directory to 
                                            30, 30, java.awt.Image.SCALE_SMOOTH)); //30 x 30 pixels );
            
            for (int row = 0; row < rowCount; row++) {                  //for each row, 
                for (int column = 0; column < columnCount; column++) {  //and for each column, 
                    if (gameboard[row][column] == -1) {                 //if the tile in the current iteration is a mine, 
                        tiles[row][column].setIcon(mine);     //reveal the mine icon 
                    }
                }
            }
            
            JLabel gameOverLabel = new JLabel("Game Over!", JLabel.CENTER);  //create a new JLabel object
            gameOverLabel.setFont(new Font("Courier", Font.BOLD, 25));  //set the label font
            gameOverLabel.setForeground(Color.white);                   //set the font color to white
            gameOverLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);   //horizontally align the JLabel
            
            textPanel.add(gameOverLabel);       //add the JLabel to the text panel
        } 

        new File("saved.txt").delete();   //once the user has finished a game, delete the old save file


        JLabel playAgainLabel = new JLabel("Play Again?", JLabel.CENTER);   //create a new JLabel object
        playAgainLabel.setFont(new Font("Courier", Font.PLAIN, 16));        //set the font
        playAgainLabel.setForeground(Color.white);                          //set the font color to white
        playAgainLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);      //horizontally center the component

        textPanel.add(playAgainLabel);      //add the component to the text panel
        
        textPanel.add(Box.createVerticalGlue());        //add some vertical glue (to vertically center all the text)

        gameOverPanel.add(textPanel);       //add the inner text panel to the outer invisible panel
        gameOverPanel.add(buttonPanel);     //add the button panel to the outer invisible panel
        gameOverPanel.setVisible(true);     //make everything visible     
        glassPanel.setVisible(true);        //make the glass panel visible 
        
        gameOver = true;   //update the gameOver boolean
    }
    
    /**
     * Method used to sort the record file
     */
    private void sortRecord() {
        recordArray = new ArrayList();          //create new array list objects
        recordDatesArray = new ArrayList();     //for both arrays
        
        //GET DATA FROM TXT FILE
        try {       //try the following code
            File file = new File("record.txt");     //find the record.txt file
            Scanner scanner = new Scanner(file);    //create a scanner for that file

            while (scanner.hasNextLine()) {             //while the scanner has next lines
                String entry = scanner.nextLine();          //get the string of the line
                recordArray.add(Integer.parseInt(entry.substring(0, entry.indexOf(" ")))); //add the score value (substring before the space)
                recordDatesArray.add(entry.substring(entry.indexOf(" ") + 1));  //add the dates to the parallel array
            }
            
        } catch (FileNotFoundException e) {     //catch FileNotFoundExceptions
            return;     //exit out of function
        }
        
        if (recordArray.isEmpty()) {        //if array is empty, no need to sort
            return;     //exit out of function
        }
        
        quickSort(recordArray, recordDatesArray, 0, recordArray.size() - 1); //quick sort both arrays

        //SORT TXT FILE ENTRIES
        try {           //then, try the following code
            FileWriter writer = new FileWriter("record.txt");   //create a new file writer object, with append set to false
            
            for (int x = 0; x < recordArray.size(); x++) {                          //for each entry in the recordArray list
                writer.write(Integer.toString(recordArray.get(x)) + " " + recordDatesArray.get(x) + System.lineSeparator());    //print the score and the date in ascending order of score
            }
            
            writer.close(); //close the writer
            
        } catch (IOException e) {}           //Catch any IO exceptions
    }
    
    /**
     * Method used to sort the scores
     * @param al the array list of scores
     * @param parallel the parallel array list of dates
     * @param left the left bound of the sorting algorithm
     * @param right the right bound of the sorting algorithm
     */
    private static void quickSort(ArrayList<Integer> al, ArrayList<String> parallel, int left, int right) {  // the quick method array takes 3 parameters; the array we want sorted, right value, left value
        int i = left;                         //lowest part of array
        int j = right;                        //highest part of array
        int temp;                             //temporary int variable to store the swapping element of the main array list
        String temp1;                         //temporary string variable to store contents of the parallel array
        int pivot = al.get((left + right) / 2);  //we select a pivot by taking the most left and most right value and finds the midpoint array value
        
        while (i <= j) {                      //start the sorting process
            
            while (al.get(i) < pivot) {  //if the data is less than the pivot,
                i++;       //keep moving forward
            }
            while (al.get(j) > pivot) {  //if the data on the right side is greater than the pivot,
                j--;       // we move down and don't do any changes
            }
            
            if (i <= j) {                     //otherwise, swap the data
                temp = al.get(i);      //temporary variable to store contents of element at current index
                al.set(i, al.get(j));   //move index at j to i
                al.set(j, temp);    //move element at index i (in the temporary variable) to index j
                
                temp1 = parallel.get(i);    //temporary variable to store element of parallel array at current index, 
                parallel.set(i, parallel.get(j));   //move the element at index j to index i
                parallel.set(j, temp1);     //move the element at index i (stored in the temporary variable) to index j
                
                i++;                          //update i counter variable
                j--;                          //update j counter variable
            }
        }
        if (left < j) {     
            quickSort(al, parallel, left, j);   //continue the above process
        }
        if (i < right) {
            quickSort(al, parallel, i, right);  //until the array is fully sorted
        }
    }
    
    //COMPONENT VARIABLES DECLARATION
    private JPanel playingFieldPanel;       //the main playing field
    private JPanel headerPanel;             //the header
    public JLabel flagCountLabel;           //the flag counter in the header
    public JLabel timerLabel;               //the timer in the header
    private JLabel settingsLabel;           //the settings icon button
    private JPanel settingsPanel;           //the settings menu panel
    private JPanel splashPanel;             //the welcome screen
    private JPanel glassPanel;              //the invisible glass panel used to intercept mouse clicks
    private JPanel gameOverPanel;           //the game over screen
}



