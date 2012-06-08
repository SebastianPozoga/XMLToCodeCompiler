/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jspfmetadatacompiler;

import java.io.Reader;
import java.io.Writer;
import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

/**
 *
 * @author sebastian
 */
public class Formatter {


    static Formatter.JavaFormatter javaFormatter;
    public static FormatterInterface getJavaFormatter(){
        if(javaFormatter==null){
            javaFormatter = new JavaFormatter();
        }
        return javaFormatter;
    }


    static Formatter.CFormatter cFormatter;
    public static FormatterInterface getCFormatter(){
        if(cFormatter==null){
            cFormatter = new CFormatter();
        }
        return cFormatter;
    }


    /*
     * Formater interface
     */
    public interface FormatterInterface {
        String format(Reader reader) throws Throwable;
        void format(Reader reader, Writer writer) throws Throwable;
    }



    public static class JavaFormatter implements FormatterInterface {
        ASFormatter formatter;
        public JavaFormatter() {
            formatter = new ASFormatter();
            formatter.setJavaStyle();
        }
        public String format(Reader reader) throws Throwable {
            return FormatterHelper.format(reader, formatter);
        }
        public void format(Reader reader, Writer writer) throws Throwable {
            writer.write( format(reader) );
        }
    }

    public static class CFormatter implements FormatterInterface {
        ASFormatter formatter;
        public CFormatter() {
            formatter = new ASFormatter();
            formatter.setCStyle();
        }
        public String format(Reader reader) throws Throwable {
            return FormatterHelper.format(reader, formatter);
        }
        public void format(Reader reader, Writer writer) throws Throwable {
            writer.write( format(reader) );
        }
    }
}
