package View;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class TerminalWin extends JFrame
{
    private final String logPath = "file/temp/log.txt";

    private static int num = 0;
    private JTextPane paneText;
    private StyledDocument doc;
    private Style base = StyleContext.getDefaultStyleContext().
            getStyle(StyleContext.DEFAULT_STYLE);
    private Style normalStyle, greenStyle, redStyle;

    //singeltone
    private static TerminalWin instance = null;
    public static TerminalWin getInstance() {
        if(instance == null) {
            instance = new TerminalWin();
        }
        return instance;
    }
    private TerminalWin()
    {
        super("Web Crawler");
        setLocation(100,100);

        paneText = new JTextPane();
        paneText.setEditable(false);
        paneText.setBackground(Color.black);

        doc = paneText.getStyledDocument();

        normalStyle = doc.addStyle("normalStyle", base);
        StyleConstants.setFontSize(normalStyle, 18);
        StyleConstants.setFontFamily(normalStyle,"Consolas");
        StyleConstants.setForeground(normalStyle, Color.white);

        greenStyle = doc.addStyle("greenStyle", base);
        StyleConstants.setFontSize(greenStyle, 18);
        StyleConstants.setFontFamily(greenStyle,"Consolas");
        StyleConstants.setForeground(greenStyle, Color.green);

        redStyle = doc.addStyle("greenStyle", base);
        StyleConstants.setFontSize(redStyle, 18);
        StyleConstants.setFontFamily(redStyle,"Consolas");
        StyleConstants.setForeground(redStyle, Color.red);

        JScrollPane scrollPaneUp = new JScrollPane(paneText);
        scrollPaneUp.setPreferredSize(new Dimension(900, 500));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(scrollPaneUp);
        add(mainPanel);
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        clearLog();
    }

    public void print(String str)
    {
        try
        {
            doc.insertString(doc.getLength(),str + '\n', normalStyle);
        } catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        paneText.setCaretPosition(paneText.getDocument().getLength());
        printLog(str);
    }

    public void printGreen(String str)
    {
        try
        {
            doc.insertString(doc.getLength(),str + '\n', greenStyle);
        } catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        paneText.setCaretPosition(paneText.getDocument().getLength());
        printLog(str);
    }

    public void printRed(String str)
    {
        try
        {
            doc.insertString(doc.getLength(),str + '\n', redStyle);
        } catch (BadLocationException e)
        {
            e.printStackTrace();
        }
        paneText.setCaretPosition(paneText.getDocument().getLength());
        printLog(str);
    }

    public void printLog(String str){
        try {
            File file = new File(logPath);
            FileWriter out = new FileWriter(file,true);

            out.append(str + "\n");

            out.close();
        }
        catch (Exception e){
            System.out.println("Error create urls file: log.txt");
        }
    }

    public void clearLog(){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(logPath);
            pw.close();
        } catch (Exception e) {}

    }


}
