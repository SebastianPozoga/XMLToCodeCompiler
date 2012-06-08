/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jspfmetadatacompiler;

import CSLCompiler.CSLCompiler;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author sebastian
 */
public class Main {

    /*public static void compilePath(File outFile, File inFile, File dataXMLFile) throws FileNotFoundException, IOException, Exception {
        File[] files = inFile.listFiles(transformationFileFilter);
        if (files == null) {
            return;
        }
        Transformer transformer = Factory.newTransformer(new StreamSource(new File(xsltPath)));
        for (File file : files) {
            if (file.isFile()) {
                System.out.print("Transform: " + file.getAbsolutePath() + " ... ");
                String fileName = file.getName().substring(0, file.getName().length() - 4) ;
                fileName = fileName.replace("_", dataXMLFile.getName().substring(0, dataXMLFile.getName().length() - 4));
                File newOut = new File(outFile, fileName);
                /*simpleTransform(
                        dataXMLFile.getPath(),
                        file.getPath(),
                        newOut.getPath());* /

                transformer.transform(new StreamSource(new File(sourcePath)), new StreamResult(new File(resultDir)));

                //Select formatter if available
                Formatter.FormatterInterface formatter = null;
                if(fileName.endsWith(".cpp") || fileName.endsWith(".c") || fileName.endsWith(".h")){
                    //Formatting for - c / c++
                    Formatter.FormatterInterface cFormatter = Formatter.getCFormatter();
                }else if(fileName.endsWith(".java")){
                    //Formatter for - java
                    Formatter.FormatterInterface jFormatter = Formatter.getCFormatter();
                }

                if(formatter==null){

                }

                FileReader r = new FileReader(newOut);
                FileWriter w = new FileWriter(newOut.getPath()+"Format.txt");


            }
        }
        File[] directories = inFile.listFiles(directoryFileFilter);
        for(File directory : directories){
            if (directory.getName().equals("..") && directory.getName().equals(".")) {
                continue;
            }
            File newOut = new File(outFile, directory.getName());
            newOut.mkdirs();
            compilePath(newOut,  directory, dataXMLFile);
        }
    }*/

    /*
     * Const
     */

    public static final String DATA_BASE_PATH = "./Metadata";
    public static final String TRANSFORMATION_BASE_PATH = "./Transforms";
    public static final String OUTPUT_DIRECTORY = "./out";


    /*
     * Variables
     */
    static TransformerFactory tFactory = TransformerFactory.newInstance();

    public static void main(String[] args) throws Throwable {

        //Compile CSL to XSL
        List<File> cslFiles = listFiles(new File(TRANSFORMATION_BASE_PATH), cslFileFilter, true);
        CSLCompiler compiler = new CSLCompiler();
        for(File file : cslFiles){
            //Out file
            String outPath = file.getPath();
            outPath = outPath.substring(0, outPath.length()-4)+".xsl";
            File outXSLFile = new File(outPath);
            //Compile
            FileReader cslReader = new FileReader(file);
            FileWriter xslWriter = new FileWriter(outXSLFile);
            System.out.print("Compile: "+file.getPath()+" to "+outXSLFile.getPath()+" ...");
            compiler.compile(cslReader, xslWriter);
            System.out.println("OK");
        }



        //Build file list for metadata
        List<File> dataFiles = listFiles(new File(DATA_BASE_PATH), dataFileFilter, true);
        List<File> transformFiles = listFiles(new File(TRANSFORMATION_BASE_PATH), transformationFileFilter, true);

        File f = new File(DATA_BASE_PATH);
        System.out.println((f.isDirectory()?"TAK":"NIE")+f.getPath());
        f = new File(TRANSFORMATION_BASE_PATH);
        System.out.println((f.isDirectory()?"TAK":"NIE")+f.getPath());

        Map dataFileCache = new SoftHashMap();
        Map transformFilesCache = new SoftHashMap();

        System.out.println("Start transform");
        System.out.println("please waiting...");
        System.out.println();


        for(File dataFile : dataFiles){
            Source dataSource = (Source) dataFileCache.get(dataFile);
            if(dataSource==null){
                dataSource = new StreamSource(dataFile);
                dataFileCache.put(dataFile, dataSource);
            }
            for(File transformFile : transformFiles){
                Transformer transformer = (Transformer) transformFilesCache.get(transformFile);
                if(transformer==null){
                    Source transformSource = new StreamSource(transformFile);
                    transformer = tFactory.newTransformer(transformSource);
                    transformFilesCache.put(transformFile, transformer);
                }
                
                //Math out path
                String outDirPath = OUTPUT_DIRECTORY+transformFile.getPath().substring( TRANSFORMATION_BASE_PATH.length(), transformFile.getPath().length()-transformFile.getName().length());
                String outFilename = transformFile.getName().substring(0,transformFile.getName().length()-4 );
                String elementName =  dataFile.getName().substring(0,dataFile.getName().length()-4);
                outDirPath = outDirPath.replace("_",elementName);
                outFilename= outFilename.replace("_",elementName);
                File outDir = new File(outDirPath);
                outDir.mkdirs();
                File outFile = new File(outDir, outFilename);

                //User massage
                System.out.print("Transform: "+dataFile.getPath()+" with "+transformFile.getPath()+" to "+outFile.getPath()+"...");

                //Treansform
                StringWriter writer = new StringWriter();
                transformer.transform(dataSource, new StreamResult(writer));
                String result = writer.toString();

                //Formatt code
                Formatter.FormatterInterface formatter = null;
                if(outFilename.endsWith(".cpp") || outFilename.endsWith(".c") || outFilename.endsWith(".h")){
                    //Formatting for - c / c++
                    formatter = Formatter.getCFormatter();
                }else if(outFilename.endsWith(".java")){
                    //Formatter for - java
                    formatter = Formatter.getCFormatter();
                }

                if(formatter!=null){
                    System.out.print(" Formatting... ");
                    StringReader reader = new StringReader(result);
                    result = formatter.format(reader);
                }

                //Save result
                outFile.createNewFile();
                Writer out = new FileWriter(outFile);
                out.write(result);
                out.close();

                //User massage: ended
                System.out.println(" end");
            }
        }

        System.out.println();
        System.out.println("Formated "+dataFiles.size()+" data files, used "+transformFiles.size()+" transform files. ");
        System.out.println();
        System.out.println("Metadata compilation complete.");
        System.out.println();
    }


    /*
     * Load file
     */
    private static String readFileAsString(File file) throws java.io.IOException{
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream f = new FileInputStream(file);
        f.read(buffer);
        return new String(buffer);
    }


    /*
     * create file list in directory (and subdirectories - if true)
     */
    public static List<File> listFiles(File basePath, FileFilter fileFilter, boolean recursive){
        List<File> list = new ArrayList<File>();
        list.addAll( Arrays.asList(basePath.listFiles(fileFilter)) );
        if(recursive==true){
            for(File dir : basePath.listFiles(directoryFileFilter)){
                _loadFilesToList(dir, fileFilter, list);
            }
        }
        return list;
    }

    protected static  void _loadFilesToList(File basePath, FileFilter fileFilter, List<File> list){
        list.addAll( Arrays.asList(basePath.listFiles(fileFilter)) );
        for(File dir : basePath.listFiles(directoryFileFilter)){
            _loadFilesToList(dir, fileFilter, list);
        }
    }


    /* Helpers - Filter  */
    static class XSLFileFilter implements FilenameFilter {

        public boolean accept(File file, String name) {
            return name.endsWith(".xsl");
        }
    }

    static class SimpleFileFilter implements FileFilter {
        String endWith;
        public SimpleFileFilter(String endWith) {
            this.endWith = endWith;
        }
        public boolean accept(File file) {
            if(!file.getName().endsWith(endWith))
                return false;
            if(!file.isFile())
                return false;
            return true;
        }
    }
    static SimpleFileFilter cslFileFilter = new SimpleFileFilter(".csl");
    static SimpleFileFilter dataFileFilter = new SimpleFileFilter(".xml");
    static SimpleFileFilter transformationFileFilter = new SimpleFileFilter(".xsl");


    static class DirectoryFileFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }
    static DirectoryFileFilter directoryFileFilter = new DirectoryFileFilter();
}
