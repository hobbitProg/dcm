package com.github.hobbitProg.dcm.client.cds.cdCatalog.model

class NoCDHasGivenISRC(
  val invalidISRC: ISRCs
) extends Exception(
  "Invalid ISRC: " +
    invalidISRC
) {
}
