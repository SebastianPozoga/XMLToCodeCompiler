/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSLCompiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebastian
 */
public class CSLCompiler_oldVersion {

    int lineNumber = 0;

    public void compile(Reader cslCodeReader, Writer xslCodeWriter) throws IOException, Exception {
        //Init variable
        lineNumber = 1;
        //Write begin xsl template
        printText("XSLTemplate.begin", xslCodeWriter);
        //Parse input
        int r;
        while ((r = cslCodeReader.read()) != -1) {
            char ch = (char) r;
            if (ch == '\n') {
                lineNumber++;
            }
            if (ch == '<') {
                int r2 = cslCodeReader.read();
                if (r2 == -1) {
                    xslCodeWriter.write(ch);
                    break;
                }
                char blockType = (char) r2;
                if (blockType == '$') {
                    //Print Or set  variable
                    String body = readTagBody(cslCodeReader);
                    parseVariableTagBody(body, xslCodeWriter);
                } else if (blockType == '@') {
                    //Print  attribute
                    String body = readTagBody(cslCodeReader);
                    parseAttributeTagBody(body, xslCodeWriter);
                } else if (blockType == '.') {
                    //Print  current node (simple tag - can contain only whitespace)
                    endSimpleTag(cslCodeReader);
                    xslCodeWriter.write("<xsl:value-of select=\".\"/>");
                } else if (blockType == ';') {
                    //Print endline (simple tag - can contain only whitespace)
                    endSimpleTag(cslCodeReader);
                    xslCodeWriter.write("<xsl:value-of select=\"$NEWLINE\"/>");
                } else if (blockType == '_') {
                    //Print  white space (simple tag - can contain only whitespace)
                    endSimpleTag(cslCodeReader);
                    xslCodeWriter.write("<xsl:value-of select=\"$SPACE\"/>");
                    //...
                } else if (blockType == '[') {
                    //Code execution control block
                    /*contain if:endif; while:endwhile; foreach:endforeach; choose:endchoose; otherwise:endotherwise*/
                    String body = readTagBody(cslCodeReader);
                    parseExecutionTagBody(body, xslCodeWriter);
                } else if (blockType == '{') {
                    //Code execution control block
                    /*contain if:endif; while:endwhile; foreach:endforeach; choose:endchoose; otherwise:endotherwise*/
                    String body = readTagBody(cslCodeReader,"}>");
                    xslCodeWriter.write("<xsl:text disable-output-escaping=\"yes\"><![CDATA[");
                    xslCodeWriter.write(body);
                    xslCodeWriter.write("]]></xsl:text>");
                }
            } else {
                xslCodeWriter.write(ch);
            }
        }
        //xslCodeWriter.flush();
        //Write end xsl template
        printText("XSLTemplate.end", xslCodeWriter);
        xslCodeWriter.flush();
    }

    protected void endSimpleTag(Reader reader) throws IOException, Exception {
        int r2 = reader.read();
        if (r2 == -1) {
            throw new Exception("You must close simple csl tag in line " + lineNumber + " beforu you end file");
        }
        char ch = (char) r2;
        if (ch != '>') {
            throw new Exception("You must close simple csl tag in line " + lineNumber + " (you don't use any char inside simple tags) ");
        }
    }

    protected String readTagBody(Reader reader) throws IOException, Exception {
        int beginLine = lineNumber;
        String body = new String();
        char ch;
        do{
            int r2 = reader.read();
            if (r2 == -1) {
                throw new Exception("You must close CSL tag with '>', tag begin in line " + beginLine + " ");
            }
            ch = (char) r2;
            if (ch == '\n') {
                lineNumber++;
            }
            if(ch=='>'){
                break;
            }
            body+=ch;
        }while(1==1);
        return body;
    }

    protected String readTagBody(Reader reader, String endTag) throws IOException, Exception {
        int beginLine = lineNumber;
        String body = new String();
        char ch;
        do{
            int r2 = reader.read();
            if (r2 == -1) {
                throw new Exception("You must close CSL tag with '>', tag begin in line " + beginLine + " ");
            }
            ch = (char) r2;
            if (ch == '\n') {
                lineNumber++;
            }
            body+=ch;
            if(body.endsWith(endTag)){
                break;
            }
        }while(1==1);
        return body.substring(0,body.length()-1);
    }

    protected void parseVariableTagBody(String body, Writer writer) throws Exception{
        //----------------------------
        String[] records = body.split("\\s+");
        for(String s : records){
            if(s.equals("")) continue;
            if(s.indexOf('=')!=-1){
                //set variable
                String[] parts = s.split("=",3);
                if(parts.length>2){
                    throw new Exception("You musn't use mult copy. You can use only one '=' char in tag body section");
                }
                int functionSeparator=parts[1].indexOf(':');
                if(functionSeparator!=-1){
                    writer.write("<xsl:variable name=\""+parts[0]+"\">");
                    writer.write( getFunctionBody(
                            parts[1].substring(0,functionSeparator),
                            parts[1].substring(functionSeparator+1)));
                    writer.write("</xsl:variable>");
                }else{
                    writer.write("<xsl:variable name=\""+parts[0]+"\" select=\""+parts[1]+"\" />");
                }
            }else{
                //get variable
                writer.write("<xsl:value-of select=\"$"+s+"\" />");
            }
        }
    }

    protected void parseAttributeTagBody(String body, Writer writer) throws Exception{
        //----------------------------
        String[] records = body.split("\\s+");
        for(String s : records){
            if(s.equals("")) continue;
            if(s.indexOf('=')!=-1){
                throw new Exception("You can not set attribute. teg end at line "+lineNumber);
            }else{
                //get variable
                writer.write("<xsl:value-of select=\"@"+s+"\" />");
            }
        }
    }


    static public class ExecutionTag{
        String openString;
        String endString;
        String CodeOpen;
        String CodeEnd;

        public ExecutionTag(String openString, String endString, String CodeOpen, String CodeEnd) {
            this.openString = openString;
            this.endString = endString;
            this.CodeOpen = CodeOpen;
            this.CodeEnd = CodeEnd;
        }
        
    }

    static class ExecutionTagStackRecord{
        ExecutionTag executionTag;
        String body;

        public ExecutionTagStackRecord(ExecutionTag executionTag, String body) {
            this.executionTag = executionTag;
            this.body = body;
        }

    }

    protected static List<ExecutionTag> exectutionTags;

    static{
        exectutionTags=new ArrayList<ExecutionTag>();
        exectutionTags.add(new ExecutionTag("if:", "if;", "<xsl:if test=\"%\">","</xsl:if>"));
        exectutionTags.add(new ExecutionTag("foreach:", "foreach;", "<xsl:for-each select=\"%\">", "</xsl:for-each>"));
        exectutionTags.add(new ExecutionTag("choose:", "choose;", "<xsl:choose>", "</xsl:choose>"));
        exectutionTags.add(new ExecutionTag("when:", "when;", "<xsl:when test=\"%\">", "</xsl:when>"));
        exectutionTags.add(new ExecutionTag("otherwise:", "otherwise;", "<xsl:otherwise>", "</xsl:otherwise>"));
    }

    List<ExecutionTagStackRecord> tagsStock=new ArrayList();

    protected void parseExecutionTagBody(String body, Writer writer) throws Exception{
        //----------------------------
        String bodyLow = body.toLowerCase();
        //Special for else
        if (bodyLow.startsWith("else:")) {
            ExecutionTagStackRecord oldTagRecord = tagsStock.get(tagsStock.size() - 1);
            if (oldTagRecord.executionTag.CodeOpen.equals("if:")) {
                throw new Exception("You can use else only in if tag");
            }
            writer.write("</xsl:if><xsl:if test=\"not(%)\">".replaceAll("%", oldTagRecord.body.substring(3)));
            return;
        }
        for(ExecutionTag tag : exectutionTags){
            if(bodyLow.startsWith(tag.openString)){
                tagsStock.add(new ExecutionTagStackRecord(tag,body));
                writer.write(tag.CodeOpen.replaceAll("%", body.substring(tag.openString.length())));
                return;
            }
            if(bodyLow.startsWith(tag.endString)){
                if(!tagsStock.isEmpty()){
                    ExecutionTag oldTag = tagsStock.get(tagsStock.size()-1).executionTag;
                    if(tag!=oldTag){
                        throw new Exception("You must close current tag with "+oldTag.CodeOpen+" befor you open "+tag.openString);
                    }
                }
                writer.write(tag.CodeEnd.replaceAll("%", body.substring(tag.endString.length())));
                tagsStock.remove(tagsStock.size()-1);
                return;
            }
        }
        throw new Exception("Don't know tag with body: '"+body+"' near line "+lineNumber);
    }


    public static class BodyFunction{
        String functionName;
        String code;
        public BodyFunction(String functionName, String code) {
            this.functionName = functionName;
            this.code = code;
        }
    }

    static List<BodyFunction> functions;

    static{
        functions=new ArrayList<BodyFunction>();
        functions.add(new BodyFunction(
                "uf",
                "<xsl:value-of select=\"translate(substring(%,1,1), $SMALLCASE, $UPPERCASE)\" /><xsl:value-of select=\"substring(%,2)\" />"));
        functions.add(new BodyFunction(
                "lf",
                "<xsl:value-of select=\"translate(substring(%,1,1), $UPPERCASE, $SMALLCASE)\" /><xsl:value-of select=\"substring(%,2)\" />"));
        functions.add(new BodyFunction(
                "lower",
                "<xsl:value-of select=\"translate(%, $SMALLCASE, $UPPERCASE)\" />"));
        functions.add(new BodyFunction(
                "upper",
                "<xsl:value-of select=\"translate(%, $UPPERCASE, $SMALLCASE)\" />"));
    }

    String getFunctionBody(String functionName, String body) throws Exception{
        for(BodyFunction bf : functions){
            if(bf.functionName.equals(functionName)){
                String code = bf.code.replace("%", body);
                return code;
            }
        }
        throw new Exception("No know function: "+functionName);
    }



    private final static void printText(String filename, Writer out) throws IOException {
        InputStream is = CSLCompiler_oldVersion.class.getResourceAsStream(filename);
        Reader reader = new InputStreamReader(is);
        char[] buffer = new char[4096];
        int read = 0;
        while ((read = reader.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        reader.close();
    }
}
