package application.config;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public abstract class ConfigHandler {

  private static final char SEPARATOR = '.';
  private static final HashMap < String, ConfigItem > properties = new HashMap < > ();

  static {
    try {
      ConfigHandler.overwriteConfig("defaultConfig.xml");
    } catch (ParserConfigurationException | IOException | SAXException | XMLParseException e) {
      e.printStackTrace();
    }
  }

  private ConfigHandler() throws IllegalStateException {
    throw new IllegalStateException();
  }

  public static String combineStringName(String string1, String string2) {
    return string1.isBlank() ? string2 : string1 + SEPARATOR + string2;
  }

  public static void overwriteConfig(String fileName) throws ParserConfigurationException, IOException, SAXException, XMLParseException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document doc = documentBuilder.parse(ConfigHandler.class.getResourceAsStream(fileName));
    doc.getDocumentElement().normalize();
    setPropertiesTree(doc.getElementsByTagName("config").item(0).getChildNodes(), "");
  }

  public static void setPropertiesTree(NodeList nodeList, String treeName) throws XMLParseException {
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Node node = nodeList.item(i);
      if (node.getNodeName().equals("#text"))
        continue;
      if (node.hasChildNodes())
        setPropertiesTree(node.getChildNodes(), combineStringName(treeName, node.getNodeName()));
      else {
        final NamedNodeMap namedNodeMap = node.getAttributes();
        final String key = namedNodeMap.getNamedItem("key").getNodeValue();
        final String value = namedNodeMap.getNamedItem("value").getNodeValue();
        final String type = namedNodeMap.getNamedItem("type").getNodeValue();

        if (key == null || value == null || type == null)
          throw new XMLParseException("The given XML is wrong build");

        set(combineStringName(treeName, key), value, type);
      }
    }
  }

  public static record ConfigItem(String key, String value, String type) {
    public ConfigItem {
      switch (type.toUpperCase()) {
      case "INTEGER", "INT" -> Integer.parseInt(value);
      case "DOUBLE" -> Double.parseDouble(value);
      case "LONG" -> Long.parseLong(value);
      case "STRING" -> {}
      default ->
      throw new IllegalStateException("Unexpected value: " + type.toUpperCase());
      }
    }

    public boolean isDouble() {
      return type().equalsIgnoreCase("DOUBLE");
    }

    public boolean isLong() {
      return type().equalsIgnoreCase("LONG");
    }

    public boolean isInt() {
      return type().equalsIgnoreCase("INTEGER") || type().equalsIgnoreCase("INT");
    }
  }

  public static void set(String key, String value, String type) {
    key = key.toUpperCase();
    getProperties().put(key, new ConfigItem(key, value, type));
  }

  private static ConfigItem get(String key) {
    return getProperties().get(key.toUpperCase());
  }

  public static String getString(String key) {
    return get(key).value();
  }

  public static int getInt(String key) throws ConfigFormatException {
    final ConfigItem item = get(key);

    if (!item.isInt())
      throw new ConfigFormatException();

    return Integer.parseInt(item.value());
  }

  public static long getLong(String key) throws ConfigFormatException {
    final ConfigItem item = get(key);

    if (!item.isLong())
      throw new ConfigFormatException();

    return Long.parseLong(item.value());
  }

  public static double getDouble(String key) throws ConfigFormatException {
    final ConfigItem item = get(key);

    if (!item.isDouble())
      throw new ConfigFormatException();

    return Double.parseDouble(item.value());
  }

  public static Collection < ConfigItem > getConfigItems() {
    return getProperties().values();
  }

  private static HashMap < String, ConfigItem > getProperties() {
    return properties;
  }

  public static class ConfigFormatException extends RuntimeException {}
}