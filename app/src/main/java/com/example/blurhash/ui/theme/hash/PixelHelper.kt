package com.example.blurhash.hash

internal interface PixelReader {
  /** Returns the red components of the pixel at the [x]-[y] coordinate. */
  fun readRed(x: Int, y: Int): Int

  /** Returns the green components of the pixel at the [x]-[y] coordinate. */
  fun readGreen(x: Int, y: Int): Int

  /** Returns the blue components of the pixel at the [x]-[y] coordinate. */
  fun readBlue(x: Int, y: Int): Int
}

internal class PixelReaderArgb8888(
  private val pixels: IntArray,
  private val width: Int,
) : PixelReader {
  override fun readRed(x: Int, y: Int): Int = pixels[y * width + x] shr 16 and 0xff

  override fun readGreen(x: Int, y: Int): Int = pixels[y * width + x] shr 8 and 0xff

  override fun readBlue(x: Int, y: Int): Int = pixels[y * width + x] and 0xff
}

internal interface PixelWriter<T : Any> {
  fun write(x: Int, y: Int, width: Int, red: Int, green: Int, blue: Int)

  fun get(): T
}

internal class PixelWriterArgb8888(
  width: Int,
  height: Int,
) : PixelWriter<IntArray> {
  private val pixels = IntArray(width * height)

  override fun write(
    x: Int,
    y: Int,
    width: Int,
    red: Int,
    green: Int,
    blue: Int,
  ) {
    pixels[x + width * y] = 0xFF000000.toInt() or (red shl 16) or (green shl 8) or blue
  }

  override fun get() = pixels
}