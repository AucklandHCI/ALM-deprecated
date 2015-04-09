package linsolve.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {
	
	private Map<String, Entry> entries = new HashMap<String, Entry>();
	private List<String> entryNames = new ArrayList<String>();
	
	public <Type> void addEntryCategory(String name){
		entries.put(name, new Entry(name));
		entryNames.add(name);
	}
	
	
	public String[] getEntryHeader(){
		return entryNames.toArray(new String[entryNames.size()]);
	}
	
	public String[] toStringArray(){
		
		String[] rslt = new String[entries.size()];
		for(int i = 0; i < entries.size(); i++){
			try {
				rslt[i] = entries.get(entryNames.get(i)).getValue().toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rslt;
	}
	
	public void resetEntries(){
		for(String name : entryNames) entries.get(name).resetValue();
	}
	
	public void addEntry(String name, Object value) throws Exception{
		if(!entries.containsKey(name))
			throw new Exception("Attribute " + name + " is not part of this test! Check spelling and Entry configuration.");
		entries.get(name).setValue(value);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String name;
		for(int i = 0; i < entryNames.size() - 1; i++){
			name = entryNames.get(i);
			sb.append(name);
			sb.append(": ");
			try {
				sb.append(entries.get(name).getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			sb.append(", ");
		}
		name = entryNames.get(entryNames.size() - 1);
		sb.append(name);
		sb.append(": ");
		try {
			sb.append(entries.get(name).getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		return sb.toString();
	}
	
	
	public class Entry{
		private String name;
		private Object value;
		boolean isSet = false;
		
		public Entry(String name){
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public void setValue(Object value){
			isSet = true;
			this.value = value;
		}
		
		public Object getValue() throws Exception{
			if(!isSet) throw new Exception("No value set!");
			return value;
		}
		
		public void resetValue(){
			value = null;
			isSet = false;
		}
	}
}
