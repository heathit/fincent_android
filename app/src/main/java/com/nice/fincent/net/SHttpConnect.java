package com.nice.fincent.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by stoas0605 on 2016-07-26.
 */
public class SHttpConnect {


    public String httpRequest(
            String url
            , String method
            , int connectTimeout
            , int readTimeout
            , String accept
            , String contentType
            , String cacheControl
            , String params
            , String paramCharset
            ) {

        Log.e("httprequest url", url);

        StringBuffer responseText = new StringBuffer();
        String cookieString;
        HttpURLConnection httpURLConnection;

        try {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoOutput("POST".equals(method));
            httpURLConnection.setDoInput(true);

            httpURLConnection.setConnectTimeout(connectTimeout);
            httpURLConnection.setReadTimeout(readTimeout);

            httpURLConnection.setRequestProperty("Accept", accept);
            httpURLConnection.setRequestProperty("Content-Type", contentType);
            httpURLConnection.setRequestProperty("Cache-Control", cacheControl);
            //if(cookie.length() > 0) httpURLConnection.setRequestProperty("Cookie", cookie);
            cookieString = CookieManager.getInstance().getCookie(com.nice.fincent.define.URL.SERVER);
            if (cookieString != null && cookieString.length() > 0) {
                httpURLConnection.setRequestProperty("Cookie", cookieString);
            }

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(params.getBytes(paramCharset));
            outputStream.flush();
            outputStream.close();

            httpURLConnection.connect();

            InputStream inputStream;
            if(httpURLConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                responseText.append(line);
            }


            Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();

            List<String> cookiesHeader = headerFields.get("Set-Cookie");

            if(cookiesHeader != null) {
                for (String cookie_ : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie_).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie_).get(0).getValue();

                    cookieString = cookieName + "=" + cookieValue;

                    CookieManager.getInstance().setCookie(com.nice.fincent.define.URL.SERVER, cookieString);
                }
            }

            httpURLConnection.disconnect();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseText.toString();
    }

    public static boolean httpConnected(Context context) {

        boolean httpConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
            httpConnected = true;
        } else {
            Toast.makeText(context, "네트워크 연결 상태를 확인하세요.", Toast.LENGTH_SHORT).show();
            httpConnected = false;
            // not connected to the internet
        }

        return httpConnected;
    }
}
