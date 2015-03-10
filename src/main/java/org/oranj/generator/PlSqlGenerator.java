package org.oranj.generator;

import org.oranj.exceptions.InitConfigurationException;
import org.oranj.mappings.xml.XMLParsersHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by panasenko on 10.03.2015.
 */
public class PlSqlGenerator {

    private PlSqlGenerator() {
    }

    public void loadConfig(String configFileName) throws InitConfigurationException {

        Document document = XMLParsersHelper.parseXMLFile(null, configFileName);


    }

    /**
     * first argument - full path to code-generation properties config file name
     * @param args
     */
    public static void main(String[] args) throws InitConfigurationException {

        PlSqlGenerator plSqlGenerator = new PlSqlGenerator();
        plSqlGenerator.loadConfig(args[0]);

    }
}
