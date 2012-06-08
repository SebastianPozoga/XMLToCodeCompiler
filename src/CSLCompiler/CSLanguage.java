/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CSLCompiler;

/**
 *
 * @author sebastian
 */
public class CSLanguage extends CSLCompilerSchema {

   /*
    * Const
    */
   public static final String CHOOSE_TAGS = "choose";


    public CSLanguage() {
        //Add STANDARD_TAGS
        Tag ifTag = new Tag("<[if:", "<[if;>", "<xsl:if test=\"%\">","</xsl:if>");
        ifTag.specialInsideOneExpresion="<[else:>";
        ifTag.specialInsideOneExpresionCode="</xsl:if><xsl:if test=\"not(%)\">";
        add(ifTag, STANDARD_TAGS);

        Tag foreachTag = new Tag("<[foreach:", "<[foreach;>", "<xsl:for-each select=\"%\">", "</xsl:for-each>");
        add(foreachTag, STANDARD_TAGS);

        Tag chooseTag = new Tag("<[choose:", "<[choose;>", "<xsl:choose>", "</xsl:choose>");
        chooseTag.insideTags=CHOOSE_TAGS;
        add(chooseTag, STANDARD_TAGS);

        //Add CHOOSE_TAGS
        Tag whenTag = new Tag("<[when:", "<[when;>", "<xsl:when test=\"%\">", "</xsl:when>");
        add(whenTag, STANDARD_TAGS);

        Tag otherwiseTag = new Tag("<[otherwise:", "<[otherwise;>", "<xsl:otherwise>", "</xsl:otherwise>");
        add(otherwiseTag, STANDARD_TAGS);

        
        //Add replaces
        add(new Replace("<", "<xsl:text disable-output-escaping=\"yes\"><![CDATA[<]]></xsl:text>"));
        add(new Replace(">", "<xsl:text disable-output-escaping=\"yes\"><![CDATA[>]]></xsl:text>"));
        add(new Replace("&", "<xsl:text disable-output-escaping=\"yes\"><![CDATA[&]]></xsl:text>"));
        add(new Replace("<.>", "<xsl:value-of select=\".\"/>"));
        add(new Replace("<;>", "<xsl:value-of select=\"$NEWLINE\"/>"));
        add(new Replace("<_>", "<xsl:value-of select=\"$SPACE\"/>"));


        //Add errror sequence
        add(new ErrorSequence("<![CDATA[", "Can not use CDATA sequences."));
        add(new ErrorSequence("]]>", "Can not use CDATA sequences."));
    }

}
