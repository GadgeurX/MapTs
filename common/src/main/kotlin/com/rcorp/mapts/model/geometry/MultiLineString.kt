package com.rcorp.mapts.model.geometry

class MultiLineString: ComposedGeom {
  internal var len = -1.0
  val lines:Array<LineString>
  get() {
    return subgeoms.toList().toTypedArray() as Array<LineString>
  }
  override fun hashCode():Int {
    return super.hashCode() xor this.length().toInt()
  }
  constructor() : super(MULTILINESTRING) {}
  constructor(lines:Array<Geometry?>) : super(MULTILINESTRING, lines) {}
  constructor(value:String, haveM:Boolean = false) : super(MULTILINESTRING, value, haveM) {}
  protected override fun createSubGeomInstance(token:String, haveM:Boolean): Geometry {
    return LineString(token, haveM)
  }
  protected override fun createSubGeomArray(nlines:Int):Array<Geometry?> {
    return arrayOfNulls<Geometry?>(nlines)
  }
  fun numLines():Int {
    return subgeoms.size
  }
  fun getLine(idx:Int): LineString {
    if ((idx >= 0) and (idx < subgeoms.size))
    {
      return subgeoms[idx] as LineString
    }
    else
    {
      return LineString()
    }
  }
  fun length():Double {
    if (len < 0)
    {
      val lines = subgeoms as Array<LineString>
      if (lines.size < 1)
      {
        len = 0.0
      }
      else
      {
        var sum = 0.0
        for (i in lines.indices)
        {
          sum += lines[i].length()
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