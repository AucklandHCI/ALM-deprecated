package alm;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import alm.examples.old.*;
import commons.Utils;
import junit.framework.Assert;

import org.junit.Test;

public class ALMTests {
	@Test
	public void threeButtonsTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button1", new Integer[]{0, 0, 242, 231});
		expectedValues.put("button2", new Integer[]{242, 0, 242, 231});
		expectedValues.put("button3", new Integer[]{0, 231, 484, 231});

		Component [] components = ThreeButtonsOld.createAndShowGUI("ALM-ThreeButtons", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("ThreeButtonsApp", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void twoButtonsAndOnePanelTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("Child Button", new Integer[]{271, 5, 102, 26});
		expectedValues.put("Child Label", new Integer[]{111, 10, 62, 16});
		expectedValues.put("Child Text Field", new Integer[]{178, 8, 88, 20});
		expectedValues.put("button1", new Integer[]{0, 0, 242, 231});
		expectedValues.put("button2", new Integer[]{242, 0, 242, 231});
		expectedValues.put("panel1", new Integer[]{0, 231, 484, 231});

		Component [] components = TwoButtonsAndOnePanel.createAndShowGUI("ALM-TwoButtonsAndOnePanel", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("TwoButtonsAndOnePanel", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void thirteenWidgetsTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button1", new Integer[]{0, 0, 77, 26});
		expectedValues.put("button2", new Integer[]{77, 0, 77, 26});
		expectedValues.put("button3", new Integer[]{0, 26, 77, 26});
		expectedValues.put("button4", new Integer[]{77, 26, 77, 26});
		expectedValues.put("button5", new Integer[]{0, 52, 77, 26});
		expectedValues.put("button6", new Integer[]{77, 52, 77, 26});
		expectedValues.put("checkedListBox1", new Integer[]{0, 78, 154, 337});
		expectedValues.put("label1", new Integer[]{0, 442, 34, 16});
		expectedValues.put("label2", new Integer[]{279, 442, 34, 16});
		expectedValues.put("label3", new Integer[]{450, 442, 34, 16});
		expectedValues.put("listView1", new Integer[]{0, 415, 439, 23});
		expectedValues.put("richTextBox1", new Integer[]{164, 62, 265, 343});
		expectedValues.put("textBox1", new Integer[]{154, 0, 330, 52});
		expectedValues.put("textBox2", new Integer[]{439, 52, 45, 386});

		Component [] components = ThirteenWidgetsOld.createAndShowGUI("ALM-ThirteenWidgets", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("ThirteenWidgets", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void tableTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("A1", new Integer[]{0, 0, 49, 26});
		expectedValues.put("A2", new Integer[]{218, 218, 49, 26});
		expectedValues.put("A3", new Integer[]{435, 436, 49, 26});

		Component [] components = TableTest.createAndShowGUI("ALM-TableTest", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("TableTest", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void reverseEngineeringTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button1", new Integer[]{13, 9, 82, 37});
		expectedValues.put("button2", new Integer[]{13, 52, 82, 36});
		expectedValues.put("listBox1", new Integer[]{13, 94, 82, 95});
		expectedValues.put("textBox1", new Integer[]{101, 9, 179, 180});

		Component [] components = ReverseEngineering.createAndShowGUI("ALM-ReverseEngineering", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("ReverseEngineering", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void jTableExampleTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("tableScrollPane", new Integer[]{15, 29, 453, 403});

		Component [] components = JTableExampleOld.createAndShowGUI("ALM-JTableExample", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("JTableExample", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void testEdit1Test() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button1", new Integer[]{0, 0, 77, 26});
		expectedValues.put("button2", new Integer[]{77, 0, 77, 26});
		expectedValues.put("button3", new Integer[]{0, 26, 77, 26});
		expectedValues.put("button4", new Integer[]{77, 26, 77, 26});
		expectedValues.put("button5", new Integer[]{0, 52, 77, 26});
		expectedValues.put("button6", new Integer[]{77, 52, 77, 26});
		expectedValues.put("checkedListBox1", new Integer[]{0, 78, 154, 26});
		expectedValues.put("label1", new Integer[]{0, 134, 34, 324});
		expectedValues.put("label2", new Integer[]{202, 134, 34, 324});
		expectedValues.put("label3", new Integer[]{450, 134, 34, 324});
		expectedValues.put("listView1", new Integer[]{0, 104, 284, 26});
		expectedValues.put("richTextBox1", new Integer[]{164, 62, 110, 32});
		expectedValues.put("textBox1", new Integer[]{154, 0, 330, 52});
		expectedValues.put("textBox2", new Integer[]{284, 52, 200, 78});

		Component [] components = TestEdit1.createAndShowGUI("AIM-TestEdit1Test", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("TestEdit1Test", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void editorSampleTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button1", new Integer[]{0, 0, 150, 120});
		expectedValues.put("button2", new Integer[]{150, 0, 334, 120});
		expectedValues.put("button3", new Integer[]{150, 120, 200, 72});
		expectedValues.put("button4", new Integer[]{350, 120, 134, 72});
		expectedValues.put("button5", new Integer[]{150, 192, 100, 48});
		expectedValues.put("button6", new Integer[]{250, 192, 234, 48});
		expectedValues.put("button7", new Integer[]{150, 240, 234, 222});
		expectedValues.put("button8", new Integer[]{384, 240, 100, 222});
		expectedValues.put("textbox1", new Integer[]{0, 120, 150, 342});

		Component [] components = EditorSample.createAndShowGUI("ALMGridBagLayout-EditorSampleTest", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("EditorSampleTest", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void pinWheelTest() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("Button 1", new Integer[]{0, 0, 323, 154});
		expectedValues.put("Button 2", new Integer[]{323, 0, 161, 308});
		expectedValues.put("Button 3", new Integer[]{161, 308, 323, 154});
		expectedValues.put("Button 4", new Integer[]{0, 154, 161, 308});
		expectedValues.put("Text 1", new Integer[]{161, 154, 161, 154});

		Component [] components = PinWheel.createAndShowGUI("ALMGridBagLayout-PinWheelTest", new Dimension(500, 500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("PinWheelTest", components);
		}

		checkResults(expectedValues, components);
	}
		
	private void checkResults(Map<String, Integer[]> expectedValues, Component[] components) {
		for (int i=0; i < components.length; i++) {
			String compName = components[i].getName();
			Integer [] expected = expectedValues.get(compName);
			Assert.assertEquals(compName+"(X)", components[i].getX(), (int)Integer.valueOf(expected[0]), commons.TestConfigParams.epsilon);
			Assert.assertEquals(compName+"(Y)", components[i].getY(), (int)Integer.valueOf(expected[1]), commons.TestConfigParams.epsilon);
			Assert.assertEquals(compName+"(W)", components[i].getWidth(), (int)Integer.valueOf(expected[2]), commons.TestConfigParams.epsilon);
			Assert.assertEquals(compName+"(H)", components[i].getHeight(), (int)Integer.valueOf(expected[3]), commons.TestConfigParams.epsilon);
		}		
	}

}