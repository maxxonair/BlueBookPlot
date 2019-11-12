package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;


import menu.FileMenu;
import menu.SettingMenu;
import menu.SidePanel;
import serviceFunctions.FileWatcher;
import serviceFunctions.BackgroundMenuBar;
import serviceFunctions.PlotPanelManager;



public class BlueBookPlot {

	static private String PROJECT_TITLE = "  BlueBook Plotting Toolkit - Mk1";
    static private Font smallFont	= new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 10);
    
    static private boolean isDarkTemplate = true;
   	
    private static Color labelColor =  new Color(220,220,220);    
   	private static Color backgroundColor = new Color(41,41,41);
   	
    private static String resultFilePath  = System.getProperty("user.dir") + "/results.txt"  ;
    
    private static String variableListPath  = System.getProperty("user.dir") + "/variableList"  ; 
    
    private static String iconFilePath  = System.getProperty("user.dir") + "/images/icon.png"  ; 
    
    private static String resultFileDelimiter=" ";
    
    private static int plotNumber=1;
    private static PlotPanelManager plotPanelManager;

    private static List<String> variableList = new ArrayList<String>();
    
	public static void createGUI() {
        plotPanelManager = new PlotPanelManager(1);
		
        JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame frame = new JFrame("" + PROJECT_TITLE);
        frame.setFont(smallFont);
        frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener(){

			@Override
			public void ancestorMoved(HierarchyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void ancestorResized(HierarchyEvent arg0) {
				boolean status = plotPanelManager.isTwoPlotTruncated();
				if( frame.getSize().width < frame.getSize().height && !status) {
					plotPanelManager.setTwoPlotTruncated(true);
					plotPanelManager.refresh(variableList, resultFilePath);	
				} else if(frame.getSize().width > frame.getSize().height && status) {
					plotPanelManager.setTwoPlotTruncated(false);
					plotPanelManager.refresh(variableList, resultFilePath);		
				}
			}           
        });
        //frame.setPreferredSize(new java.awt.Dimension(x_init, y_init));
        //------------------------------------------------------------------
        try {
        	variableList = readVariableList(System.getProperty("user.dir")+"/variableList");
		} catch (IOException e1) {
			System.err.println("ERROR: Variable list not recognized");
		}
        
        
      	BackgroundMenuBar menuBar = new BackgroundMenuBar();
        menuBar.setColor(new Color(250,250,250));
        menuBar.setOpaque(true);
        menuBar.setPreferredSize(new Dimension(1200, 25));
        frame.add(menuBar, BorderLayout.NORTH);
               
        menuBar.add(FileMenu.create());
        
        menuBar.add(SettingMenu.create());
        
        frame.add(SidePanel.create(), BorderLayout.WEST);
        
        JPanel plotPanel = plotPanelManager.createPlotPanel(variableList, resultFilePath);
         frame.add(plotPanel, BorderLayout.CENTER);        
        
         // ---------------------------------------------------------------------------------
         //       Define Task (FileWatcher) Update Result Overview
    	 	// ---------------------------------------------------------------------------------
    	  @SuppressWarnings("unused")
		FileWatcher task_Update = new FileWatcher( new File(resultFilePath) ) {
    		    protected void onChange( File file ) {
    		    	plotPanelManager.refresh(variableList, resultFilePath);
    		    }
    		  };
        
       	   Timer timer = new Timer();
     	  // repeat the check every second
     	   timer.schedule( task_Update , new Date(), 1000 );
        //------------------------------------------------------------------
           setGUIColors(true);
           
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        //final URL imageResource = BlueBookPlot.getClassLoader().getResource("resources/images/icon.gif");
        final Image image = defaultToolkit.getImage(iconFilePath);

        //this is new since JDK 9
        final Taskbar taskbar = Taskbar.getTaskbar();

        try {
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(image);
        } catch (final UnsupportedOperationException e) {
            System.out.println("The os does not support: 'taskbar.setIconImage'");
        } catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }

        //set icon for windows os (and other systems which do support this method)
        frame.setIconImage(image);
        
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
	createGUI();	
	}

	public static Color getLabelColor() {
		return labelColor;
	}

	public static Color getBackgroundColor() {
		return backgroundColor;
	}

	public static Font getSmallFont() {
		return smallFont;
	}

    
    public static void selectResultFile(Component itemSelectResult) {
        File myfile = new File(System.getProperty("user.dir"));
        	JFileChooser fileChooser = new JFileChooser(myfile);
       	if (fileChooser.showOpenDialog(itemSelectResult) == JFileChooser.APPROVE_OPTION) {
       		
       		File file = fileChooser.getSelectedFile() ;
       		resultFilePath = file.getAbsolutePath();
       		plotPanelManager.refresh(variableList, resultFilePath);
       	}
       	plotPanelManager.refresh(variableList, resultFilePath);
    }
    
    public static void selectVariableList(Component itemSelectVariableList) {
        File myfile = new File(System.getProperty("user.dir"));
        	JFileChooser fileChooser = new JFileChooser(myfile);
       	if (fileChooser.showOpenDialog(itemSelectVariableList) == JFileChooser.APPROVE_OPTION) {
       		
       		File file = fileChooser.getSelectedFile() ;
       		variableListPath = file.getAbsolutePath();
       		for(int i=variableList.size()-1;i>=0;i--) {
       			variableList.remove(i);
       		}
       		try {
				variableList = readVariableList(variableListPath);
	       		plotPanelManager.refresh(variableList, resultFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       	}
    }
    
    public static List<String> readVariableList(String filePath) throws IOException {
    	List<String> variableList = new ArrayList<String>();
      	 BufferedReader br = new BufferedReader(new FileReader(filePath));
       	 String strLine;
     try { 
    		      while ((strLine = br.readLine()) != null )   {
    		    	  String after = strLine.trim().replaceAll(" +", " ");
    		      	if(!after.isEmpty()) {
    		      	variableList.add(after);
    		      	}
    		      }
     }catch(NullPointerException eNPE) { System.out.println(eNPE);}
     br.close();
     if(variableList.size()==0) {
    	 System.err.println("ERROR: Variable List empty. Return 0");
     }
     return variableList;
    }
    

    
    public static void setGUIColors(boolean value) {
    	isDarkTemplate = value;
        if(isDarkTemplate) {
            labelColor = new Color(220,220,220);    
            backgroundColor = new Color(41,41,41);
          } else {
            labelColor =  Color.BLACK;    
          	  backgroundColor = Color.white;
          }
        plotPanelManager.refresh(variableList, resultFilePath);
    }

	public static boolean isDarkTemplate() {
		return isDarkTemplate;
	}

	public static int getPlotNumber() {
		return plotNumber;
	}

	public static void setPlotNumber(int plotNumber) {
		BlueBookPlot.plotNumber = plotNumber;
	}

	public static PlotPanelManager getPlotPanelManager() {
		return plotPanelManager;
	}

	public static String getResultFilePath() {
		return resultFilePath;
	}

	public static List<String> getVariableList() {
		return variableList;
	}

	public static void setResultFileDelimiter(String resultFileDelimiter) {
		BlueBookPlot.resultFileDelimiter = resultFileDelimiter;
	}

	public static String getResultFileDelimiter() {
		return resultFileDelimiter;
	}
	
	
	
}
