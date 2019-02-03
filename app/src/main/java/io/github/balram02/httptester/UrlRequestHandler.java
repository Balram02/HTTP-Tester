package io.github.balram02.httptester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import static io.github.balram02.httptester.Constants.CODE_DELETE;
import static io.github.balram02.httptester.Constants.CODE_GET;
import static io.github.balram02.httptester.Constants.CODE_HEAD;
import static io.github.balram02.httptester.Constants.CODE_PATCH;
import static io.github.balram02.httptester.Constants.CODE_POST;
import static io.github.balram02.httptester.Constants.CODE_PUT;


class UrlRequestHandler {

    URL url;
    HttpURLConnection conn;
    StringBuilder string;

    UrlRequestHandler(String link) throws Exception {
        this.url = new URL(link);
        this.conn = (HttpURLConnection) this.url.openConnection();
    }

    void setHeaders(HashMap<String, String> headersMap) {
        if (headersMap != null && !headersMap.isEmpty()) {
            for (HashMap.Entry<String, String> map : headersMap.entrySet()) {
                conn.setRequestProperty(map.getKey(), map.getValue());
            }
        }
    }

    void setRequest(int requestType) throws Exception {
        switch (requestType) {
            case CODE_GET:
                conn.setRequestMethod("GET");
                break;
            case CODE_POST:
                conn.setRequestMethod("POST");
                break;
            case CODE_PUT:
                conn.setRequestMethod("PUT");
                break;
            case CODE_DELETE:
                conn.setRequestMethod("DELETE");
                break;
            case CODE_HEAD:
                conn.setRequestMethod("HEAD");
                break;
            case CODE_PATCH:
                conn.setRequestMethod("PATCH");
                break;
        }

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);

    }

    String getServerResponse() throws Exception {
        StringBuilder stringBuilder;

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } else {
            return "Unknown Response : \n Response Message : \t" + conn.getResponseMessage() + "\n Response Code : \t" + conn.getResponseCode();
        }
    }


    void setBody(HashMap<String, String> bodyMap) throws Exception {
        if (bodyMap != null && !bodyMap.isEmpty()) {
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getEncodedPostRequest(bodyMap));
            writer.flush();
            writer.close();
            os.close();
        }
    }

    private String getEncodedPostRequest(HashMap<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (HashMap.Entry<?, ?> maps : map.entrySet()) {
            if (first)
                first = false;
            else
                sb.append("&");
            sb.append(URLEncoder.encode((String) maps.getKey()));
            sb.append("=");
            sb.append(URLEncoder.encode((String) maps.getValue()));
        }
        return sb.toString();
    }
}
