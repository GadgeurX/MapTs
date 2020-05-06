package com.rcorp.mapts.model.geometry

class MultiPoint: PointComposedGeom {
  constructor() : super(MULTIPOINT) {}
  constructor(points:Array<Geometry?>) : super(MULTIPOINT, points) {}
  constructor(value:String) : this(value, false) {}
  constructor(value:String, haveM:Boolean) : super(MULTIPOINT, value, haveM) {}
  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
  }
}