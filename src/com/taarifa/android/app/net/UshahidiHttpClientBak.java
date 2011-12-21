/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.taarifa.android.app.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import com.taarifa.android.app.UshahidiPref;
import com.taarifa.android.app.UshahidiService;
import com.taarifa.android.app.util.Util;

import android.text.TextUtils;
import android.util.Log;

public class UshahidiHttpClientBak {
    
    public static final String TASK = "task";

    public static final String INCIDENT_TITLE = "incident_title";

    public static final String INCIDENT_DESCRIPTION = "incident_description";

    public static final String INCIDENT_DATE = "incident_date";

    public static final String INCIDENT_HOUR = "incident_hour";

    public static final String INCIDENT_MINUTE = "incident_minute";

    public static final String INCIDENT_AMPM = "incident_ampm";

    public static final String INCIDENT_CATEGORY = "incident_category";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String LOCATION_NAME = "location_name";

    public static final String PERSON_FIRST = "person_first";

    public static final String PERSON_LAST = "person_last";

    public static final String PERSON_EMAIL = "person_email";

    public static final String PHOTO = "filename";
    
    private static final int IO_BUFFER_SIZE = 512;

    final public static List<NameValuePair> blankNVPS = new ArrayList<NameValuePair>();

    private static final String CLASS_TAG = UshahidiHttpClient.class.getCanonicalName();

    public static HttpResponse GetURL(String URL) throws IOException {

        UshahidiPref.httpRunning = true;

        try {
            // wrap try around because this constructor can throw Error
            final HttpGet httpget = new HttpGet(URL);
            httpget.addHeader("User-Agent", "Ushahidi-Android/1.0)");

            // Post, check and show the result (not really spectacular, but
            // works):
            HttpResponse response = UshahidiService.httpclient.execute(httpget);
            UshahidiPref.httpRunning = false;

            return response;

        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UshahidiPref.httpRunning = false;
        return null;
    }

    public static HttpResponse PostURL(String URL, List<NameValuePair> data, String Referer)
            throws IOException {
        UshahidiPref.httpRunning = true;
        // Dipo Fix
        try {
            // wrap try around because this constructor can throw Error
            final HttpPost httpost = new HttpPost(URL);
            // org.apache.http.client.methods.
            if (Referer.length() > 0) {
                httpost.addHeader("Referer", Referer);
            }
            if (data != null) {
                try {
                    // NEED THIS NOW TO FIX ERROR 417
                    httpost.getParams().setBooleanParameter("http.protocol.expect-continue", false);
                    httpost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
                } catch (final UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    UshahidiPref.httpRunning = false;
                    return null;
                }
            }

            // Post, check and show the result (not really spectacular, but
            // works):
            try {
                HttpResponse response = UshahidiService.httpclient.execute(httpost);
                UshahidiPref.httpRunning = false;
                return response;

            } catch (final Exception e) {

            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        UshahidiPref.httpRunning = false;
        return null;
    }

    public static HttpResponse PostURL(String URL, List<NameValuePair> data) throws IOException {
        return PostURL(URL, data, "");
    }

    public static boolean PostFileUpload(String URL, HashMap<String, String> params)
            throws IOException {
        ClientHttpRequest req = null;
        Log.d(CLASS_TAG, "PostFileUpload(): upload file to server.");
        try {
            URL url = new URL(URL);
            req = new ClientHttpRequest(url);

            req.setParameter("task", params.get("task"));
            req.setParameter("incident_title", params.get("incident_title"));
            req.setParameter("incident_description", params.get("incident_description"));
            req.setParameter("incident_date", params.get("incident_date"));
            req.setParameter("incident_hour", params.get("incident_hour"));
            req.setParameter("incident_minute", params.get("incident_minute"));
            req.setParameter("incident_ampm", params.get("incident_ampm"));
            req.setParameter("incident_category", params.get("incident_category"));
            req.setParameter("latitude", params.get("latitude"));
            req.setParameter("longitude", params.get("longitude"));
            req.setParameter("location_name", params.get("location_name"));
            req.setParameter("person_first", params.get("person_first"));
            req.setParameter("person_last", params.get("person_last"));
            req.setParameter("person_email", params.get("person_email"));
            Log.i("HTTP Client:", "filename:" + UshahidiPref.savePath + params.get("filename"));
            if (!TextUtils.isEmpty(params.get("filename")))
                req.setParameter("incident_photo[]", new File(params.get("filename")));

            InputStream serverInput = req.post();

            if (Util.extractPayloadJSON(GetText(serverInput))) {

                return true;
            }

        } catch (MalformedURLException ex) {
            Log.d(CLASS_TAG, "PostFileUpload(): MalformedURLException");
            ex.printStackTrace();
            return false;
            // fall through and return false
        }
        return false;
    }

    public static byte[] fetchImage(String address) throws MalformedURLException, IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(address).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 4 * 1024);
            copy(in, out);
            out.flush();

            // need to close stream before return statement
            closeStream(in);
            closeStream(out);

            return dataStream.toByteArray();
        } catch (IOException e) {
            // android.util.Log.e("IO", "Could not load buddy icon: " + this,
            // e);

        } finally {
            closeStream(in);
            closeStream(out);

        }
        return null;

    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     * 
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[4 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * Closes the specified stream.
     * 
     * @param stream The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e("IO", "Could not close stream", e);
            }
        }
    }

    public static String GetText(HttpResponse response) {
        String text = "";
        try {
            text = GetText(response.getEntity().getContent());
        } catch (final Exception ex) {
        }
        return text;
    }

    public static String GetText(InputStream in) {
        String text = "";
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1024);
        final StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (final Exception ex) {
        } finally {
            try {
                in.close();
            } catch (final Exception ex) {
            }
        }
        return text;
    }
}
