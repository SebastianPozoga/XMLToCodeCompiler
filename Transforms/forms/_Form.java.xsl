<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="yes"/>

    <xsl:template match="/codemetadata">
        <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
        <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
 /* To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.pozoga.jspf.form;

import eu.pozoga.jspf.core.Form;
import eu.pozoga.jspf.core.Form.Massage;
import eu.pozoga.jspf.core.ServiceUnit;
import eu.pozoga.jspf.model.<xsl:value-of select="@name"/>;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sebastian
 */
public class <xsl:value-of select="@name"/>
    <!-- Extends Form -->
    <xsl:text disable-output-escaping="yes"><![CDATA[Form extends Form< ]]></xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text disable-output-escaping="yes"><![CDATA[>]]></xsl:text>{


    <!-- Constructor -->
    public <xsl:value-of select="@name"/>Form(<xsl:value-of select="@name"/> entity, String namespace) {
        super(entity, namespace);
    }





    <!-- Validate Function -->
    @Override
    public boolean validate() {
        /*
         * Init variables
         */
        boolean isValid = true;
        <xsl:text disable-output-escaping="yes">List<![CDATA[<Massage>]]> massage;
        </xsl:text>
        
        <xsl:value-of select="@name" />
        entity = getEntity();

        <!-- Generate Code for properties -->
        <xsl:for-each select="properties/property">
        //<xsl:value-of select="@name"/>
        <xsl:choose>
        <xsl:when test="count(validators)">
        <!--
            Define and inicjalise propert variable
            -->
        <!-- property value -->
        <xsl:text disable-output-escaping="yes"><![CDATA[
        ]]></xsl:text>
        <xsl:value-of select="@type"/><xsl:text disable-output-escaping="yes"> </xsl:text><xsl:value-of select="@name"/> = entity.get<xsl:value-of select="translate(substring(@name,1,1), $smallcase, $uppercase)" /><xsl:value-of select="substring(@name,2)" />();

        </xsl:when>
                <xsl:otherwise>
                    When-false
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>

        setValid(isValid);
        return isValid;}

    @Override
    public void decode(ServletRequest req, String baseName) throws Throwable {
        DBConnectorService cs = (DBConnectorService) ServiceUnit.getService((HttpServletRequest) req, null, DBConnectorService.class);
        Session session = cs.getSession();
        //Decode
        loadString("setName", req.getParameter(baseName+"Name"));
        loadString("setPassword", req.getParameter(baseName+"Password"));
        loadString("setViewname", req.getParameter(baseName+"Viewname"));
        loadString("setFirstname", req.getParameter(baseName+"Firstname"));
        loadString("setLastname", req.getParameter(baseName+"Lastname"));
        loadString("setEmail", req.getParameter(baseName+"Email"));
        loadEntity("setUsergroup", req.getParameter(baseName+"Usergroup"), session, UsergroupManager.getInstance());
    }

    public void decode(HttpServletRequest req) throws Throwable {
        decode(req, getNamespace());
    }

}
    </xsl:template>




    <xsl:template match="validators">
        <xsl:apply-templates select="."/>
    </xsl:template>

    <xsl:template match="validators/lenght">
        <xsl:text disable-output-escaping="yes"><![CDATA[
            isValid &= eu.pozoga.jspf.validators.LengthValidator.validate(massage, 3, 40, u.getLastname());
        ]]></xsl:text>
    </xsl:template>
</xsl:stylesheet>