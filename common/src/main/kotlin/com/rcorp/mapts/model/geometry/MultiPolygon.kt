package com.rcorp.mapts.model.geometry

class MultiPolygon: ComposedGeom {
  val polygons:Array<Polygon>
  get() {
    return subgeoms as Array<Polygon>
  }
  constructor() : super(MULTIPOLYGON) {}
  constructor(polygons:Array<Geometry?>) : super(MULTIPOLYGON, polygons) {}
  constructor(value:String) : this(value, false) {}
  constructor(value:String, haveM:Boolean) : super(MULTIPOLYGON, value, haveM) {}
  protected override fun createSubGeomArray(npolygons:Int):Array<Geometry?> {
    return arrayOfNulls<Geometry?>(npolygons)
  }
  protected override fun createSubGeomInstance(token:String, haveM:Boolean): Geometry {
    return Polygon(token, haveM)
  }
  fun numPolygons():Int {
    return subgeoms.size
  }
  fun getPolygon(idx:Int): Polygon {
    if ((idx >= 0) and (idx < subgeoms.size))
    {
      return subgeoms[idx] as Polygon
    }
    else
    {
      return Polygon()
    }
  }
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}