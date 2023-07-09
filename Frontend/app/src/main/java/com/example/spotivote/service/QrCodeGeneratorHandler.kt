package com.example.spotivote.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeGeneratorHandler {
    fun generateQrCode(context: Context, data: String): Bitmap? {
        if (data.isEmpty()) {
            Log.d("QR GEN", "generateQrCode empty data")
            //Toast.makeText( context: context, text: "enter some data", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("QR GEN", "generateQrCode $data")

            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                return bmp
            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }
        return null
    }
}