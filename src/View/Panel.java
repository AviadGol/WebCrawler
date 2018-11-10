package View;

import Controller.WebCrawler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class Panel {
    public Button continBut, newBut;
    public Label urlLable, depthLable, threadLable;
    TextField urlText, depthText, threadText;

    public Panel() {
        Frame frame = new Frame("Web Crawler");
        //buttons
        newBut = new Button("Start new scan");
        newBut.setBounds(80, 230, 230, 50);;
        newBut.setFont(new Font("TimesRoman", Font.BOLD, 20));
        newBut.addActionListener(new ActionListener() {
                                     @Override
                                     public void actionPerformed(ActionEvent e) {
                                         //check if all fields is fill
                                         if(urlText.getText().equals("")||
                                                 depthText.getText().equals("") ||
                                                 threadText.getText().equals("")){
                                             JLabel label = new JLabel("Please fill in all fields");
                                             label.setFont(new Font("Arial", Font.BOLD, 18));
                                             JOptionPane.showMessageDialog(null,
                                                     label,"Message",
                                                     JOptionPane.INFORMATION_MESSAGE);
                                         }
                                         else{
                                             //check if depthText && threadText is number
                                             if(!depthText.getText().matches("\\d+") ||
                                                     !threadText.getText().matches("\\d+")){
                                                 JLabel label = new JLabel("Please enter a number in the fields: Recursion depth and Number of threads");
                                                 label.setFont(new Font("Arial", Font.BOLD, 18));
                                                 JOptionPane.showMessageDialog(null,
                                                         label,"Message",
                                                         JOptionPane.INFORMATION_MESSAGE);
                                             }
                                             else {
                                                 try{
                                                     //check if url proper
                                                     URL url = new URL(urlText.getText());
                                                     //start scan
                                                     new WebCrawler(url,Integer.valueOf(depthText.getText()),Integer.valueOf(threadText.getText()));

                                                     //close panel
                                                     frame.setVisible(false);
                                                     frame.dispose();
                                                 }catch (Exception ex){
                                                     JLabel label = new JLabel("URL field problem (necessary protocol)");
                                                     label.setFont(new Font("Arial", Font.BOLD, 18));
                                                     JOptionPane.showMessageDialog(null,
                                                             label,"Message",
                                                             JOptionPane.INFORMATION_MESSAGE);
                                                 }
                                             }
                                         }


                                     }
                                 });

        continBut = new Button("Continue the last scan");
        continBut.setBounds(80, 300, 230, 50);
        continBut.setFont(new Font("TimesRoman", Font.BOLD, 20));
        continBut.setBackground(Color.green);
        //set able just if exist all file
        if(!WebCrawler.isContinual())
            continBut.setEnabled(false);
        continBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //start continue scan
                new WebCrawler();
                //close panel
                frame.setVisible(false);
                frame.dispose();
            }
        });

        //url
        urlLable = new Label("URL:");
        urlLable.setBounds(20,50,50,30);
        urlLable.setFont(new Font("TimesRoman", Font.BOLD, 20));

        urlText = new TextField();
        urlText.setBounds(80,50,270,30);
        urlText.setEditable(true);
        urlText.setFont(new Font("Consolas", Font.BOLD, 20));

        //rec depth
        depthLable = new Label("Recursion depth:");
        depthLable.setBounds(20,100,170,30);
        depthLable.setFont(new Font("TimesRoman", Font.BOLD, 20));

        depthText = new TextField();
        depthText.setBounds(220,100,30,30);
        depthText.setEditable(true);
        depthText.setFont(new Font("Consolas", Font.BOLD, 20));

        //multi threads
        threadLable = new Label("Number of threads:");
        threadLable.setBounds(20,150,190,30);
        threadLable.setFont(new Font("TimesRoman", Font.BOLD, 20));

        threadText = new TextField();
        threadText.setBounds(220,150,30,30);
        threadText.setEditable(true);
        threadText.setFont(new Font("Consolas", Font.BOLD, 20));

        frame.add(continBut);
        frame.add(newBut);
        frame.add(urlLable);
        frame.add(urlText);
        frame.add(depthLable);
        frame.add(depthText);
        frame.add(threadLable);
        frame.add(threadText);
        frame.setSize(400, 400);
        frame.setLocation(500,300);
        frame.setLayout(null);
        frame.setVisible(true);

        //close program on X button
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

}