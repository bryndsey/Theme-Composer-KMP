package dev.bryanlindsey.themecomposer

import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

// This code taken from https://rosettacode.org/wiki/Perlin_noise#Kotlin
object Perlin {
    private val permutation = intArrayOf(
        151, 160, 137,  91,  90,  15, 131,  13, 201,  95,  96,  53, 194, 233,   7, 225,
        140,  36, 103,  30,  69, 142,   8,  99,  37, 240,  21,  10,  23, 190,   6, 148,
        247, 120, 234,  75,   0,  26, 197,  62,  94, 252, 219, 203, 117,  35,  11,  32,
        57, 177,  33,  88, 237, 149,  56,  87, 174,  20, 125, 136, 171, 168,  68, 175,
        74, 165,  71, 134, 139,  48,  27, 166,  77, 146, 158, 231,  83, 111, 229, 122,
        60, 211, 133, 230, 220, 105,  92,  41,  55,  46, 245,  40, 244, 102, 143,  54,
        65,  25,  63, 161,   1, 216,  80,  73, 209,  76, 132, 187, 208,  89,  18, 169,
        200, 196, 135, 130, 116, 188, 159,  86, 164, 100, 109, 198, 173, 186,   3,  64,
        52, 217, 226, 250, 124, 123,   5, 202,  38, 147, 118, 126, 255,  82,  85, 212,
        207, 206,  59, 227,  47,  16,  58,  17, 182, 189,  28,  42, 223, 183, 170, 213,
        119, 248, 152,   2,  44, 154, 163,  70, 221, 153, 101, 155, 167,  43, 172,   9,
        129,  22,  39, 253,  19,  98, 108, 110,  79, 113, 224, 232, 178, 185, 112, 104,
        218, 246,  97, 228, 251,  34, 242, 193, 238, 210, 144,  12, 191, 179, 162, 241,
        81,  51, 145, 235, 249,  14, 239, 107,  49, 192, 214,  31, 181, 199, 106, 157,
        184,  84, 204, 176, 115, 121,  50,  45, 127,   4, 150, 254, 138, 236, 205,  93,
        222, 114,  67,  29,  24,  72, 243, 141, 128, 195,  78,  66, 215,  61, 156, 180
    )

    private val p = IntArray(512) {
        if (it < 256) permutation[it] else permutation[it - 256]
    }

    fun noise(x: Double, y: Double, z: Double): Double {
        // Find unit cube that contains point
        val xi = Math.floor(x).toInt() and 255
        val yi = Math.floor(y).toInt() and 255
        val zi = Math.floor(z).toInt() and 255

        // Find relative x, y, z of point in cube
        val xx = x - Math.floor(x)
        val yy = y - Math.floor(y)
        val zz = z - Math.floor(z)

        // Compute fade curves for each of xx, yy, zz
        val u = fade(xx)
        val v = fade(yy)
        val w = fade(zz)

        // Hash co-ordinates of the 8 cube corners
        // and add blended results from 8 corners of cube

        val a  = p[xi] + yi
        val aa = p[a] + zi
        val ab = p[a + 1] + zi
        val b  = p[xi + 1] + yi
        val ba = p[b] + zi
        val bb = p[b + 1] + zi

        return lerp(w, lerp(v, lerp(u, grad(p[aa], xx, yy, zz),
            grad(p[ba], xx - 1, yy, zz)),
            lerp(u, grad(p[ab], xx, yy - 1, zz),
                grad(p[bb], xx - 1, yy - 1, zz))),
            lerp(v, lerp(u, grad(p[aa + 1], xx, yy, zz - 1),
                grad(p[ba + 1], xx - 1, yy, zz - 1)),
                lerp(u, grad(p[ab + 1], xx, yy - 1, zz - 1),
                    grad(p[bb + 1], xx - 1, yy - 1, zz - 1))))
    }

    private fun fade(t: Double) = t * t * t * (t * (t * 6 - 15) + 10)

    private fun lerp(t: Double, a: Double, b: Double) = a + t * (b - a)

    private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
        // Convert low 4 bits of hash code into 12 gradient directions
        val h = hash and 15
        val u = if (h < 8) x else y
        val v = if (h < 4) y else if (h == 12 || h == 14) x else z
        return (if ((h and 1) == 0) u else -u) +
                (if ((h and 2) == 0) v else -v)
    }
}

fun perlinSequence(
    stepCount: Int,
    stepSize: Double,
    spreadFactor: Double = 1.0,
    mapToMin: Double = 0.0,
    mapToMax: Double = 1.0
): List<Double> {
    val startPoint = Random.nextDouble(128.0)
    val y = Random.nextDouble(256.0)
    val z = Random.nextDouble(256.0)

    return (1..stepCount).map {
        Perlin.noise(
            startPoint + (it * stepSize),
            y,
            z
        )
    }
        .map {
            spreadDouble(it, spreadFactor)
        }
        .map {
            val midpoint = (mapToMin + mapToMax) / 2
            val rangeSize = mapToMax - mapToMin

            // Current midpoint is 0 and range size is 2, so math it appropriately
            (it * rangeSize / 2) + midpoint
        }
}

private fun spreadDouble(value: Double, factor: Double): Double {
    val absoluteValueFactored = abs(value).pow(factor)
    return if (value < 0) {
        -absoluteValueFactored
    } else {
        absoluteValueFactored
    }
}
