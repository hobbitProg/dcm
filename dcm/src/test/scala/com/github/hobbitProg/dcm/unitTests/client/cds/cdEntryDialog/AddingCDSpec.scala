package com.github.hobbitProg.dcm.unitTests.client.cds.cdEntryDialog

import org.scalatest.FreeSpec

/**
  * Specification for adding a CD to the catalog using the dialog
  * @author Kyle Cranmer
  * @since 0.4
  */
class AddingCDSpec
    extends FreeSpec {
  "Given the categories that can be associated with CDs" - {
    "and valid informatin on a CD to add to the catalog" - {
      "and the catalog that is being updated" - {
        "and the repository to place the CD catalog information into" - {
          "and the service for the CD catalog" - {
            "and the parent window that created the CD creation window" - {
              "when the CD dialog is created" - {
                "and the title of the CD is entered" - {
                  "and the artist of the CD is entered" - {
                    "and the ASIN of the CD is entered" - {
                      "and the cover of the CD is chosen" - {
                        "and the appropriate categories are associated with " +
                        "the CD" - {
                          "and the CD information is saved" - {
                            "then the CD entry dialog is closed" in (pending)
                            "and the CD was saved into the catalog" in (pending)
                            "and the CD was saved into the repostory" in (pending)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}
