package com.rcorp.mapts.data.geometry

import com.rcorp.mapts.data.geometry.ByteGetter.BinaryByteGetter
import com.rcorp.mapts.data.geometry.ByteGetter.StringByteGetter
import com.rcorp.mapts.model.geometry.*


class BinaryParser {

    /**
     * Parse a hex encoded geometry
     *
     * Is synchronized to protect offset counter. (Unfortunately, Java does not
     * have neither call by reference nor multiple return values.)
     *
     * @param value String containing the data to be parsed
     * @return resulting geometry for the parsed data
     */
    @Synchronized
    fun parse(value: String): Geometry {
        val bytes = StringByteGetter(value)
        return parseGeometry(valueGetterForEndian(bytes))
    }

    /**
     * Parse a binary encoded geometry.
     *
     * Is synchronized to protect offset counter. (Unfortunately, Java does not
     * have neither call by reference nor multiple return values.)
     *
     * @param value byte array containing the data to be parsed
     * @return resulting geometry for the parsed data
     */
    @Synchronized
    fun parse(value: ByteArray): Geometry {
        val bytes = BinaryByteGetter(value)
        return parseGeometry(valueGetterForEndian(bytes))
    }

    /**
     * Parse a geometry starting at offset.
     *
     * @param data ValueGetter with the data to be parsed
     * @return the parsed geometry
     */
    protected fun parseGeometry(data: ValueGetter): Geometry {
        val endian = data.byte // skip and test endian flag
        if (endian != data.endian) {
            throw IllegalArgumentException("Endian inconsistency!")
        }
        val typeword = data.int

        val realtype = typeword and 0x1FFFFFFF // cut off high flag bits

        val haveZ = typeword and -0x80000000 != 0
        val haveM = typeword and 0x40000000 != 0
        val haveS = typeword and 0x20000000 != 0

        var srid = Geometry.UNKNOWN_SRID

        if (haveS) {
            srid = Geometry.parseSRID(data.int)
        }
        val result1: Geometry
        when (realtype) {
            Geometry.POINT -> result1 = parsePoint(data, haveZ, haveM)
            Geometry.LINESTRING -> result1 = parseLineString(data, haveZ, haveM)
            Geometry.POLYGON -> result1 = parsePolygon(data, haveZ, haveM)
            Geometry.MULTIPOINT -> result1 = parseMultiPoint(data)
            Geometry.MULTILINESTRING -> result1 = parseMultiLineString(data)
            Geometry.MULTIPOLYGON -> result1 = parseMultiPolygon(data)
            else -> throw IllegalArgumentException("Unknown com.rcorp.mapts.model.geometry.Geometry Type: $realtype")
        }

        if (srid != Geometry.UNKNOWN_SRID) {
            result1.srid = srid
        }
        return result1
    }

    private fun parsePoint(data: ValueGetter, haveZ: Boolean, haveM: Boolean): Point {
        val X = data.double
        val Y = data.double
        val result: Point
        if (haveZ) {
            val Z = data.double
            result = Point(X, Y, Z)
        } else {
            result = Point(X, Y)
        }

        if (haveM) {
            result.m = data.double
        }

        return result
    }

    /** Parse an Array of "full" Geometries  */
    private fun parseGeometryArray(data: ValueGetter, container: Array<Geometry?>) {
        for (i in container.indices) {
            container[i] = parseGeometry(data)
        }
    }

    /**
     * Parse an Array of "slim" Points (without endianness and type, part of
     * LinearRing and Linestring, but not MultiPoint!
     *
     * @param haveZ
     * @param haveM
     */
    private fun parsePointArray(data: ValueGetter, haveZ: Boolean, haveM: Boolean): Array<Point?> {
        val count = data.int
        val result = arrayOfNulls<Point>(count)
        for (i in 0 until count) {
            result[i] = parsePoint(data, haveZ, haveM)
        }
        return result
    }

    private fun parseMultiPoint(data: ValueGetter): MultiPoint {
        val points = arrayOfNulls<Geometry>(data.int)
        parseGeometryArray(data, points)
        return MultiPoint(points)
    }

    private fun parseLineString(data: ValueGetter, haveZ: Boolean, haveM: Boolean): LineString {
        val points = parsePointArray(data, haveZ, haveM)
        return LineString(points as Array<Geometry?>)
    }

    private fun parseLinearRing(data: ValueGetter, haveZ: Boolean, haveM: Boolean): LinearRing {
        val points = parsePointArray(data, haveZ, haveM)
        return LinearRing(points as Array<Geometry?>)
    }

    private fun parsePolygon(data: ValueGetter, haveZ: Boolean, haveM: Boolean): Polygon {
        val count = data.int
        val rings = arrayOfNulls<Geometry>(count)
        for (i in 0 until count) {
            rings[i] = parseLinearRing(data, haveZ, haveM)
        }
        return Polygon(rings)
    }

    private fun parseMultiLineString(data: ValueGetter): MultiLineString {
        val count = data.int
        val strings = arrayOfNulls<Geometry>(count)
        parseGeometryArray(data, strings)
        return MultiLineString(strings)
    }

    private fun parseMultiPolygon(data: ValueGetter): MultiPolygon {
        val count = data.int
        val polys = arrayOfNulls<Geometry>(count)
        parseGeometryArray(data, polys)
        return MultiPolygon(polys)
    }

    companion object {

        /**
         * Get the appropriate ValueGetter for my endianness
         *
         * @param bytes The appropriate Byte Getter
         *
         * @return the ValueGetter
         */
        fun valueGetterForEndian(bytes: ByteGetter): ValueGetter {
            return if (bytes[0] == ValueGetter.XDR.NUMBER.toInt()) { // XDR
                ValueGetter.XDR(bytes)
            } else if (bytes[0] == ValueGetter.NDR.NUMBER.toInt()) {
                ValueGetter.NDR(bytes)
            } else {
                throw IllegalArgumentException("Unknown Endian type:" + bytes[0])
            }
        }
    }
}