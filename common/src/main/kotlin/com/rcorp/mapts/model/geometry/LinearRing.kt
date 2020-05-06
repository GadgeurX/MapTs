package com.rcorp.mapts.model.geometry

class LinearRing: PointComposedGeom {
  constructor(points:Array<Geometry?>) : super(LINEARRING, points) {}
  /**
 * This is called to construct a LinearRing from the PostGIS string
 * representation of a ring.
 *
 * @param value Definition of this ring in the PostGIS string format.
 * @throws SQLException when a SQLException occurs
 */
  constructor(value:String) : this(value, false) {}
  /**
 * @param value The text representation of this LinearRing
 * @param haveM Hint whether we have a measure. This is given to us by other
 * "parent" Polygon, and is passed further to our parent.
 * @throws SQLException when a SQLException occurs
 */
  constructor(value:String, haveM:Boolean) : super(LINEARRING) {
    val valueNoParans = GeometryTokenizer.removeLeadingAndTrailingStrings(value.trim({ it <= ' ' }), "(", ")")
    val tokens = GeometryTokenizer.tokenize(valueNoParans, ',')
    val npoints = tokens.size
    val points = arrayOfNulls<Point>(npoints)
    for (p in 0 until npoints)
    {
      points[p] = Point(tokens.get(p), haveM)
    }
    this.dimension = points[0]?.dimension ?: 0
    // fetch haveMeasure from subpoint because haveM does only work with
    // 2d+M, not with 3d+M geometries
    this.isMeasured = points[0]?.isMeasured ?: false
    this.subgeoms = points as Array<Geometry?>
  }
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}