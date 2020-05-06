package com.rcorp.mapts.model.geometry

class Polygon: ComposedGeom {
  constructor() : super(POLYGON) {}
  constructor(rings:Array<Geometry?>) : super(POLYGON, rings) {}
  constructor(value:String, haveM:Boolean = false) : super(POLYGON, value, haveM) {}
  protected override fun createSubGeomInstance(token:String, haveM:Boolean): Geometry {
    return LinearRing(token, haveM)
  }
  protected override fun createSubGeomArray(ringcount:Int):Array<Geometry?> {
    return arrayOfNulls<Geometry?>(ringcount)
  }
  fun numRings():Int {
    return subgeoms.size
  }
  fun getRing(idx:Int): LinearRing {
    if ((idx >= 0) and (idx < subgeoms.size))
    {
      return subgeoms[idx] as LinearRing
    }
    else
    {
      return LinearRing("")
    }
  }
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}