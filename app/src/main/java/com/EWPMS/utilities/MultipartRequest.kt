package com.EWPMS.utilities

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.UnsupportedEncodingException

class MultipartRequest(
    url: String,
    private val headers: Map<String, String>? = null,
    private val params: MutableMap<String, String?>,
    private val fileKey: String,
    private val fileData: ByteArray,
    private val listener: Response.Listener<String>,
    private val errorListener: Response.ErrorListener
) : Request<String>(Method.POST, url, errorListener) {

    private val boundary = "multipart_boundary_${System.currentTimeMillis()}"

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    override fun getHeaders(): MutableMap<String, String> {
        return headers?.toMutableMap() ?: super.getHeaders()
    }

    override fun getBody(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val writer = PrintWriter(OutputStreamWriter(outputStream, "UTF-8"), true)

        // Add text parameters
        for ((key, value) in params) {
            writer.append("--$boundary").append("\r\n")
            writer.append("Content-Disposition: form-data; name=\"$key\"")
                .append("\r\n")
                .append("\r\n")
                .append(value)
                .append("\r\n")
        }

        // Add file data
        writer.append("--$boundary").append("\r\n")
        writer.append("Content-Disposition: form-data; name=\"$fileKey\"; filename=\"image.jpg\"")
            .append("\r\n")
        writer.append("Content-Type: image/jpeg").append("\r\n").append("\r\n")
        writer.flush()
        outputStream.write(fileData)
        outputStream.flush()
        writer.append("\r\n")
        writer.flush()

        // End boundary
        writer.append("--$boundary--").append("\r\n")
        writer.flush()

        return outputStream.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        return try {
            Response.success(
                String(response.data, charset(HttpHeaderParser.parseCharset(response.headers, "UTF-8"))),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(p0: String?) {

    }
}
