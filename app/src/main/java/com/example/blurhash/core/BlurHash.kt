package com.example.blurhash.core

import android.graphics.Bitmap

object BlurHash {

  private const val DEFAULT_PUNCH = 1f

  /**
   * Clear in-memory calculations.
   * The cache is not big, but will increase when many image sizes are decoded, using [decode].
   * If the app needs memory, it is recommended to clear it.
   */
  fun clearCache() = CoreBlurHash.clearCache()

  /**
   * Returns the average sRGB color for the given [blurHash] in respect to its [punch].
   */
  fun averageColor(
    blurHash: String,
    punch: Float,
  ) = CoreBlurHash.averageColor(blurHash, punch)

  /**
   * Calculates the blur hash from the given [bitmap].
   *
   * [componentX] number of components in the x dimension
   * [componentY] number of components in the y dimension
   */
  fun encode(
    bitmap: Bitmap,
    componentX: Int,
    componentY: Int,
  ): String {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    return CoreBlurHash.encode(
      pixelReader = PixelReaderArgb8888(pixels = pixels, width = width),
      width = width,
      height = height,
      componentX = componentX,
      componentY = componentY,
    )
  }

  /**
   * If [blurHash] is a valid blur hash, the method will return a [Bitmap],
   * with the requested [width] as well as [height].
   *
   * [useCache] to control the caching which will improve performance (with a slight memory impact)
   */
  fun decode(
    blurHash: String,
    width: Int,
    height: Int,
    punch: Float = DEFAULT_PUNCH,
    useCache: Boolean = true,
  ): Bitmap? {
    val pixels = CoreBlurHash.decode(
      blurHash = blurHash,
      pixelWriter = PixelWriterArgb8888(width = width, height = height),
      width = width,
      height = height,
      punch = punch,
      useCache = useCache,
    )

    return pixels?.let { Bitmap.createBitmap(it, width, height, Bitmap.Config.ARGB_8888) }
  }
}