package com.rcorp.mapts.data.geometry

import com.rcorp.mapts.model.geometry.*
import java.lang.Exception

object GeometryBuilder {
  /** The prefix that indicates SRID presence */
  val SRIDPREFIX = "SRID="
  @JvmOverloads fun geomFromString(value:String, haveM:Boolean = false): Geometry {
    val bp = BinaryParser()
    return geomFromString(value, bp, haveM)
  }
  @JvmOverloads fun geomFromString(value:String, bp:BinaryParser, haveM:Boolean = false): Geometry {
    var value = value.trim({ it <= ' ' })
    var srid = Geometry.UNKNOWN_SRID
    if (value.startsWith(SRIDPREFIX))
    {
      // break up geometry into srid and wkt
      val parts = splitSRID(value)
      value = parts[1].trim({ it <= ' ' })
      srid = Geometry.parseSRID(Integer.parseInt(parts[0].substring(5)))
    }
    val result: Geometry
    if (value.startsWith("00") || value.startsWith("01"))
    {
      result = bp.parse(value)
    }
    else if (value.startsWith("MULTIPOLYGON"))
    {
      result = MultiPolygon(value, haveM)
    }
    else if (value.startsWith("MULTILINESTRING"))
    {
      result = MultiLineString(value, haveM)
    }
    else if (value.startsWith("MULTIPOINT"))
    {
      result = MultiPoint(value, haveM)
    }
    else if (value.startsWith("POLYGON"))
    {
      result = Polygon(value, haveM)
    }
    else if (value.startsWith("LINESTRING"))
    {
      result = LineString(value, haveM)
    }
    else if (value.startsWith("POINT"))
    {
      result = Point(value, haveM)
    }
    else
    {
      throw Exception("Unknown type: " + value)
    }
    if (srid != Geometry.UNKNOWN_SRID)
    {
      result.srid = srid
    }
    return result
  }
  /**
 * Splits a String at the first occurrence of border character.
 *
 * Poor man's String.split() replacement, as String.split() was invented at
 * jdk1.4, and the Debian PostGIS Maintainer had problems building the woody
 * backport of his package using DFSG-free compilers. In all the cases we
 * used split() in the org.postgis package, we only needed to split at the
 * first occurence, and thus this code could even be faster.
 *
 * @param whole the String to be split
 * @return String array containing the split elements
 * @throws SQLException when a SQLException occurrs
 */
  fun splitSRID(whole:String):Array<String> {
    val index = whole.indexOf(';', 5) // sridprefix length is 5
    if (index == -1)
    {
      throw Exception("Error parsing Geometry - SRID not delimited with ';' ")
    }
    else
    {
      return arrayOf<String>(whole.substring(0, index), whole.substring(index + 1))
    }
  }
}/**
 * Maybe we could add more error checking here?
 *
 * @param value String representing the geometry
 * @param bp BinaryParser to use whe parsing
 * @return Geometry object parsed from the specified string value
 * @throws SQLException when a SQLException occurs
 */