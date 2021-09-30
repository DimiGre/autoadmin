package MVC;

import admin.Downloader;
import admin.Group;
import admin.Info;

public class Model {

    private  View view;
    private Group CurrentGroup;
    private long timeBetweenPosts = 30*60000;


    public Model(View _view){
        view = _view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void addContentMaker(Downloader.PhotoInfo.Response _response){
        CurrentGroup.addNewContentMaker(_response);
    }

    public void setTimeBetweenPosts(long timeBetweenPosts) {
        this.timeBetweenPosts = timeBetweenPosts;
    }

    public long getTimeBetweenPosts(){
        return timeBetweenPosts;
    }

    public void startThread(int id) {
        CurrentGroup = new Group(new Info(id), this);
        Thread thread = new Thread(CurrentGroup);
        thread.start();
    }

    public void removeContMaker(String id) {
        CurrentGroup.removeContMaker(id);
    }
}
