package project.hotelsystem.util;

import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.ScryptFunction;
import project.hotelsystem.database.models.user;
import project.hotelsystem.database.controller.userController;


public class authenticationManager {

    private static final ScryptFunction scrypt = ScryptFunction.getInstance(128, 12, 4, 64);

    public static boolean Authenticate(String id, String password) {

        if (userController.authLogin(id)) {

            user userDetails = userController.getUserById(id);
            System.out.println(userDetails.getStatus());
            if(userDetails.getStatus().matches("NIL")){
                return false;
            }

            if (userDetails != null) {

                if (Password.check(password, userDetails.getPassword())
                        .addSalt(userDetails.getPrivilege())
                        .with(scrypt)) {
                    authenticationManager am = new authenticationManager();
                    setUsername(userDetails.getUsername());
                    am.setPrivilege(userDetails.getPrivilege());
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean updatePassword(String id , String password){
        Hash hash = Password.hash(password).addSalt(privilege).with(scrypt);
        return userController.updatePassword(id, hash.getResult());

    }

    public static boolean updatePasswordAndUser(String id , String password, String priv,
                                                String name, String email, String ph){
        if(password == null || password.isEmpty()) return userController.updateUser(
                id,name,priv,ph,email
        );

        Hash hash = Password.hash(password).addSalt(privilege).with(scrypt);
        return userController.updateUser(id,name,priv,hash.getResult(),ph,email);

    }

    public static boolean addNewUser(String uid, String username,
                                     String password, String privilege,
                                     String email, String Ph) {

        Hash hash = Password.hash(password).addSalt(privilege).with(scrypt);

        return userController.addUser(new user(uid, username, privilege, hash.getResult(), email,Ph,"offline"));
    }

    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        authenticationManager.username = username;
    }

    private static String privilege;

    private void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public static String getPrivilege() {
        return privilege;
    }

    public static void main(String[] args) {
        addNewUser("A1","Kenma","hotel","admin","hello@gmail.com","+6198113731");
    }
}