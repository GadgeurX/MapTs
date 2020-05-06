package com.rcorp.mapts.model.geometry

import kotlin.jvm.Transient

abstract class Geometry
/**
 * Constructor for subclasses
 *
 * @param type
 * has to be given by all subclasses.
 */
protected constructor(type: Int) {
    // Properties common to all geometries
    /**
     * The dimensionality of this feature (2,3)
     */
    /**
     * Queries the number of geometric dimensions of this geometry. This does
     * not include measures, as opposed to the server.
     *
     * @return The dimensionality (eg, 2D or 3D) of this geometry.
     */
    var dimension: Int = 0
    /**
     * Do we have a measure (4th dimension)
     */
    /**
     * Returns whether we have a measure
     *
     * @return true if the geometry has a measure, false otherwise
     */
    var isMeasured = false
    /**
     * The OGIS geometry type of this feature. this is final as it never
     * changes, it is bound to the subclass of the instance.
     */
    /**
     * The OGIS geometry type number of this geometry.
     *
     * @return int value representation for the type of this geometry
     */
    var type: Int = 0
    /**
     * The spacial reference system id of this geometry, default is no srid
     */
    /**
     * The OGIS geometry type number of this geometry.
     *
     * @return the SRID of this geometry
     */
    /**
     * Recursively sets the srid on this geometry and all contained
     * subgeometries
     *
     * @param srid the SRID for this geometry
     */
    @Transient
    open var srid = UNKNOWN_SRID
    /**
     * Same as getPoint(0);
     *
     * @return the initial com.rcorp.mapts.model.geometry.Point in this geometry
     */
    abstract val firstPoint: Point
    /**
     * Same as getPoint(numPoints()-1);
     *
     * @return the final com.rcorp.mapts.model.geometry.Point in this geometry
     */
    abstract val lastPoint: Point
    /**
     * Return the Type as String
     *
     * @return String representation for the type of this geometry
     */
    val typeString: String
        get() {
            return getTypeString(this.type)
        }
    /**
     * backwards compatibility method
     *
     * @return String representation of the value for the geometry.
     */
    val value: String
        get() {
            return mediumWKT("")
        }

    init {
        this.type = type
    }

    /**
     * java.lang.Object hashCode implementation
     */
    public override fun hashCode(): Int {
        return dimension or (type * 4) or (srid * 32)
    }

    /**
     * geometry specific equals implementation - only defined for non-null
     * values
     *
     * @param other geometry to compare
     * @return true if equal, false otherwise
     */
    fun equals(other: Geometry): Boolean {
        return ((other != null) && (this.dimension == other.dimension)
                && (this.type == other.type) && (this.srid == other.srid)
                && (this.isMeasured == other.isMeasured)
                && other::class == this::class
                && this.equalsintern(other))
    }

    /**
     * Whether test coordinates for geometry - subclass specific code
     *
     * Implementors can assume that dimensin, type, srid and haveMeasure are
     * equal, other != null and other is the same subclass.
     *
     * @param other geometry to compare
     * @return true if equal, false otherwise
     */
    abstract fun equalsintern(other: Geometry?): Boolean

    /**
     * Return the number of Points of the geometry
     *
     * @return number of points in the geometry
     */
    abstract fun numPoints(): Int

    /**
     * Get the nth com.rcorp.mapts.model.geometry.Point of the geometry
     *
     * @param n the index of the point, from 0 to numPoints()-1;
     * @return nth point in the geometry
     * @throws ArrayIndexOutOfBoundsException in case of an emtpy geometry or bad index.
     */
    abstract fun getPoint(n: Int): Point

    public override fun toString(): String {
        return if (srid != UNKNOWN_SRID) {
            outerWKT("SRID=${srid};", true)
        } else
            outerWKT("", true)
    }

    /**
     * Render the WKT version of this com.rcorp.mapts.model.geometry.Geometry (without SRID) into the given
     * StringBuffer.
     *
     * @param sb StringBuffer to render into
     * @param putM flag to indicate if the M character should be used.
     */
    fun outerWKT(sb: String, putM: Boolean = true): String {
        val result = "$sb$typeString"
        if (putM && isMeasured && dimension == 2) {
            return mediumWKT("${result}M")
        }
        return mediumWKT(result)
    }

    /**
     * Render the WKT without the type name, but including the brackets into the
     * StringBuffer
     *
     * @param sb StringBuffer to render into
     */
    open fun mediumWKT(sb: String): String {
        return "(${innerWKT(sb)})"
    }

    /**
     * Render the "inner" part of the WKT (inside the brackets) into the
     * StringBuffer.
     *
     * @param SB StringBuffer to render into
     */
    abstract fun innerWKT(SB: String): String

    /**
     * Do some internal consistency checks on the geometry.
     *
     * Currently, all Geometries must have a valid dimension (2 or 3) and a
     * valid type. 2-dimensional Points must have Z=0.0, as well as non-measured
     * Points must have m=0.0. Composed geometries must have all equal SRID,
     * dimensionality and measures, as well as that they do not contain NULL or
     * inconsistent subgeometries.
     *
     * BinaryParser and WKTParser should only generate consistent geometries.
     * BinaryWriter may produce invalid results on inconsistent geometries.
     *
     * @return true if all checks are passed.
     */
    open fun checkConsistency(): Boolean {
        return (dimension >= 2 && dimension <= 3) && (type >= 0 && type <= 7)
    }

    /**
     * Splits the SRID=4711; part of a EWKT rep if present and sets the srid.
     *
     * @param value String value to extract the SRID from
     * @return value without the SRID=4711; part
     */
    protected fun initSRID(value: String): String {
        val value = value.trim({ it <= ' ' })
        if (value.startsWith("SRID=")) {
            val index = value.indexOf(';', 5) // sridprefix length is 5
            if (index == -1) {
                throw IllegalArgumentException(
                        "Error parsing com.rcorp.mapts.model.geometry.Geometry - SRID not delimited with ';' ")
            } else {
                this.srid = (value.substring(5, index)).toInt()
                return value.substring(index + 1).trim({ it <= ' ' })
            }
        } else {
            return value
        }
    }

    companion object {
        /* JDK 1.5 Serialization */
        private val serialVersionUID: Long = 0x100
        // OpenGIS com.rcorp.mapts.model.geometry.Geometry types as defined in the OGC WKB Spec
        // (May we replace this with an ENUM as soon as JDK 1.5
        // has gained widespread usage?)
        /** Fake type for linear ring */
        val LINEARRING = 0
        /**
         * The OGIS geometry type number for points.
         */
        val POINT = 1
        /**
         * The OGIS geometry type number for lines.
         */
        val LINESTRING = 2
        /**
         * The OGIS geometry type number for polygons.
         */
        val POLYGON = 3
        /**
         * The OGIS geometry type number for aggregate points.
         */
        val MULTIPOINT = 4
        /**
         * The OGIS geometry type number for aggregate lines.
         */
        val MULTILINESTRING = 5
        /**
         * The OGIS geometry type number for aggregate polygons.
         */
        val MULTIPOLYGON = 6
        /**
         * The OGIS geometry type number for feature collections.
         */
        val GEOMETRYCOLLECTION = 7
        val ALLTYPES = arrayOf<String>("", // internally used LinearRing does not have any text in front of
                // it
                "POINT", "LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION")

        /**
         * The Text representations of the geometry types
         *
         * @param type int value of the type to lookup
         * @return String reprentation of the type.
         */
        fun getTypeString(type: Int): String {
            if (type >= 0 && type <= 7) {
                return ALLTYPES[type]
            } else {
                throw IllegalArgumentException("Unknown com.rcorp.mapts.model.geometry.Geometry type" + type)
            }
        }

        /**
         * Official UNKNOWN srid value
         */
        val UNKNOWN_SRID = 0

        /**
         * Parse a SRID value, anything {@code <= 0} is unknown
         *
         * @param srid the SRID to parse
         * @return parsed SRID value
         */
        fun parseSRID(srid: Int): Int {
            if (srid < 0) {
                 return 0
            }
            return srid
        }

    }
}