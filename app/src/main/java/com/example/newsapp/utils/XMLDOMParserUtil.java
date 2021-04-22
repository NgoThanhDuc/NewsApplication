package com.example.newsapp.utils;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class XMLDOMParserUtil {

    public Document getDocument(String xml, ListView listView, TextView textView, ShimmerLayout shimmer_view_contain, FrameLayout frameLayout_contain) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            is.setEncoding("UTF-8");
            document = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage(), e);
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            shimmer_view_contain.stopShimmerAnimation();
            shimmer_view_contain.setVisibility(View.GONE);
            frameLayout_contain.setVisibility(View.VISIBLE);
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage(), e);
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            shimmer_view_contain.stopShimmerAnimation();
            shimmer_view_contain.setVisibility(View.GONE);
            frameLayout_contain.setVisibility(View.VISIBLE);
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage(), e);
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

            shimmer_view_contain.stopShimmerAnimation();
            shimmer_view_contain.setVisibility(View.GONE);
            frameLayout_contain.setVisibility(View.VISIBLE);
            return null;
        }

        return document;
    }

    public Document getDocument(String xml) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            is.setEncoding("UTF-8");
            document = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        }

        return document;
    }

    public String getValue(Element item, String name) {
        NodeList nodes = item.getElementsByTagName(name);
        return this.getTextNodeValue(nodes.item(0));
    }

    private final String getTextNodeValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}

