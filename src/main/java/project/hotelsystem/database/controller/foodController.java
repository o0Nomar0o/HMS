package project.hotelsystem.database.controller;

import project.hotelsystem.database.connection.DBConnection;
import project.hotelsystem.database.models.food;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Nomar
 * @author Zin Min Oo
 */

public class foodController {


    public static double getFoodPrice(int id){
        String sql = "SELECT food_price from food WHERE food_id = ?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement psmt = con.prepareStatement(sql)){

            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                return rs.getDouble("food_price");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }
    public static List<food> getAllFood() {
        List<food> allFood = new ArrayList<>();
        String sql = "SELECT f.food_name, f.food_price, f.food_image, f.current_stock, fc.food_category, f.stock_status \n" +
                "FROM food f\n" +
                "JOIN food_category fc ON f.category_id = fc.category_id;";

        try {
            Throwable var29 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                List<food> var10000;

                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        ResultSet rs = psmt.executeQuery();

                        while(true) {
                            if (!rs.next()) {
                                var10000 = allFood;
                                break;
                            }

                            String name = rs.getString(1);
                            double price = rs.getDouble(2);
                            Blob img = rs.getBlob(3);
                            int stock = rs.getInt(4);
                            String cat = rs.getString(5);
                            String status = rs.getString(6);
                            if(status.matches("NIL")) continue;
                            food newFood = new food(name, price, img, stock,cat);
                            allFood.add(newFood);
                        }
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var26) {
                    if (var29 == null) {
                        var29 = var26;
                    } else if (var29 != var26) {
                        var29.addSuppressed(var26);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var29;
                }

                if (con != null) {
                    con.close();
                }

                return var10000;
            } catch (Throwable var27) {
                if (var29 == null) {
                    var29 = var27;
                } else if (var29 != var27) {
                    var29.addSuppressed(var27);
                }

                throw var29;
            }
        } catch (Throwable var28) {
            SQLException e = (SQLException) var28;
            e.printStackTrace();
            return null;
        }
    }

    public static List<food> getFoodByCat(String catName) {
        List<food> allFood = new ArrayList();
        String sql = "SELECT f.food_name, f.food_price, f.food_image, f.current_stock, fc.food_category, f.stock_status \n" +
                "FROM food f \n" +
                "JOIN food_category fc ON f.category_id = fc.category_id\n" +
                "WHERE fc.food_category = ?;";

        try {
            Throwable var30 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                List<food> var10000;
                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        psmt.setString(1, catName);
                        ResultSet rs = psmt.executeQuery();

                        while(true) {
                            if (!rs.next()) {
                                var10000 = allFood;
                                break;
                            }

                            String name = rs.getString(1);
                            double price = rs.getDouble(2);
                            Blob img = rs.getBlob(3);
                            int stock = rs.getInt(4);
                            String category = rs.getString(5);
                            String status = rs.getString(6);
                            if(status.matches("NIL")) continue;
                            food newFood = new food(name, price, img, stock, category);
                            allFood.add(newFood);
                        }
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var27) {
                    if (var30 == null) {
                        var30 = var27;
                    } else if (var30 != var27) {
                        var30.addSuppressed(var27);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var30;
                }

                if (con != null) {
                    con.close();
                }

                return var10000;
            } catch (Throwable var28) {
                if (var30 == null) {
                    var30 = var28;
                } else if (var30 != var28) {
                    var30.addSuppressed(var28);
                }

                throw var30;
            }
        } catch (Throwable var29) {
            SQLException e = (SQLException) var29;
            e.printStackTrace();
            return null;
        }
    }

    public static food getFoodByID(int id) {
        String sql = "SELECT food_name, food_price, food_image, current_stock from `food WHERE food_id=?`";

        try {
            Throwable var29 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                food var10000;
                label330: {
                    try {
                        PreparedStatement psmt = con.prepareStatement(sql);

                        try {
                            psmt.setInt(1, id);
                            ResultSet rs = psmt.executeQuery();
                            if (rs.next()) {
                                String name = rs.getString(1);
                                double price = rs.getDouble(2);
                                Blob img = rs.getBlob(3);
                                int stock = rs.getInt(4);
                                food newFood = new food(name, price, img, stock);
                                var10000 = newFood;
                                break label330;
                            }
                        } finally {
                            if (psmt != null) {
                                psmt.close();
                            }

                        }
                    } catch (Throwable var26) {
                        if (var29 == null) {
                            var29 = var26;
                        } else if (var29 != var26) {
                            var29.addSuppressed(var26);
                        }

                        if (con != null) {
                            con.close();
                        }

                        throw var29;
                    }

                    if (con != null) {
                        con.close();
                    }

                    return null;
                }

                if (con != null) {
                    con.close();
                }

                return var10000;
            } catch (Throwable var27) {
                if (var29 == null) {
                    var29 = var27;
                } else if (var29 != var27) {
                    var29.addSuppressed(var27);
                }

                throw var29;
            }
        } catch (Throwable var28) {
            SQLException e = (SQLException) var28;
            e.printStackTrace();
            return null;
        }
    }

    public static List<food> getAllCategory() {
        List<food> allCat = new ArrayList();
        String sql = "SELECT food_category, category_image from `food_category`";

        try {
            Throwable var26 = null;
            Object var3 = null;

            try {
                Connection con = DBConnection.getConnection();

                List<food> var10000;

                try {
                    PreparedStatement psmt = con.prepareStatement(sql);

                    try {
                        ResultSet rs = psmt.executeQuery();

                        while(true) {
                            if (!rs.next()) {
                                var10000 = allCat;
                                break;
                            }

                            String name = rs.getString(1);
                            Blob img = rs.getBlob(2);
                            food newCat = new food(name, img);
                            allCat.add(newCat);
                        }
                    } finally {
                        if (psmt != null) {
                            psmt.close();
                        }

                    }
                } catch (Throwable var23) {
                    if (var26 == null) {
                        var26 = var23;
                    } else if (var26 != var23) {
                        var26.addSuppressed(var23);
                    }

                    if (con != null) {
                        con.close();
                    }

                    throw var26;
                }

                if (con != null) {
                    con.close();
                }

                return var10000;
            } catch (Throwable var24) {
                if (var26 == null) {
                    var26 = var24;
                } else if (var26 != var24) {
                    var26.addSuppressed(var24);
                }

                throw var26;
            }
        } catch (Throwable var25) {
            SQLException e = (SQLException) var25;
            e.printStackTrace();
            return null;
        }
    }
}
