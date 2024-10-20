package project.hotelsystem.database.models;

public class user {
    private String uid;
    private String username;
    private String privilege;
    private String password;
    private String email;
    private String phone_no;
    private String status;

    public user(String uid, String username, String privilege, String password, String status) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
        this.password = password;
        this.status = status;
    }

    public user(String uid, String username, String privilege, String password, String email, String phone_no, String status) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
        this.password = password;
        this.email = email;
        this.phone_no = phone_no;
        this.status = status;
    }

    public user(String uid, String username, String privilege, String email, String phone_no, String status) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
        this.email = email;
        this.phone_no = phone_no;
        this.status = status;
    }

    public user(String uid, String username, String privilege, String status) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
        this.status = status;
    }

    public user(String uid, String username, String privilege) {
        this.uid = uid;
        this.username = username;
        this.privilege = privilege;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    @Override
    public String toString() {
        return "user{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", privilege='" + privilege + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}