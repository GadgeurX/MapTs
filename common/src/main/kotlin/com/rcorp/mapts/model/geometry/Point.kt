package com.rcorp.mapts.model.geometry

import kotlin.math.sqrt

class Point(): Geometry(POINT) {
    /** Optimized versions for this special case */
    override val firstPoint: Point
        get() {
            return this
        }
    /** Optimized versions for this special case */
    override val lastPoint: Point
        get() {
            return this
        }
    /**
     * The X coordinate of the point.
     * In most long/lat systems, this is the longitude.
     */
    var x:Double = 0.toDouble()
    /**
     * The Y coordinate of the point.
     * In most long/lat systems, this is the latitude.
     */
    var y:Double = 0.toDouble()
    /**
     * The Z coordinate of the point.
     * In most long/lat systems, this is a radius from the
     * center of the earth, or the height / elevation over
     * the ground.
     */
    var z:Double = 0.toDouble()
    /**
     * The measure of the point.
     */
    var m = 0.0
    set(value) {
        isMeasured = true
        field = value
    }

    override fun hashCode():Int {
        return super.hashCode() xor hashCode(x) xor hashCode(y) xor hashCode(z) xor hashCode(m)
    }

    override fun equalsintern(otherg: Geometry?):Boolean {
        val other = otherg as Point
        return equals(other)
    }

    override fun checkConsistency(): Boolean {
        return (super.checkConsistency() && (this.dimension == 3 || this.z === 0.0)
                && (this.isMeasured || this.m === 0.0))
    }

    fun equals(other: Point):Boolean {
        val xequals = double_equals(x, other.x)
        val yequals = double_equals(y, other.y)
        val zequals = ((dimension == 2) || double_equals(z, other.z))
        val mequals = (!isMeasured || double_equals(m, other.m))
        val result = xequals && yequals && zequals && mequals
        return result
    }
    override fun getPoint(index:Int): Point {
        if (index == 0)
        {
            return this
        }
        else
        {
            throw IndexOutOfBoundsException("com.rcorp.mapts.model.geometry.Point only has a single com.rcorp.mapts.model.geometry.Point! $index")
        }
    }
    override fun numPoints():Int {
        return 1
    }
    /** Constructs a new com.rcorp.mapts.model.geometry.Point
     * @param x the longitude / x ordinate
     * @param y the latitude / y ordinate
     * @param z the radius / height / elevation / z ordinate
     */
    constructor(x:Double, y:Double, z:Double) : this() {
        this.x = x
        this.y = y
        this.z = z
        dimension = 3
    }
    /** Constructs a new com.rcorp.mapts.model.geometry.Point
     * @param x the longitude / x ordinate
     * @param y the latitude / y ordinate
     */
    constructor(x:Double, y:Double) : this() {
        this.x = x
        this.y = y
        this.z = 0.0
        dimension = 2
    }
    /**
     * Construct a com.rcorp.mapts.model.geometry.Point from EWKT.
     *
     * (3D and measures are legal, but SRID is not allowed).
     *
     * @param value String representation of the geometry.
     * @throws SQLException when a SQLException occurs
     */
    constructor(value:String) : this(value, false) {}
    /**
     * Construct a com.rcorp.mapts.model.geometry.Point
     *
     * @param value The text representation of this point
     * @param haveM Hint whether we have a measure. This is used by other
     * geometries parsing inner points where we only get "1 2 3 4"
     * like strings without the "POINT(" and ")" stuff. If there
     * acutally is a POINTM prefix, this overrides the given value.
     * However, POINT does not set it to false, as they can be
     * contained in measured collections, as in
     * "GEOMETRYCOLLECTIONM(POINT(0 0 0))".
     * @throws SQLException when a SQLException occurs
     */
    constructor(value:String, haveM:Boolean) : this() {
        var value = initSRID(value)
        var haveM = haveM
        if (value.indexOf("POINTM") == 0)
        {
            haveM = true
            value = value.substring(6).trim({ it <= ' ' })
        }
        else if (value.indexOf("POINT") == 0)
        {
            value = value.substring(5).trim({ it <= ' ' })
        }
        val valueNoParans = GeometryTokenizer.removeLeadingAndTrailingStrings(value, "(", ")")
        val tokens = GeometryTokenizer.tokenize(valueNoParans, ' ')
        try
        {
            x = tokens.get(0).toDouble()
            y = tokens.get(1).toDouble()
            haveM = haveM or (tokens.size == 4)
            if ((tokens.size == 3 && !haveM) || (tokens.size == 4))
            {
                z = tokens.get(2).toDouble()
                dimension = 3
            }
            else
            {
                dimension = 2
            }
            if (haveM)
            {
                m = tokens.get(dimension).toDouble()
            }
        }
        catch (e:NumberFormatException) {
            throw NumberFormatException("Error parsing com.rcorp.mapts.model.geometry.Point: " + e.toString())
        }
        isMeasured = haveM
    }
    override fun innerWKT(sb: String): String {
        var result = "$x"
        if (CUTINTS)
            result = cutint(result)
        result = "$result $y"
        if (CUTINTS)
            result = cutint(result)
        if (dimension == 3)
        {
            result = "$result $z"
            if (CUTINTS)
                result = cutint(result)
        }
        if (isMeasured)
        {
            result = "$result $m"
            if (CUTINTS)
                result = cutint(result)
        }
        return result
    }
    fun setX(x:Int) {
        this.x = x.toDouble()
    }
    fun setY(y:Int) {
        this.y = y.toDouble()
    }
    fun setZ(z:Int) {
        this.z = z.toDouble()
    }
    fun distance(other: Point):Double {
        val tx:Double
        val ty:Double
        val tz:Double
        if (this.dimension != other.dimension)
        {
            throw IllegalArgumentException("Points have different dimensions!")
        }
        tx = this.x - other.x
        when (this.dimension) {
            1 -> return sqrt(tx * tx)
            2 -> {
                ty = this.y - other.y
                return sqrt(tx * tx + ty * ty)
            }
            3 -> {
                ty = this.y - other.y
                tz = this.z - other.z
                return sqrt(tx * tx + ty * ty + tz * tz)
            }
            else -> throw IllegalArgumentException("Illegal dimension of com.rcorp.mapts.model.geometry.Point" + this.dimension)
        }
    }

    companion object {
        /* JDK 1.5 Serialization */
        private val serialVersionUID:Long = 0x100
        val CUTINTS = true
        fun hashCode(value:Double):Int {
            val v = value.toLong()
            return (v xor (v.ushr(32))).toInt()
        }
        fun double_equals(a:Double, b:Double):Boolean {
            if (a.isNaN() && b.isNaN())
            {
                return true
            }
            else
            {
                return (a == b)
            }
        }
        private fun cutint(sb:String): String {
            val l = sb.length - 2
            if ((sb[l + 1] == '0') && (sb[l] == '.'))
            {
                return sb.substring(0..l)
            }
            return sb
        }
    }
}