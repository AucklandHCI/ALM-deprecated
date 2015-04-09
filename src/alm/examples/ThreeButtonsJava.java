package alm.examples;

import alm.*;

import javax.swing.*;

class ThreeButtonsJava extends JFrame {

    public ThreeButtonsJava() {
        setTitle("ThreeButtonsJava");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ALMLayout layout = new ALMLayout();
        XTab x1 = layout.addXTab("x1");
        YTab y1 = layout.addYTab("y1");
        layout.addArea(layout.getLeft(), layout.getTop(), x1, y1, new JButton("button1"));
        layout.addArea(x1, layout.getTop(), layout.getRight(), y1, new JButton("button2"));
        layout.addArea(layout.getLeft(), y1, layout.getRight(), layout.getBottom(), new JButton("button3"));

        getContentPane().setLayout(layout);
    }

    public static void main(String[] args) {
        new ThreeButtonsJava().setVisible(true);
    }
}
