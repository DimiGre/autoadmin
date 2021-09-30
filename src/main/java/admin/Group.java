package admin;

import com.google.gson.Gson;
import javafx.application.Platform;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Group implements Runnable {
    private Info info;
    private String wayStr;
    private MVC.Model model;
    private List<String> contentMakers;
    private ArrayList<ArrayList<admin.Pair<Integer, Integer>>> listOfTopOfPosts;

    @Override
    public void run() {
        System.out.println(info.MyGroupId + " thread started");
        WakeUp(wayStr);
        while (true) {
            if (contentMakers.size() > 0) {
                newPosts();
                makePost();
                saveToFile();
                try {
                    Thread.sleep(model.getTimeBetweenPosts());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public Group(Info _info, MVC.Model _model) {
        info = _info;
        model = _model;
        wayStr = System.getProperty("user.dir") + "\\" + info.MyGroupId;
        contentMakers = new ArrayList<>();
        listOfTopOfPosts = new ArrayList<>();
        System.out.println(info.MyGroupId + " created");
    }

    public void addNewContentMaker(admin.Downloader.PhotoInfo.Response response) {
        try {
            FileWriter fileWriter = new FileWriter(wayStr + "\\data.txt", StandardCharsets.UTF_8, true);
            fileWriter.write(response.id + " " + new String(response.name.getBytes("windows-1251"), "UTF-8") + '\n');
            fileWriter.flush();
            contentMakers.add(response.id);
            listOfTopOfPosts.add(new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WakeUp(String way) {
        try {
            if (Files.exists(Path.of(way)) == false) Files.createDirectory(Path.of(way));
            if (Files.exists(Path.of(way + "\\data.txt")) == false) Files.createFile(Path.of(way + "\\data.txt"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            File file = new File(wayStr + "\\data.txt");
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
            for (int i = 0; sc.hasNext(); i++) {
                String ID = sc.next();
                String name = sc.nextLine();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        model.getView().addContentMaker(ID, name);
                    }
                });
                contentMakers.add(ID);
                listOfTopOfPosts.add(new ArrayList<>());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        int len = contentMakers.size();

        for (int i = 0; i < len; i++) {
            try {
                String ID = contentMakers.get(i);
                File file = new File(wayStr + "\\" + ID + "\\data.txt");
                Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
                while (sc.hasNext()) {
                    Integer first = sc.nextInt();
                    Integer second = sc.nextInt();
                    listOfTopOfPosts.get(i).add(new admin.Pair<>(first, second));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void newPosts() {
        try {
            int len = contentMakers.size();
            for (int i = 0; i < len; i++) {
                String ContMakerID = contentMakers.get(i);
                ArrayList<admin.Pair<Integer, Integer>> topOfPosts = listOfTopOfPosts.get(i);
                try {
                    if (Files.exists(Path.of(wayStr + "\\" + ContMakerID)) == false)
                        Files.createDirectory(Path.of(wayStr + "\\" + ContMakerID));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                String str = Info.VKmethod + "wall.get?owner_id=-" + Integer.parseInt(ContMakerID.substring(1)) + "&domain=216910860&count=100&offset=0&" + Info.AccessToken;
                admin.Downloader.Spisock res = (admin.Downloader.Spisock) admin.Downloader.getJS(str, admin.Downloader.Spisock.class);

                System.out.println(str);
                ListIterator iterator = res.response.items.listIterator();
                while (iterator.hasNext()) {
                    admin.Downloader.Spisock.Item ite = (admin.Downloader.Spisock.Item) iterator.next();
                    if (ite.attachments != null && ite.attachments.size() == 1) {
                        //ListIterator attIter = ite.attachments.listIterator();
                        ///while (attIter.hasNext()) {
                        admin.Downloader.Spisock.Attachmens att; //= ((Downloader.Spisock.Attachmens) attIter.next());
                        att = ite.attachments.get(0);
                        if (att.photo != null) {
                            if (!Files.exists(Path.of(wayStr + "\\" + att.photo.id))) {
                                String phUrl;
                                phUrl = att.photo.sizes.get(att.photo.sizes.size() - 1).url;
                                String phWay = wayStr + "\\" + ContMakerID + "\\" + att.photo.id + ".jpg";
                                File file = new File(phWay);
                                try {
                                    if (file.createNewFile()) {
                                        topOfPosts.add(new admin.Pair<Integer, Integer>(att.photo.id, ite.likes.count));
                                        admin.Downloader.downloadFiles(phUrl, phWay, 256);
                                        System.out.println(phUrl);
                                    }
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
                Collections.sort(topOfPosts);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            newPosts();
        }
    }

    public void makePost() {
        try {
            Random random = new Random();

            int numb = random.nextInt(contentMakers.size());
            for(int i = 0; listOfTopOfPosts.get(numb).get(0).getSecond() == -1; i++) {
                numb = random.nextInt(contentMakers.size());
            }
            admin.Pair<Integer, Integer> popular = listOfTopOfPosts.get(numb).get(0);
            String ContMakerID = contentMakers.get(numb);

            String uploadServer = ((admin.Downloader.Spisock) admin.Downloader.getJS(Info.VKmethod + "photos.getWallUploadServer?group_id=" + info.MyGroupId + "&" + Info.AccessToken, admin.Downloader.Spisock.class)).response.upload_url;
            System.out.println(uploadServer);

            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();

                HttpPost uploadFile = new HttpPost(uploadServer);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                File photo = new File(wayStr + "\\" + ContMakerID + "\\" + popular.getFirst() + ".jpg");
                builder.addBinaryBody("photo", photo);

                HttpEntity multipart = builder.build();
                uploadFile.setEntity(multipart);

                CloseableHttpResponse response = httpClient.execute(uploadFile);

                HttpEntity responseEntity = response.getEntity();

                if (responseEntity != null) {

                    InputStreamReader reader = new InputStreamReader(responseEntity.getContent());
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    StringBuilder sb = new StringBuilder();
                    String aline = "";
                    while ((aline = bufferedReader.readLine()) != null) {
                        sb.append(aline);

                    }
                    Gson gson = new Gson();
                    admin.Downloader.PhotoUploader photoUploader = gson.fromJson(sb.toString(), admin.Downloader.PhotoUploader.class);
                    admin.Downloader.PhotoInfo photoInfo = ((admin.Downloader.PhotoInfo) admin.Downloader.getJS(Info.VKmethod + "photos.saveWallPhoto?group_id=" + info.MyGroupId + "&server=" + photoUploader.server + "&photo=" + photoUploader.photo + "&hash=" + photoUploader.hash + "&" + info.MyGroupId + "&" + Info.AccessToken, admin.Downloader.PhotoInfo.class));
                    String str = Info.VKmethod + "wall.post?owner_id=-" + info.MyGroupId + "&from_group=1&" + Info.AccessToken + "&attachments=photo" + photoInfo.response.get(0).owner_id + "_" + photoInfo.response.get(0).id;
                    URL url = new URL(str);
                    url.openStream();
                    popular.setSecond(-1);
                }


                httpClient.close();
                response.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            makePost();
        }
    }

    public void saveToFile() {
        int size = contentMakers.size();
        for (int i = 0; i < size; i++) {
            try {
                String ID = contentMakers.get(i);
                ArrayList<admin.Pair<Integer, Integer>> topOfPosts = listOfTopOfPosts.get(i);
                Collections.sort(topOfPosts);
                File data = new File(wayStr + "\\" + ID + "\\data.txt");
                FileWriter dataWrite = new FileWriter(data, StandardCharsets.UTF_8);
                for (admin.Pair post : topOfPosts) {
                    dataWrite.write(post.getFirst() + " " + post.getSecond() + '\n');
                }
                dataWrite.flush();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public void loadFromFile() {

    }

    public void removeContMaker(String id) {
        for (int i = 0; i < contentMakers.size(); i++)
            if (contentMakers.get(i).equals(id)) {
                contentMakers.remove(i);
                listOfTopOfPosts.remove(i);
 /*               try {
                    Files.deleteIfExists(Path.of(wayStr + "\\" + id));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;
            }

 /*       ArrayList<String> names = new ArrayList<>();
        try {
            File file = new File(wayStr + "\\data.txt");
            Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
            for (int i = 0; sc.hasNext(); i++) {
                if(!(sc.next().equals(id))) names.add(sc.next());
                else sc.next();
            }
        }catch(IOException e){
                e.printStackTrace();
            }

        int size = contentMakers.size();
            try {
                File data = new File(wayStr + "\\" + info.MyGroupId + "\\data.txt");
                FileWriter dataWrite = new FileWriter(data);
                for (int i = 0; i < size; i++) {
                        dataWrite.write(contentMakers.get(i) + " " + names.get(i));
                }
                    dataWrite.flush();
            } catch (Exception e) {
                e.getMessage();
            }*/
        }
    }

