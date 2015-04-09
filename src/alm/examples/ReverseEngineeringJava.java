package alm.examples;

import alm.ALMLayout;

import javax.swing.*;
import java.awt.*;

class ReverseEngineeringJava extends JFrame {

    public ReverseEngineeringJava() {
        setTitle("ReverseEngineeringJava");
        // TODO change manual layout size so that reverse engineering connects the layout properly at the bottom and right
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button1 = new JButton();
        button1.setLocation(new Point(13, 9));
        button1.setName("button1");
        button1.setSize(new Dimension(82, 37));
        button1.setText("button1");
        add(button1);

        JButton button2 = new JButton();
        button2.setLocation(new Point(13, 52));
        button2.setName("button2");
        button2.setSize(new Dimension(82, 36));
        button2.setText("button2");
        add(button2);

        JList listBox1 = new JList(new String[]{"item1", "item2"});
        listBox1 = new JList(new String[]{"item1", "item2"});
        listBox1.setLocation(new Point(13, 94));
        listBox1.setName("listBox1");
        listBox1.setSize(new Dimension(82, 95));
        add(listBox1);

        JTextArea textBox1 = new JTextArea();
        textBox1.setLocation(new Point(101, 9));
        textBox1.setName("textBox1");
        textBox1.setSize(new Dimension(189, 180));
        textBox1.setText("hello");
        add(textBox1);

        ALMLayout layout = new ALMLayout();
        layout.recoverLayout(getContentPane());
        getContentPane().setLayout(layout);
    }

    public static void main(String[] args) {
        new ReverseEngineeringJava().setVisible(true);
    }
}
