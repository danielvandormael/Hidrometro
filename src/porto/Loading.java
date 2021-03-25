package porto;

import javax.swing.*;
import java.awt.*;

public class Loading {
    JFrame Load = new JFrame("Please Wait");

    public void createRoot(){
        JLabel text = new JLabel("Loading...");
        JPanel root = new JPanel();
        root.add(text);
        Load.getContentPane().add(text);
        Load.setSize(300, 200);
        Load.setLocationRelativeTo(null);
        Load.setVisible(true);
    }

    public void Dispose(){
        Load.dispose();
    }
}
