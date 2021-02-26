package com.reactnativepngprocessor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.lang.Exception
import java.net.URL

class PngProcessorModule(val appContext: ReactApplicationContext) : ReactContextBaseJavaModule(appContext) {
    override fun getName(): String {
        return "PngProcessor"
    }

    @ReactMethod
    fun makeTransparent(imageUri: String, promise: Promise) {
        Log.d(name, "HELLO HELLO")
        val srcUri = Uri.parse(imageUri)
        try {
            val bitmap = getBitmapFromOnline(srcUri) ?: run {
                promise.resolve(null)
                return
            }
            val outBitmap = replaceColor(bitmap)
            val file = File(appContext.filesDir, "map.png")
            file.writeBitmap(outBitmap, Bitmap.CompressFormat.PNG, 85)
            promise.resolve(file.toURI().toString())
        } catch (e: IOException) {
            Log.e(name, "Error loading from URI $imageUri")
            promise.reject("Error loading from URI", e)
        }
    }

    private fun getBitmapFromOnline(uri: Uri): Bitmap? {
        val url = URL(uri.toString())
        Log.d(name, "trying to get this $url")
        return try {
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            Log.e(name, "IO exception error: $e")
            null
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val contentResolver = appContext.contentResolver
        val parcelFileDescriptor: ParcelFileDescriptor? =
                contentResolver.openFileDescriptor(uri, "r")
        parcelFileDescriptor ?: return null
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        val options =  BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val image = BitmapFactory
                .decodeFileDescriptor(fileDescriptor, null, options)
        parcelFileDescriptor.close()
        return image
    }

    fun replaceColor(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        Log.d(name, "width: $width height: $height")
        Log.d(name, "bitmap size: ${src.byteCount}")
        Log.d(name, "getting pixels ")
        src.getPixels(pixels, 0, 1 * width, 0, 0, width, height)
        Log.d(name, "got the pixels")
        for (i in pixels.indices) {
            if (pixels[i] == Color.WHITE) {
                pixels[i] = 0;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}


private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}