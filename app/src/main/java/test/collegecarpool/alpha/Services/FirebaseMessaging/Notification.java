package test.collegecarpool.alpha.Services.FirebaseMessaging;

class Notification {

    private String title;
    private String icon;
    private String body;

    Notification(){}

    Notification(String title, String icon, String body){
        this.title = title;
        this.icon = icon;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
