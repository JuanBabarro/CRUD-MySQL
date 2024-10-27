package Clases;

import com.mysql.cj.jdbc.CallableStatement;
import com.toedter.calendar.JDateChooser;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CConexion {

    Connection conectar = null;

    String usuario = "root";
    String contraseña = "12345";
    String bd = "bdusuarios";
    String ip = "localhost";
    String puerto = "3306";
    String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd;

    public Connection establecerConexion() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contraseña);

            //JOptionPane.showMessageDialog(null, "Se conectó a la BD correctamente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en la Conexion con la BD");
        }

        return conectar;
    }

    public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
            }
            //JOptionPane.showMessageDialog(null, "Conexión cerrada");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo cerrar la conexión");

        }
    }

}
