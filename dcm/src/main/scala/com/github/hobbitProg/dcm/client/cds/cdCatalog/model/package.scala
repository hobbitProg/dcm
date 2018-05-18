package com.github.hobbitProg.dcm.client.cds.cdCatalog

import java.net.URI
package object model {
  type Titles = String
  type Artists = String
  type ISRCs = String
  type CoverImages = Option[URI]
  type Categories = String
}
