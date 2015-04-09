package compatibility.GridBagLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import commons.Utils;
import junit.framework.Assert;

import org.junit.Test;

public class GridBagLayoutTests {

	@Test
	public void test1() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{121, 167, 81, 26});
		expectedValues.put("button-2", new Integer[]{202, 167, 81, 26});
		expectedValues.put("button-3", new Integer[]{283, 167, 81, 26});
		expectedValues.put("button-4", new Integer[]{121, 193, 243, 66});
		expectedValues.put("button-5", new Integer[]{202, 269, 162, 26});
		
		Test1App app = new Test1App();
		
		Component [] components = app.createAndShowGUI("ALMGridBagLayout-Test1", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);
		
		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test1", components);
		}
		
		checkResults(expectedValues, components);
	}

	@Test
	public void test2() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("actionlabel-1", new Integer[]{10, 106, 230, 26});
		expectedValues.put("controlpanel-1", new Integer[]{0, 0, 250, 145});
		expectedValues.put("formatted-text-field-1", new Integer[]{126, 66, 114, 20});
		expectedValues.put("panel-1", new Integer[]{212, 0, 272, 462});
		expectedValues.put("panel-2", new Integer[]{0, 0, 250, 462});
		expectedValues.put("scrollpane-1", new Integer[]{0, 145, 250, 317});
		expectedValues.put("splitpane-1", new Integer[]{10, 23, 252, 429});
		expectedValues.put("tempText1", new Integer[]{126, 86, 114, 20});
		expectedValues.put("textfield-1", new Integer[]{126, 26, 114, 20});
		expectedValues.put("textfield-2", new Integer[]{126, 46, 114, 20});
		expectedValues.put("textfield-3", new Integer[]{62, 28, 64, 16});
		expectedValues.put("textfield-4", new Integer[]{31, 48, 95, 16});
		expectedValues.put("textfield-5", new Integer[]{10, 68, 116, 16});
		expectedValues.put("textfield-6", new Integer[]{32, 88, 94, 16});

		Component [] components = Test2App.createAndShowGUI("ALMGridBagLayout-Test2", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test2", components);
		}

		checkResults(expectedValues, components);
	}




	@Test
	public void test3() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{92, 231, 105, 26});
		expectedValues.put("button-2", new Integer[]{197, 231, 195, 26});
		expectedValues.put("button-3", new Integer[]{92, 205, 300, 26});
		expectedValues.put("panel-1", new Integer[]{92, 257, 300, 75});
		expectedValues.put("scrollpane-1", new Integer[]{92, 130, 300, 75});

		Component [] components = Test3App.createAndShowGUI("ALMGridBagLayout-Test3", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test3", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test4() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{165, 205, 77, 26});
		expectedValues.put("button-2", new Integer[]{242, 205, 77, 26});
		expectedValues.put("button-3", new Integer[]{165, 231, 77, 26});
		expectedValues.put("button-4", new Integer[]{242, 231, 77, 26});
		
		Component [] components = Test4App.createAndShowGUI("ALMGridBagLayout-Test4", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);
		
		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test4", components);
		}
		
		checkResults(expectedValues, components);
	}
	
	@Test
	public void test5() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{33, 153, 88, 52});
		expectedValues.put("button-10", new Integer[]{121, 257, 331, 26});
		expectedValues.put("button-11", new Integer[]{33, 283, 88, 26});
		expectedValues.put("button-12", new Integer[]{121, 283, 88, 26});
		expectedValues.put("button-2", new Integer[]{121, 153, 88, 78});
		expectedValues.put("button-3", new Integer[]{209, 153, 81, 52});
		expectedValues.put("button-4", new Integer[]{290, 153, 162, 26});
		expectedValues.put("button-5", new Integer[]{290, 179, 81, 26});
		expectedValues.put("button-6", new Integer[]{371, 179, 81, 26});
		expectedValues.put("button-7", new Integer[]{209, 205, 243, 26});
		expectedValues.put("button-8", new Integer[]{33, 231, 88, 52});
		expectedValues.put("button-9", new Integer[]{121, 231, 331, 26});

		Component [] components = new Test5App().createAndShowGUI("ALMGridBagLayout-Test5", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test5", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test6() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{117, 166, 169, 26});
		expectedValues.put("button-10", new Integer[]{198, 270, 88, 26});
		expectedValues.put("button-2", new Integer[]{286, 166, 81, 26});
		expectedValues.put("button-3", new Integer[]{117, 192, 81, 26});
		expectedValues.put("button-4", new Integer[]{198, 192, 88, 26});
		expectedValues.put("button-5", new Integer[]{286, 192, 81, 26});
		expectedValues.put("button-6", new Integer[]{117, 218, 81, 52});
		expectedValues.put("button-7", new Integer[]{117, 270, 81, 26});
		expectedValues.put("button-8", new Integer[]{198, 218, 88, 26});
		expectedValues.put("button-9", new Integer[]{198, 244, 88, 26});

		Component [] components = Test6App.createAndShowGUI("ALMGridBagLayout-Test6", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test6", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test1_Wx_noWy_F() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{0, 167, 141, 26});
		expectedValues.put("button-2", new Integer[]{141, 167, 81, 26});
		expectedValues.put("button-3", new Integer[]{222, 167, 262, 26});
		expectedValues.put("button-4", new Integer[]{0, 193, 484, 66});
		expectedValues.put("button-5", new Integer[]{141, 269, 343, 26});

		Test1App app = new Test1App();
		app.setOptions(true, false, true);
		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test1_Wx_noWy_F", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test1_Wx_noWy_F", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test1_noWx_Wy_F() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{121, 0, 81, 93});
		expectedValues.put("button-2", new Integer[]{202, 0, 81, 93});
		expectedValues.put("button-3", new Integer[]{283, 0, 81, 93});
		expectedValues.put("button-4", new Integer[]{121, 93, 243, 133});
		expectedValues.put("button-5", new Integer[]{202, 236, 162, 226});
		
		Test1App app = new Test1App();
		app.setOptions(false, true, true);

		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test1_noWx_Wy_F", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test1_noWx_Wy_F", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void test1_Wx_Wy_F() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{0, 0, 141, 93});
		expectedValues.put("button-2", new Integer[]{141, 0, 81, 93});
		expectedValues.put("button-3", new Integer[]{222, 0, 262, 93});
		expectedValues.put("button-4", new Integer[]{0, 93, 484, 133});
		expectedValues.put("button-5", new Integer[]{141, 236, 343, 226});

		Test1App app = new Test1App();
		app.setOptions(true, true, true);

		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test1_Wx_Wy_F", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test1_Wx_Wy_F", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test1_Wx_Wy_noF() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{30, 33, 81, 26});
		expectedValues.put("button-2", new Integer[]{141, 33, 81, 26});
		expectedValues.put("button-3", new Integer[]{313, 33, 81, 26});
		expectedValues.put("button-4", new Integer[]{164, 126, 156, 66});
		expectedValues.put("button-5", new Integer[]{292, 436, 41, 26});

		Test1App app = new Test1App();
		app.setOptions(true, true, false);

		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test1_Wx_Wy_noF", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test1_Wx_Wy_noF", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test5_W_F() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{0, 0, 104, 170});
		expectedValues.put("button-10", new Integer[]{104, 339, 380, 85});
		expectedValues.put("button-11", new Integer[]{0, 424, 104, 38});
		expectedValues.put("button-12", new Integer[]{104, 424, 104, 38});
		expectedValues.put("button-2", new Integer[]{104, 0, 104, 255});
		expectedValues.put("button-3", new Integer[]{209, 0, 97, 170});
		expectedValues.put("button-4", new Integer[]{306, 0, 178, 85});
		expectedValues.put("button-5", new Integer[]{306, 85, 83, 85});
		expectedValues.put("button-6", new Integer[]{388, 85, 96, 85});
		expectedValues.put("button-7", new Integer[]{209, 170, 275, 85});
		expectedValues.put("button-8", new Integer[]{0, 255, 104, 170});
		expectedValues.put("button-9", new Integer[]{104, 255, 380, 85});

		Test5App app = new Test5App();
		app.setOptions(true, true);
		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test5_W_F", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test5_W_F", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test5_W_noF() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{12, 72, 81, 26});
		expectedValues.put("button-10", new Integer[]{250, 369, 88, 26});
		expectedValues.put("button-11", new Integer[]{8, 430, 88, 26});
		expectedValues.put("button-12", new Integer[]{112, 430, 88, 26});
		expectedValues.put("button-2", new Integer[]{116, 114, 81, 26});
		expectedValues.put("button-3", new Integer[]{217, 72, 81, 26});
		expectedValues.put("button-4", new Integer[]{354, 29, 81, 26});
		expectedValues.put("button-5", new Integer[]{307, 114, 81, 26});
		expectedValues.put("button-6", new Integer[]{396, 114, 81, 26});
		expectedValues.put("button-7", new Integer[]{306, 199, 81, 26});
		expectedValues.put("button-8", new Integer[]{12, 326, 81, 26});
		expectedValues.put("button-9", new Integer[]{254, 284, 81, 26});

		Test5App app = new Test5App();
		app.setOptions(true, false);
		Component [] components = app.createAndShowGUI("ALMGridBagLayout-test5_W_noF", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("test5_W_noF", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void testBorders() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{18, 23, 81, 26});

		Component [] components = new TestBorders().createAndShowGUI("ALMGridBagLayout-testBorders", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("testBorders", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test7() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("Five", new Integer[]{134, 436, 56, 26});
		expectedValues.put("Four", new Integer[]{0, 299, 134, 163});
		expectedValues.put("One", new Integer[]{39, 137, 57, 26});
		expectedValues.put("Six", new Integer[]{267, 299, 217, 163});
		expectedValues.put("Three", new Integer[]{267, 0, 217, 26});
		expectedValues.put("Two", new Integer[]{209, 0, 58, 299});

		Component [] components = Test7App.createAndShowGUI("ALMGridBagLayout-Test7", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test7", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test8() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("Annual Contribution", new Integer[]{210, 2, 114, 16});
		expectedValues.put("Compute", new Integer[]{448, 40, 111, 26});
		expectedValues.put("Current Age", new Integer[]{0, 22, 96, 16});
		expectedValues.put("Current Savings", new Integer[]{0, 2, 96, 16});
		expectedValues.put("Help", new Integer[]{560, 40, 125, 26});
		expectedValues.put("Life Expectancy", new Integer[]{448, 22, 111, 16});
		expectedValues.put("Percent Inflation", new Integer[]{0, 45, 96, 16});
		expectedValues.put("Prct Invest Return", new Integer[]{210, 45, 114, 16});
		expectedValues.put("Retirement Age", new Integer[]{210, 22, 114, 16});
		expectedValues.put("Retirement Income", new Integer[]{448, 2, 111, 16});
		expectedValues.put("contribField", new Integer[]{324, 0, 124, 20});
		expectedValues.put("currentAgeField", new Integer[]{96, 20, 114, 20});
		expectedValues.put("deathAgeField", new Integer[]{560, 20, 124, 20});
		expectedValues.put("incomeField", new Integer[]{560, 0, 124, 20});
		expectedValues.put("inflationPercentField", new Integer[]{96, 43, 114, 20});
		expectedValues.put("investPercentField", new Integer[]{324, 43, 124, 20});
		expectedValues.put("retireAgeField", new Integer[]{324, 20, 124, 20});
		expectedValues.put("retireCanvas", new Integer[]{0, 66, 449, 396});
		expectedValues.put("retirePane", new Integer[]{448, 66, 235, 396});
		expectedValues.put("savingsField", new Integer[]{96, 0, 114, 20});

		Component [] components = Test8App.createAndShowGUI("ALMGridBagLayout-Test8", new alm.compatibility.GridBagLayout(), new Dimension(700,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test8", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test9() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("(0,0)", new Integer[]{5, 5, 212, 126});
		expectedValues.put("(0,1)", new Integer[]{25, 173, 59, 26});
		expectedValues.put("(0,2)", new Integer[]{25, 326, 59, 26});
		expectedValues.put("(0,3)", new Integer[]{25, 436, 59, 26});
		expectedValues.put("(1,1)", new Integer[]{133, 173, 59, 26});
		expectedValues.put("(1,2)", new Integer[]{116, 246, 360, 215});
		expectedValues.put("(2,1)", new Integer[]{217, 131, 138, 110});
		expectedValues.put("(3,0)", new Integer[]{415, 10, 59, 26});

		Component [] components = new Test9App().createAndShowGUI("ALMGridBagLayout-Test9", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test9", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void test10() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("Address", new Integer[]{75, 182, 51, 16});
		expectedValues.put("Name", new Integer[]{90, 30, 36, 16});
		expectedValues.put("Phone", new Integer[]{87, 106, 39, 16});
		expectedValues.put("address-field", new Integer[]{136, 180, 224, 20});
		expectedValues.put("buttonBox", new Integer[]{322, 385, 137, 26});
		expectedValues.put("name-field", new Integer[]{136, 28, 224, 20});
		expectedValues.put("phone-field", new Integer[]{136, 104, 114, 20});
		expectedValues.put("size-box", new Integer[]{26, 234, 80, 95});
		expectedValues.put("style-box", new Integer[]{181, 234, 66, 71});
		expectedValues.put("topbox", new Integer[]{339, 234, 103, 95});

		Component [] components = Test10App.createAndShowGUI("ALMGridBagLayout-Test10", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test10", components);
		}

		checkResults(expectedValues, components);
	}
	
	@Test
	public void test11() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("An Att", new Integer[]{39, 102, 34, 16});
		expectedValues.put("An Attribute", new Integer[]{6, 54, 67, 16});
		expectedValues.put("Another Att", new Integer[]{9, 126, 64, 16});
		expectedValues.put("Another Att2", new Integer[]{2, 181, 71, 16});
		expectedValues.put("Date Field", new Integer[]{18, 78, 55, 16});
		expectedValues.put("My Things", new Integer[]{0, 0, 57, 16});
		expectedValues.put("Thing Name", new Integer[]{6, 2, 67, 16});
		expectedValues.put("a", new Integer[]{77, 2, 211, 20});
		expectedValues.put("anotherAttField", new Integer[]{77, 126, 211, 51});
		expectedValues.put("b", new Integer[]{77, 54, 211, 20});
		expectedValues.put("buttonPanel", new Integer[]{73, 475, 598, 36});
		expectedValues.put("c", new Integer[]{77, 78, 211, 20});
		expectedValues.put("checkbox1", new Integer[]{77, 26, 211, 24});
		expectedValues.put("checkbox2", new Integer[]{195, 181, 93, 24});
		expectedValues.put("d", new Integer[]{77, 102, 211, 20});
		expectedValues.put("e", new Integer[]{77, 181, 114, 20});
		expectedValues.put("panel", new Integer[]{453, 16, 290, 207});
		expectedValues.put("tableButtonPanel", new Integer[]{67, 439, 319, 36});
		expectedValues.put("tableScrollPane", new Integer[]{0, 16, 453, 423});

		Component [] components = Test11App.createAndShowGUI("ALMGridBagLayout-Test11", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test11", components);
		}

		checkResults(expectedValues, components);
	}


	@Test
	public void test9Squeeze() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("(0,0)", new Integer[]{5, 5, 112, 25});
		expectedValues.put("(0,1)", new Integer[]{0, 31, 59, 10});
		expectedValues.put("(0,2)", new Integer[]{0, 41, 59, 10});
		expectedValues.put("(0,3)", new Integer[]{0, 52, 59, 11});
		expectedValues.put("(1,1)", new Integer[]{59, 31, 58, 10});
		expectedValues.put("(1,2)", new Integer[]{65, 46, 211, 15});
		expectedValues.put("(2,1)", new Integer[]{117, 31, 88, 11});
		expectedValues.put("(3,0)", new Integer[]{216, 10, 59, 10});

		Component [] components = new Test9App().createAndShowGUI("ALMGridBagLayout-Test9Squeeze", new alm.compatibility.GridBagLayout(), new Dimension(300,100), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test9Squeeze", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void test12() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("1", new Integer[]{274, 227, 88, 26});
		expectedValues.put("2", new Integer[]{0, 218, 41, 26});
		expectedValues.put("4", new Integer[]{41, 218, 41, 26});
		expectedValues.put("5", new Integer[]{122, 237, 41, 26});
		expectedValues.put("6", new Integer[]{213, 218, 41, 26});
		expectedValues.put("7", new Integer[]{402, 218, 41, 26});
		expectedValues.put("8", new Integer[]{443, 218, 41, 26});

		Component [] components = Test12App.createAndShowGUI("ALMGridBagLayout-Test12", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("Test12", components);
		}

		checkResults(expectedValues, components);
	}

	@Test
	public void ALM27Test() {
		Map<String, Integer[]> expectedValues = new HashMap<String, Integer[]>();
		expectedValues.put("button-1", new Integer[]{0, 163, 81, 26});
		expectedValues.put("button-2", new Integer[]{81, 163, 81, 26});
		expectedValues.put("button-3", new Integer[]{162, 162, 322, 26});
		expectedValues.put("button-4", new Integer[]{0, 188, 484, 26});
		expectedValues.put("button-5", new Integer[]{0, 224, 484, 75});

		Component [] components = new ALM27TestApp().createAndShowGUI("ALMGridBagLayout-ALM27Test", new alm.compatibility.GridBagLayout(), new Dimension(500,500), false);

		if (commons.TestConfigParams.debug) {
			Utils.printComponents("ALM27Test", components);
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