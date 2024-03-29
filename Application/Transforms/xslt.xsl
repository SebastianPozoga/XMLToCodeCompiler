<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" encoding="UTF-8"/>

  <!-- simply copy the message to the result tree -->
  <xsl:template match="/">
    <xsl:value-of select="message"/>
    <xsl:apply-templates/>
  </xsl:template>

  <!-- simply copy the message to the result tree -->
  <xsl:template match="test">
    <![CDATA[<jsp:text>]]>
	<xsl:value-of select="message"/>
    <![CDATA[</jsp:text>]]>
  </xsl:template>
</xsl:stylesheet> 