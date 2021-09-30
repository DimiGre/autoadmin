package admin;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Downloader {

    public static PhotoInfo.Response getID(String grUrl) {
        grUrl = grUrl.replace("https://vk.com/", "");
        grUrl = grUrl.replace("club", "");
        grUrl = grUrl.replace("public", "");
        return ((PhotoInfo) getJS(Info.VKmethod + "groups.getById?group_id=" + grUrl + "&" + Info.AccessToken, PhotoInfo.class)).response.get(0);

    }

    public static Object getJS(String strURl, Class clazz){
        URL url;
        InputStream is;
        BufferedReader br;
        String line;
        String str = new String();

        try {
            url = new URL(strURl);
            is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                str += line;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //--add-opens=org.example/jGroup$PhotoInfo.response=ALL-UNNAMED
        Gson g = new Gson(); //
        return g.fromJson(str, clazz);
    }

    public static void downloadFiles(String strURL, String strPath, int buffSize) {
        try {
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            InputStream in = null;
            in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream(strPath);
            byte buffer[] = new byte[buffSize];
            int c = in.read(buffer);
            while (c > 0) {
                writer.write(buffer, 0, c);
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public class Spisock{
        public Response response;

        public class Response{
            public List<Item> items;
            public String upload_url;
        }

        public class Item{
            public List<Attachmens> attachments;
            public Likes likes;
        }

        public class Likes{
            public int count;
        }

        public class Attachmens{
            public String type;
            public Photo photo;
        }

        public class Photo{
            public int id;
            public List<Size> sizes;
        }

        public class Size{
            public String url;
        }


    }

    public  class PhotoUploader{
        public String server;
        public String photo;
        public String hash;
    }

    public  class PhotoInfo{
        public List<Response> response;

        public class Response{
            public String owner_id;
            public String id;
            public String name;
        }
    }

}
