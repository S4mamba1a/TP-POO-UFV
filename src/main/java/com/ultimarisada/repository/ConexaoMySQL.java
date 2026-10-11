package main.java.com.ultimarisada.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL{
    private static final String URL = "jdbc:mysql://localhost:3306/Palhaco_Instrumento_de_Risada?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "senha_provisoria";

    public static Connection getConexao() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("driver jdbc nao foi encontrado na sua biblioteca do projeto", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}