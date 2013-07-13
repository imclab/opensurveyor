package devedroid.opensurveyor.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import devedroid.opensurveyor.Utils;
import devedroid.opensurveyor.XMLPresetLoader;

public class PropertyDefinition {
	public final String title;

	public final String key;
	
	public enum Type {
		String, Boolean, Choice
	}
	
	public final Type type;
	
	public static class ChoiceEntry {
		public final String title;
		public final String value;
		public ChoiceEntry(String title, String value) {
			this.title = title;
			this.value = value;
		}
		public String toString() {return title;}
	}
	
	public final List<ChoiceEntry> choices;
	
	public PropertyDefinition(String title, String key, Type type) {
		super();
		this.title = title;
		this.key = key;
		this.type = type;
		if(type == Type.Choice) {
			choices = new ArrayList<ChoiceEntry>();
		} else choices = null;
	}
	
	private void addChoice(String title, String val) {
		choices.add( new ChoiceEntry(title, val));
	}
	
	public static PropertyDefinition stringProperty(String title, String key) {
		return new PropertyDefinition(title, key, Type.String);
	}
	
	public static PropertyDefinition readFromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "property");
		String title = parser.getAttributeValue(null, "name");
		String key = parser.getAttributeValue(null, "k");
		String cType = parser.getAttributeValue(null, "type");
		Type type = Type.String;
		if("text".equals(cType)) type = Type.String; else
		if("boolean".equals(cType)) type = Type.Boolean; else
		if("choice".equals(cType)) type = Type.Choice;else
			Utils.logd("PropertyDefinition.readFromXML", "Unknown type "+cType+"; assuming String");
		//XMLPresetLoader.skip(parser);
		PropertyDefinition res = new PropertyDefinition(title, key, type);
		if(type==Type.Choice) readChoices(parser, res); else XMLPresetLoader.skip(parser);
		
		return res;
	}
	
	private static void readChoices(XmlPullParser parser, PropertyDefinition res) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "property");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("variant")) {
				res.addChoice( 
						parser.getAttributeValue(null, "name"), 
						parser.getAttributeValue(null, "v") );
				parser.next();
				parser.require(XmlPullParser.END_TAG, null, "variant");
			} else {
				XMLPresetLoader.skip(parser);
			}
		}
		parser.require(XmlPullParser.END_TAG, null, "property");

	}
}