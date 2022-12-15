package io.mob.resu.reandroidsdk;

import static io.mob.resu.reandroidsdk.AppConstants.baseUrl;

import android.content.BroadcastReceiver;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import io.mob.resu.reandroidsdk.error.ExceptionTracker;
import io.mob.resu.reandroidsdk.error.Log;
import okhttp3.OkHttpClient;


class DataExchanger extends AsyncTask<String, String, String> {

    private final String url;
    private String parameters;
    private ModelResponseData modelResponseData;
    private IResponseListener listener;
    private int requestCode = 0;
    private String requestType = "GET";
    private String CampaignBlastId = "";
    private BroadcastReceiver.PendingResult pendingResult = null;

    public DataExchanger(BroadcastReceiver.PendingResult pendingResult, String url, String parameters, IResponseListener listener, int requestCode) {
        this.pendingResult = pendingResult;
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
        this.requestCode = requestCode;
        requestType = "POST";
    }

    public DataExchanger(String url, String parameters, IResponseListener listener, int requestCode) {
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
        this.requestCode = requestCode;
        requestType = "POST";
    }

    public DataExchanger(String url, String parameters, IResponseListener listener, int requestCode, String campaignBlast) {
        this.url = url;
        this.parameters = parameters;
        this.listener = listener;
        this.requestCode = requestCode;
        this.CampaignBlastId = campaignBlast;
        requestType = "POST";
    }

    public DataExchanger(String url, IResponseListener listener, int requestCode) {
        this.url = url;
        this.listener = listener;
        this.requestCode = requestCode;
        requestType = "GET";
    }

    public DataExchanger(String url) {
        this.url = url;
        this.requestType = "GET";
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            String finalUrl;

            if (url.contains("http"))
                finalUrl = url;
            else {
                finalUrl = baseUrl + java.net.URLEncoder.encode(url, "UTF-8");
            }

            if (AppConstants.SDK) {
                if (requestType.equalsIgnoreCase("POST")) {
                    modelResponseData = sendPost(finalUrl, parameters);
                } else {
                    modelResponseData = new ModelResponseData(getJSON(finalUrl, 3000), 200);
                }
            } else {
                modelResponseData = new ModelResponseData(new JSONObject().toString(), 200);
                Log.e("------********", "********--------");
                Log.e("SDK", "Disabled Please check with SDK's Team");
                Log.e("------********", "********--------");
            }

        } catch (Exception e) {
            //ExceptionTracker.track(e);
            if (listener != null) {
                listener.logOut(requestCode);
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pendingResult != null) {
            pendingResult.finish();
        }

        Log.e("API Request Code: ", ""+requestCode+" Response Code  : "+requestCode +" Response"+modelResponseData.getResponse());

        try {
            if (listener != null) {
                if (modelResponseData != null) {
                    switch (modelResponseData.getResponseCode()) {

                        case 200:
                            listener.onSuccess(modelResponseData.getResponse(), requestCode, CampaignBlastId);
                            break;
                        case 201:
                            listener.onSuccess(modelResponseData.getResponse(), requestCode, CampaignBlastId);
                            break;
                        case 400:
                            if (modelResponseData.getResponse() != null) {
                                listener.showErrorDialog(modelResponseData.getResponse(), requestCode);
                            }
                            break;
                        case 401:
                            listener.logOut(requestCode);
                            break;
                        case 500:
                            listener.showInternalServerErrorDialog(modelResponseData.getResponse(), requestCode);
                            break;
                        default:
                            listener.logOut(requestCode);
                            break;

                    }
                } else {
                    listener.onFailure(new Throwable(), requestCode);
                }
            }


        } catch (Exception e) {
            ExceptionTracker.track(e);
        }

    }

    private String getToken(String message) {
        try {
            String secret = "D9ENR3JNZS657NP";
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = new String(Base64.encode(sha256_HMAC.doFinal(message.getBytes()), Base64.DEFAULT));
            hash = hash + ":" + message;
            String token = new String(Base64.encode(hash.getBytes(), Base64.DEFAULT));

            return token;

        } catch (Exception e) {
            ExceptionTracker.track(e);
        }
        return null;
    }

    private ModelResponseData sendPost(String url, String values) throws Exception {
        //setUpClient();
        try {
            String USER_AGENT = "android";
            String result;
            String token = getToken("" + new Random().nextInt(50) + 1);
            Log.e("Request", "Post parameters : " + values);
            if (!url.contains("IndexInsertAPI") && !url.contains("MobileSDKBlast") && !url.contains("IndexInsertBrandOwnData")) {
                values = getToken(values);
            }
           // Log.e("Request decrypted", "Post parameters : " + values);
            ModelResponseData modelResponseData;
            Log.e("Request", "Post parameters : " + url);
            if (url.contains("bizsdk"))
                Log.e("Environment ", "Biz : ");
            else if (url.contains("teamsdk"))
                Log.e("Environment ", "Team : ");
            else if (url.contains("mobis"))
                Log.e("Environment ", "RUN : ");

            if (AppConstants.socketUrl.contains("mobsoc"))
                Log.e("Socket Environment ", " RUN: ");
            else if (AppConstants.socketUrl.contains("mobi"))
                Log.e("Socket Environment ", " Team: ");


            Log.e("SDK Version ", " : " + AppConstants.SDK_VERSION);

            TrafficStats.setThreadStatsTag((int) Thread.currentThread().getId());
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(30000);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "UTF-8");
            if (!url.contains("IndexInsertAPI") && !url.contains("IndexInsertBrandOwnData"))
                con.setRequestProperty("Token", token);
            con.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
            outputStreamWriter.write(values);
            outputStreamWriter.flush();
            int responseCode = con.getResponseCode();
            //Log.e("Response", "Response Code : " + responseCode);
            BufferedReader in;
            if (200 <= con.getResponseCode() && con.getResponseCode() <= 299) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            in.close();
            result = response.toString();
            modelResponseData = new ModelResponseData(result, responseCode);
           // Log.e("Response", "Response : " + result);
            return modelResponseData;
        } catch (Exception e) {
            return new ModelResponseData("", 400);
        }


    }


    private String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {

            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(10000);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (listener != null) {
                listener.logOut(requestCode);
            }
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private OkHttpClient setUpClient(String privateKey, String certificateContent) {
        try {
            final String SECRET = "secret"; // You may also store this String somewhere more secure.
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            // Get private key
            InputStream privateKeyInputStream = new ByteArrayInputStream(privateKey.getBytes());
            byte[] privateKeyByteArray = new byte[privateKeyInputStream.available()];
            privateKeyInputStream.read(privateKeyByteArray);

            String privateKeyContent = new String(privateKeyByteArray, Charset.defaultCharset())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("PEM Format", "")
                    .replace("Client", "")
                    .replace("Certificate", "")
                    .replace("Private key", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "").trim();

            byte[] rawPrivateKeyByteArray = Base64.decode(privateKeyContent, Base64.DEFAULT);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(rawPrivateKeyByteArray);

            // Get certificate
            InputStream certificateInputStream = new ByteArrayInputStream(certificateContent.getBytes());
            //InputStream certificateInputStream = getResources().openRawResource(R.raw.cer);
            Certificate certificate = certificateFactory.generateCertificate(certificateInputStream);

            // Set up KeyStore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, SECRET.toCharArray());
            keyStore.setKeyEntry("client", keyFactory.generatePrivate(keySpec), SECRET.toCharArray(), new Certificate[]{certificate});
            certificateInputStream.close();

            // Set up Trust Managers
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            // Set up Key Managers
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, SECRET.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            // Obtain SSL Socket Factory
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .build();

            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}

