<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" doctype-system="http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd" doctype-public="-//Hibernate/Hibernate Mapping DTD 3.0//EN"/>

    <xsl:template match="/codemetadata">
        <hibernate-mapping>
            <class name="eu.pozoga.jspf.model.User">
                <xsl:attribute name="name">eu.pozoga.jspf.model.<xsl:value-of select="@name" /></xsl:attribute>
                <id name="id">
                    <generator class="increment"/>
                </id>
                <xsl:for-each select="properties/property">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
                <xsl:for-each select="relations/*">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
            </class>
            <xsl:for-each select="queries/*">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </hibernate-mapping>
    </xsl:template>

    <xsl:template match="property">
        <property name="{@name}" not-null="{@not-null='true'}" unique="true" />
    </xsl:template>

    <xsl:template match="update-query|save-query|delete-query|report-query">
        <query name="{@name}">
            <xsl:value-of select="."/>
        </query>
    </xsl:template>

    <xsl:template match="many-to-one">
        <many-to-one name="{@name}" />
    </xsl:template>


</xsl:stylesheet>