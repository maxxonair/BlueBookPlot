package menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import main.BlueBookPlot;
import serviceFunctions.ImportSetupWindow;

public class FileMenu {

	
	public static JMenu create() {
        JMenu menuPoint = new JMenu("File");
        menuPoint.setForeground(BlueBookPlot.getLabelColor());
        menuPoint.setBackground(BlueBookPlot.getBackgroundColor());
        menuPoint.setFont(BlueBookPlot.getSmallFont());
        menuPoint.setMnemonic(KeyEvent.VK_A);
        
        
        JMenuItem itemSelectResult = new JMenuItem("Select ResultFile  "); 
        itemSelectResult.setForeground(Color.black);
        itemSelectResult.setFont(BlueBookPlot.getSmallFont());
        menuPoint.add(itemSelectResult);
        itemSelectResult.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   
                       SwingUtilities.invokeLater(new Runnable() {
                           public void run() {
                               try {
               					ImportSetupWindow.createAndShowGUI();
               				} catch (IOException  e) {System.out.println(e);}
                           }
                       });
                	   
                	   
                    } });
        
        JMenuItem itemSelectVariableList = new JMenuItem("Select VariableList  "); 
        itemSelectVariableList.setForeground(Color.black);
        itemSelectVariableList.setFont(BlueBookPlot.getSmallFont());
      //  itemSelectVariableList.setAccelerator(KeyStroke.getKeyStroke(
      //          KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuPoint.add(itemSelectVariableList);
        itemSelectVariableList.addActionListener(new ActionListener() {
                   public void actionPerformed(ActionEvent e) {
                	   BlueBookPlot.selectResultFile(itemSelectVariableList);
                    } });
       return menuPoint;        
	}
}
