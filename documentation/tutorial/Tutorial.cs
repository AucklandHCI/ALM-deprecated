using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Windows.Forms.Layout;
using LinearProgramming;
using ALM;

namespace Tutorial
{
    public class Tutorial : Form
    {
        private ALMLayout alm = new ALMLayout();

        public override LayoutEngine LayoutEngine
        {
            get { return alm; }
        }

        public Tutorial()
        {
            ClientSize = new Size(292, 273);
            Text = "Tutorial";

            Button button = new Button();
            button.Text = "Hello, World!";

            XTab x1 = alm.AddXTab();
            XTab x2 = alm.AddXTab();
            YTab y1 = alm.AddYTab();
            YTab y2 = alm.AddYTab();

            // Create an area for the button in the layout
            alm.AddArea(x1, y1, x2, y2, button);

            // Constrain the button to be at least 150x150
            alm.AddConstraint(1, x2, -1, x1, OperatorType.GE, 150);
            alm.AddConstraint(1, y2, -1, y1, OperatorType.GE, 150);

            // Constrain the padding to be the same size on all edges
            alm.AddConstraint(1, x1, -1, alm.Left, -1, alm.Right, 1, x2, OperatorType.EQ, 0);
            alm.AddConstraint(1, x1, -1, alm.Left, -1, y1, 1, alm.Top, OperatorType.EQ, 0);
            alm.AddConstraint(1, x1, -1, alm.Left, -1, alm.Bottom, 1, y2, OperatorType.EQ, 0);
        }
    }
}
