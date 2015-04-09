package alm.examples;

import alm.*;

import javax.swing.*;

class RowsAndColumnsJava extends JFrame {

    public RowsAndColumnsJava() {
        setTitle("RowsAndColumnsJava");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ALMLayout layout = new ALMLayout();

        Column c1 = layout.addColumn(layout.getLeft(), layout.getRight());
        Row r1 = layout.addRow(layout.getTop(), null);
        Row r3 = layout.addRow(null, layout.getBottom());
        r1.setNext(r3);
        Row r2 = layout.addRow();
        r2.insertAfter(r1);

        JButton b1 = new JButton();
        b1.setText("A1");
        b1.setName("A1");
        Area a1 = layout.addArea(r1, c1, b1);
        a1.setHorizontalAlignment(HorizontalAlignment.LEFT);
        a1.setVerticalAlignment(VerticalAlignment.TOP);

        JButton b2 = new JButton();
        b2.setText("A2");
        b2.setName("A2");
        Area a2 = layout.addArea(r2, c1, b2);
        a2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        a2.setVerticalAlignment(VerticalAlignment.CENTER);

        JButton b3 = new JButton();
        b3.setText("A3");
        b3.setName("A3");
        Area a3 = layout.addArea(r3, c1, b3);
        a3.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        a3.setVerticalAlignment(VerticalAlignment.BOTTOM);

        r2.hasSameHeightAs(r1);
        r3.hasSameHeightAs(r1);

        getContentPane().setLayout(layout);
    }

    public static void main(String[] args) {
        new RowsAndColumnsJava().setVisible(true);
    }
}
