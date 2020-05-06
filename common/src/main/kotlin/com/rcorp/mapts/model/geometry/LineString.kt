package com.rcorp.mapts.model.geometry

class LineString: PointComposedGeom {
  internal var len = -1.0
  constructor() : super(LINESTRING) {}
  constructor(points:Array<Geometry?>) : super(LINESTRING, points) {}
  constructor(value:String) : super(LINESTRING, value) {}
  constructor(value:String, haveM:Boolean) : super(LINESTRING, value, haveM) {}
  fun reverse(): LineString {
    val points = this.points
    val l = points.size
    var i:Int
    var j:Int
    val p = arrayOfNulls<Geometry>(l)
    i = 0
    j = l - 1
    while (i < l)
    {
      p[i] = points[j]
      i++
      j--
    }
    return LineString(p)
  }
  fun concat(other: LineString): LineString {
    val points = this.points
    val opoints = other.points
    val cutPoint = (this.lastPoint == null || this.lastPoint.equals(other.firstPoint))
    val count = points.size + opoints.size - (if (cutPoint) 1 else 0)
    val p = arrayOfNulls<Geometry>(count)
    // Maybe we should use System.arrayCopy here?
    var i:Int
    var j:Int
    i = 0
    while (i < points.size)
    {
      p[i] = points[i]
      i++
    }
    if (!cutPoint)
    {
      p[i++] = other.firstPoint
    }
    j = 1
    while (j < opoints.size)
    {
      p[i] = opoints[j]
      j++
      i++
    }
    return LineString(p)
  }
  fun length():Double {
    if (len < 0)
    {
      val points = this.points
      if ((points == null) || (points.size < 2))
      {
        len = 0.0
      }
      else
      {
        var sum = 0.0
        for (i in 1 until points.size)
        {
          sum += points[i - 1].distance(points[i])
        }
        len = sum
      }
    }
    return len
  }
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}