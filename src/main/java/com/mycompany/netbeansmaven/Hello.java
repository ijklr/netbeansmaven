package com.mycompany.netbeansmaven;
import javax.swing.*;
/**
 *
 * @author scheng
 */


public class Hello extends JFrame {
    public Hello() {
        super("hello");
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.add(new JLabel("Hello, world!"));
        super.pack();
        super.setVisible(true);
    }

    public static void main(final String[] args) {
        new Hello();
    }
}