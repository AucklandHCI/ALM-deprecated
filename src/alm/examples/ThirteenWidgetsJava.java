package alm.examples;

import alm.*;
import linsolve.OperatorType;

import javax.swing.*;

class ThirteenWidgetsJava extends JFrame {

        public ThirteenWidgetsJava() {
                setTitle("ThirteenWidgetsJava");
                setSize(300, 300);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                ALMLayout layout = new ALMLayout();
                XTab x1 = layout.addXTab();
                XTab x2 = layout.addXTab();
                XTab x3 = layout.addXTab();
                YTab y1 = layout.addYTab();
                YTab y2 = layout.addYTab();
                YTab y3 = layout.addYTab();
                YTab y4 = layout.addYTab();
                YTab y5 = layout.addYTab();

                layout.addArea(layout.getLeft(), layout.getTop(), x1, y1, new JButton("button1"));
                layout.addArea(x1, layout.getTop(), x2, y1, new JButton("button2"));
                layout.addArea(layout.getLeft(), y1, x1, y2, new JButton("button3"));
                layout.addArea(x1, y1, x2, y2, new JButton("button4"));
                layout.addArea(layout.getLeft(), y2, x1, y3, new JButton("button5"));
                layout.addArea(x1, y2, x2, y3, new JButton("button6"));

                // give the buttons the same size
                layout.addConstraint(2, x1, -1, x2, OperatorType.EQ, 0);
                layout.addConstraint(2, y1, -1, y2, OperatorType.EQ, 0);
                layout.addConstraint(1, y1, 1, y2, -1, y3, OperatorType.EQ, 0);

                layout.addArea(layout.getLeft(), y3, x2, y4, new JList());
                layout.addArea(x2, layout.getTop(), layout.getRight(), y2, new JTextArea());
                layout.addArea(layout.getLeft(), y4, x3, y5, new JList());
                layout.addArea(x3, y2, layout.getRight(), y5, new JTextArea());

                Area richTextBox1Area = layout.addArea(x2, y2, x3, y4, new JTextArea());
                richTextBox1Area.setLeftInset(10);
                richTextBox1Area.setTopInset(10);
                richTextBox1Area.setRightInset(10);
                richTextBox1Area.setBottomInset(10);

                Area label1Area = layout.addArea(layout.getLeft(), y5, x2, layout.getBottom(), new JLabel("label1"));
                label1Area.setHorizontalAlignment(HorizontalAlignment.LEFT);
                label1Area.setTopInset(4);
                label1Area.setBottomInset(4);

                Area label2Area = layout.addArea(x2, y5, x3, layout.getBottom(), new JLabel("label2"));
                label2Area.setHorizontalAlignment(HorizontalAlignment.CENTER);
                label2Area.setTopInset(4);
                label2Area.setBottomInset(4);

                Area label3Area = layout.addArea(x3, y5, layout.getRight(), layout.getBottom(), new JLabel("label3"));
                label3Area.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                label3Area.setTopInset(4);
                label3Area.setBottomInset(4);

                getContentPane().setLayout(layout);
        }

        public static void main(String[] args) {
                new ThirteenWidgetsJava().setVisible(true);
        }
}
