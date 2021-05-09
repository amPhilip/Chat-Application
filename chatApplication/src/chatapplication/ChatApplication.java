/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatapplication;

import java.awt.Dimension;
import java.awt.Toolkit;
/**
 *
 * @author favored
 */
public class ChatApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        
        serverApplication serverApp = new serverApplication();
        serverApp.setVisible(true);        
        serverApp.setSize(width / 2, height / 2);
        serverApp.setLocation(width / 4, height / 4);
    }
    
}
