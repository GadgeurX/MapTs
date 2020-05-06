package com.rcorp.mapts.model.geometry

abstract class ComposedGeom: Geometry {
  /**
 * The Array containing the geometries
 *
 * This is only to be exposed by concrete subclasses, to retain type safety.
 */
  protected var subgeoms = EMPTY
  /**
 * Optimized version
 */

  override var srid = UNKNOWN_SRID
  set(value) {
    field = value
    for (i in subgeoms.indices)
    {
      subgeoms[i]?.srid = srid
    }
  }

  override val lastPoint: Point
  get() {
    if ((subgeoms == null) || (subgeoms.size == 0))
    {
      throw IndexOutOfBoundsException("Empty Geometry has no Points!")
    }
    else
    {
      return subgeoms[subgeoms.size - 1]?.lastPoint ?: Point()
    }
  }
  /**
 * Optimized version
 */
  override val firstPoint: Point
  get() {
    if ((subgeoms == null) || (subgeoms.size == 0))
    {
      throw IndexOutOfBoundsException("Empty Geometry has no Points!")
    }
    else
    {
      return subgeoms[0]?.firstPoint ?: Point()
    }
  }
  val isEmpty:Boolean
  get() {
    return (subgeoms == null) || (subgeoms.size == 0)
  }
  // Hashing - still buggy!
  internal var nohash = true
  internal var hashcode = 0
  /**
 * Constructs an instance with the specified type
 *
 * @param type int value corresponding to the geometry type.
 */
  constructor(type:Int) : super(type) {}
  fun getSubGeometry(index:Int): Geometry? {
    return subgeoms[index]
  }
  fun numGeoms():Int {
    return subgeoms.size
  }
  protected constructor(type:Int, geoms:Array<Geometry?>) : this(type) {
    this.subgeoms = geoms
    if (geoms.size > 0)
    {
      dimension = geoms[0]?.dimension ?: 0
      isMeasured = geoms[0]?.isMeasured ?: false
    }
    else
    {
      dimension = 0
    }
  }

  protected constructor(type:Int, value:String, haveM:Boolean) : super(type) {
    var value = initSRID(value)
    val typestring = getTypeString(type)
    var haveM = haveM
    if (value.indexOf(typestring) == 0)
    {
      var pfxlen = typestring.length
      if (value.get(pfxlen) == 'M')
      {
        pfxlen += 1
        haveM = true
      }
      value = value.substring(pfxlen).trim({ it <= ' ' })
    }
    else if (value.get(0) != '(')
    {
      // we are neigher inner nor outer rep.
      throw Exception("Error parsing a " + typestring + " out of " + value)
    }
    if (value == "(EMPTY)")
    {
      // Special case for PostGIS 0.X style empty geometry collections
      // (which are not OpenGIS compliant)
      return
    }
    val valueNoParans = GeometryTokenizer.removeLeadingAndTrailingStrings(value, "(", ")")
    val tokens = GeometryTokenizer.tokenize(valueNoParans, ',')
    val subgeomcount = tokens.size
    subgeoms = createSubGeomArray(subgeomcount)
    for (p in 0 until subgeomcount)
    {
      subgeoms[p] = createSubGeomInstance(tokens.get(p), haveM)
    }
    dimension = subgeoms[0]?.dimension ?: 0
    // fetch haveMeasure from sub-point because haveM does only work with
    // 2d+M, not with 3d+M geometries
    isMeasured = subgeoms[0]?.isMeasured ?: false
  }
  /**
 * Return the appropriate instance of the sub-geometry - this encapsulates
 * subclass specific constructor calls
 *
 * @param token The token containing the value for the sub-geometry
 * @param haveM flag to indicate the existence of a measure
 * @return the new sub-geometry
 * @throws SQLException if a SQLException is thrown
 */
  protected abstract fun createSubGeomInstance(token:String, haveM:Boolean): Geometry
  /**
 * Return the appropriate instance of the sub-geometry array - this
 * encapsulates subclass specific array instantiation
 *
 * @param size number of elements in the array
 * @return Geometry array corresponding to the sub-geometry
 */
  protected abstract fun createSubGeomArray(size:Int):Array<Geometry?>
  override fun equalsintern(other: Geometry?):Boolean {
    // Can be assumed to be the same subclass of Geometry, so it must be a
    // ComposedGeom, too.
    val cother = other as ComposedGeom
    if (cother.subgeoms == null && subgeoms == null)
    {
      return true
    }
    else if (cother.subgeoms == null || subgeoms == null)
    {
      return false
    }
    else if (cother.subgeoms.size != subgeoms.size)
    {
      return false
    }
    else if (subgeoms.size == 0)
    {
      return true
    }
    else
    {
      for (i in subgeoms.indices)
      {
        if (cother.subgeoms[i]?.equalsintern(this.subgeoms[i]) != true)
        {
          return false
        }
      }
    }
    return true
  }
  override fun numPoints():Int {
    if ((subgeoms == null) || (subgeoms.size == 0))
    {
      return 0
    }
    else
    {
      var result = 0
      for (i in subgeoms.indices)
      {
        result += subgeoms[i]?.numPoints() ?: 0
      }
      return result
    }
  }
  override fun getPoint(n:Int): Point {
    var n = n
    if (n < 0)
    {
      throw IndexOutOfBoundsException("Negative index not allowed")
    }
    else if ((subgeoms == null) || (subgeoms.size == 0))
    {
      throw IndexOutOfBoundsException("Empty Geometry has no Points!")
    }
    else
    {
      for (i in subgeoms.indices)
      {
        val current = subgeoms[i]
        val np = current?.numPoints() ?: 0
        if (n < np)
        {
          return current?.getPoint(n) ?: Point()
        }
        else
        {
          n -= np
        }
      }
      throw IndexOutOfBoundsException("Index too large!")
    }
  }
  fun iterator():Iterator<Geometry?> {
    return (subgeoms).iterator()
  }

  override fun mediumWKT(sb:String): String {
    var result = sb
    if ((subgeoms == null) || (subgeoms.size == 0))
    {
      result = "${result} EMPTY"
    }
    else
    {
      result = "(${innerWKT(result)})"
    }
    return result
  }
  override fun innerWKT(sb:String): String {
    var result = subgeoms[0]?.mediumWKT(sb)
    for (i in 1 until subgeoms.size)
    {
      result = ",${subgeoms[i]?.mediumWKT(result?:"")}"
    }
    return result ?: ""
  }

  override fun hashCode():Int {
    if (nohash)
    {
      hashcode = super.hashCode() xor subgeoms.hashCode()
      nohash = false
    }
    return hashcode
  }
  override fun checkConsistency():Boolean {
    if (super.checkConsistency())
    {
      if (isEmpty)
      {
        return true
      }
      // cache to avoid getMember opcode
      val _dimension = this.dimension
      val _haveMeasure = this.isMeasured
      val _srid = this.srid
      for (i in subgeoms.indices)
      {
        val sub = subgeoms[i]
        if (!((sub?.checkConsistency() == true && sub.dimension == _dimension
               && sub.isMeasured == _haveMeasure && sub.srid == _srid)))
        {
          return false
        }
      }
      return true
    }
    else
    {
      return false
    }
  }

  companion object {
    /* JDK 1.5 Serialization */
    private val serialVersionUID:Long = 0x100
    val EMPTY = arrayOfNulls<Geometry>(0)
  }
}