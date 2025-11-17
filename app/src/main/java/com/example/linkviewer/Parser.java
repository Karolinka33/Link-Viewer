package com.example.linkviewer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;

public class Parser {

    public static List<String> parse(String raw) throws Exception {
        raw = raw.trim();

        if (raw.startsWith("<")) {
            return parseXML(raw);
        } else {
            return parseJSON(raw);
        }
    }

    private static List<String> parseXML(String raw) throws Exception {
        List<String> results = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(raw.getBytes());
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList cubes = doc.getElementsByTagName("Cube");  //ECB
        for (int i = 0; i < cubes.getLength(); i++) {
            Element c = (Element) cubes.item(i);
            if (c.hasAttribute("currency") && c.hasAttribute("rate")) {
                results.add(c.getAttribute("currency") + " = " + c.getAttribute("rate"));
            }
        }


        NodeList items = doc.getElementsByTagName("item");   //FloatRates
        for (int i = 0; i < items.getLength(); i++) {
            Element it = (Element) items.item(i);

            String ccy = getText(it, "targetCurrency");
            String rate = getText(it, "exchangeRate");

            if (!ccy.isEmpty() && !rate.isEmpty()) {
                results.add(ccy + " = " + rate);
            }
        }

        return results;
    }
    private static String getText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent();
    }
    private static List<String> parseJSON(String raw) throws Exception {
        List<String> results = new ArrayList<>();

        JSONObject obj = new JSONObject(raw);
        JSONObject rates = obj.getJSONObject("rates");

        Iterator<String> keys = rates.keys();
        while (keys.hasNext()) {
            String ccy = keys.next();
            String rate = rates.get(ccy).toString();

            results.add(ccy + " = " + rate);
        }
        return results;
    }
}

