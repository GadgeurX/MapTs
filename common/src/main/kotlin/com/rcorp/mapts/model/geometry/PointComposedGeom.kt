package com.rcorp.mapts.model.geometry

abstract class PointComposedGeom: ComposedGeom {
  /**
 * Get the underlying Point array
 *
 * @return an array of Points within this geometry
 */
  val points:Array<Point>
  get() {
    return subgeoms as Array<Point>
  }
  protected constructor(type:Int) : super(type) {}
  protected constructor(type:Int, points:Array<Geometry?>) : super(type, points) {}
  constructor(type:Int, value:String, haveM:Boolean = false) : super(type, value, haveM) {}
  protected override fun createSubGeomInstance(token:String, haveM:Boolean): Geometry {
    return Point(token, haveM)
  }
  protected override fun createSubGeomArray(pointcount:Int):Array<Geometry?> {
    return arrayOfNulls<Geometry?>(pointcount)
  }

  override fun innerWKT(sb: String): String {
    var result = subgeoms[0]?.innerWKT(sb)
    for (i in 1 until subgeoms.size)
    {
      result = ",${subgeoms[i]?.innerWKT(result ?: "")}"
    }
    return result ?: ""
  }
  /**
 * optimized version
 */
  override fun numPoints():Int {
    return subgeoms.size
  }
  /**
 * optimized version
 */
  override fun getPoint(idx:Int): Point {
    if ((idx >= 0) and (idx < subgeoms.size))
    {
      return subgeoms[idx] as Point
    }
    else
    {
      return Point()
    }
  }
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}