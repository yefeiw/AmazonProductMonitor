package com.yefeiw;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;

import javax.management.Query;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yefeiw on 7/3/17.
 */
public class QueryHandler {
    private String filename;
    private BufferedReader buffers;
    public QueryHandler(String filename) {
        try {
            FileReader fileReader = new FileReader(filename);
            buffers = new BufferedReader(fileReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLine() {
        String line = "";
        try {
            while (line.trim().length() == 0) {
                line = buffers.readLine();
                if (line == null) {
                    System.out.println("EOL reached");
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line.trim();

    }

    public List<String> getLines() {
        List<String> ret = new ArrayList<String>(20);
        String line = "";
        try {
            while(line != null) {
                line = buffers.readLine();
                ret.add(line);
            }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return ret;
    }
    //ref = https://examples.javacodegeeks.com/core-java/apache/lucene/lucene-indexing-example-2/
    public String tokenizeString(String str) {
        Analyzer analyzer = new StandardAnalyzer();
        String ret = "";
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
            stream.reset();
            while (stream.incrementToken()) {
                ret += (stream.getAttribute(CharTermAttribute.class).toString());
                ret += " ";
            }
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
        }
        return ret;
    }

}
