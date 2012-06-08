<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="yes"/>

    <xsl:template match="/*">
        <!-- CONST -->
        <xsl:variable name="SMALLCASE" select="'abcdefghijklmnopqrstuvwxyz'" />
        <xsl:variable name="UPPERCASE" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
        <xsl:variable name="NEWLINE">
            <xsl:text>
            </xsl:text>
        </xsl:variable>
        <xsl:variable name="LT">
            <xsl:text disable-output-escaping="yes"><![CDATA[<]]></xsl:text>
        </xsl:variable>
        <xsl:variable name="GT">
            <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>
        </xsl:variable>
        <xsl:variable name="AND">
            <xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text>
        </xsl:variable>
        <xsl:variable name="SPACE">
            <xsl:text disable-output-escaping="yes"> </xsl:text>
        </xsl:variable>/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.pozoga.jspf.form;

import eu.pozoga.jspf.core.Form;
import eu.pozoga.jspf.core.Form.Massage;
import eu.pozoga.jspf.core.ServiceUnit;
import eu.pozoga.jspf.model.User;
import eu.pozoga.jspf.model.UsergroupManager;
import eu.pozoga.jspf.services.DBConnectorService;
import eu.pozoga.jspf.validators.EmailValidator;
import eu.pozoga.jspf.validators.LengthValidator;
import eu.pozoga.jspf.validators.RequiredValidator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;



/**
 *
 * @author sebastian
 */
<xsl:variable name="modelName"><xsl:value-of select="translate(substring(@name,1,1), $SMALLCASE, $UPPERCASE)" /><xsl:value-of select="substring(@name,2)" /></xsl:variable>
public class <xsl:value-of select="$modelName" />Form extends Form  {

    public <xsl:value-of select="$modelName" />Form(<xsl:value-of select="$modelName" /> entity) {
        super(entity);
    }


    @Override
    public boolean validate() {
        boolean isValid = true;
        <xsl:value-of select="$modelName" /> entity = getEntity();
        <xsl:text disable-output-escaping="yes"><![CDATA[List <Massage> massage;]]></xsl:text>

        //Properties
        <xsl:for-each select="properties/property">
        //<xsl:value-of select="@name" />
        <xsl:variable name="nameUF"><xsl:value-of select="translate(substring(@name,1,1), $SMALLCASE, $UPPERCASE)" /><xsl:value-of select="substring(@name,2)" /></xsl:variable>
        <xsl:variable name="nameLF"><xsl:value-of select="translate(substring(@name,1,1), $UPPERCASE, $SMALLCASE)" /><xsl:value-of select="substring(@name,2)" /></xsl:variable>
        massage = getMassages("<xsl:value-of select="$nameUF" />");
        <xsl:value-of select="$nameUF" /><xsl:value-of select="$SPACE" /><xsl:value-of select="$nameLF" /> = entity.get<xsl:value-of select="$nameUF" />();
           <xsl:for-each select="validators/lenght">
                isValid = isValid <xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text> LengthValidator.validate(massage, <xsl:value-of select="@min" />, <xsl:value-of select="@max" />, <xsl:value-of select="$nameLF" />());
            </xsl:for-each>
            <xsl:if test="@not-null='true'">
                isValid = isValid <xsl:text disable-output-escaping="yes"><![CDATA[&]]></xsl:text> RequiredValidator.validate(massage, entity.get<xsl:value-of select="$nameUF" />());
            </xsl:if>
        </xsl:for-each>

        setValid(isValid);
        return isValid;
    }

}
    </xsl:template>
</xsl:stylesheet>