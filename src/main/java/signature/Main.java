package signature;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import org.apache.commons.codec.binary.Hex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class Main {
	public static void main(String[] args) {
		String signature = getSignature(args[0], args[1]);
		BufferedReader in = null;

		try {

			URL url = new URL("https://eu-west-1.aws.webhooks.mongodb-stitch.com/api/client/v2.0/app/stitchappbolduc-xsqhz/service/test/incoming_webhook/getProductSecured?"+args[1]);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");


			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("productId", "val");

			con.setRequestProperty("X-Hook-Signature", "sha256="+signature);
			con.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			con.setRequestProperty("Accept","*/*");

			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

            InputStream inputStream;

            int status = con.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)
                inputStream = con.getErrorStream();
            else
                inputStream = con.getInputStream();

            
            in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}

			System.out.println(prettyJson(content.toString()));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(in !=null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	private static String prettyJson(String uglyJSONString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		return gson.toJson(je);

	}


	private static String getSignature(String secret, String queryparams) {
		try {

			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			return new String(Hex.encodeHex(sha256_HMAC.doFinal(queryparams.getBytes("UTF-8"))));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
