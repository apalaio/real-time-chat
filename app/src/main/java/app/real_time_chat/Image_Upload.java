package app.real_time_chat;

/**
 * Created by telis on 05/12/2017.
 */

public class Image_Upload {

    public Image_Upload(String url, String name) {
        this.origin_url = url;
        this.origin_name = name;
    }

    private String origin_name;
    private String origin_url;

    public String getName() {
        return this.origin_name;
    }

    public String getUrl() {
        return this.origin_url;
    }


}
