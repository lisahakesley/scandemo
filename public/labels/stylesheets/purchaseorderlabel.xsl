<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <xsl:param name="versionParam" select="'1.0'"/> 
  <!-- ========================= -->
  <!-- root element: labels -->
  <!-- ========================= -->
  <xsl:template match="labels">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="PurchaseOrderLabel" margin=".25in" page-width="2.4in" page-height="3.9in">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="PurchaseOrderLabel">
        <fo:flow flow-name="xsl-region-body">
           <fo:block>
                <xsl:apply-templates select="label"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <!-- ========================= -->
  <!-- child element: label     -->
  <!-- ========================= -->
  <xsl:template match="label">
        <xsl:param name="qrcodeVar">
            <xsl:value-of select="qrcode" />
        </xsl:param>    
        <fo:block break-after="page">    
            <fo:block-container absolute-position="absolute" top="0.0in" left="0.0in">
                <fo:block>
                    <fo:instream-foreign-object>
                      <svg:svg width="2.4in" height="3.9in" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                          <svg:g transform="translate(154,5)">
                            <svg:g transform="rotate(90)" font-family="Verdana" font-size="12" >
                              <svg:text x="0" y="30"><xsl:value-of select="itemName" /></svg:text>
                              <svg:text x="0" y="60">Vendor: <xsl:value-of select="vendorName" /></svg:text>
                              <svg:text x="0" y="90">Pack size: <xsl:value-of select="packSize" /></svg:text>
                            </svg:g>
                          </svg:g>
                      </svg:svg>
                    </fo:instream-foreign-object>
                </fo:block>
              </fo:block-container>      
              <fo:block-container absolute-position="absolute" top="144px" left="0px" break-before="page" margin="0px">
                <fo:block  break-after="page"  >
                  <fo:external-graphic src="url('{$qrcodeVar}')"  />
                </fo:block>
              </fo:block-container>
          </fo:block>
  </xsl:template>

</xsl:stylesheet>

