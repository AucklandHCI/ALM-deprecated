package alm.examples;

import alm.ALMLayout;

import java.awt.*;

/**
 * This example shows the Auckland Layout Editor (ALE).
 * The example is based on the ThirteenWidgets layout and simply calls the edit() function on the layout manager to
 * switch the GUI into edit mode. When closing the Properties window, the GUI is switched back to operational mode.
 */
class LayoutEditorJava {
    public static void main(String[] args) {

        ThirteenWidgetsJava frame = new ThirteenWidgetsJava();
        frame.setVisible(true);

        Container container = frame.getContentPane();
        ALMLayout layout = (ALMLayout) container.getLayout();
        layout.edit(container);
    }
}
