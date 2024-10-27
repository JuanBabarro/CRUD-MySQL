package Clases;

import com.mysql.cj.jdbc.CallableStatement;
import com.toedter.calendar.JDateChooser;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CUsuario {

    int idSexo;

    public void establecerIdSexo(int idSexo) {
        this.idSexo = idSexo;
    }

    public void MostrarSexoCombo(JComboBox comboSexo) {

        CConexion cn = new CConexion();

        String sql = "Select * from sexo";

        Statement st;

        try {
            st = cn.establecerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);

            comboSexo.removeAllItems();

            while (rs.next()) {
                String nombreSexo = rs.getString("sexo");
                this.establecerIdSexo(rs.getInt("id"));

                comboSexo.addItem(nombreSexo);
                comboSexo.putClientProperty(nombreSexo, idSexo);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar sexo: " + e.toString());
        } finally {
            cn.cerrarConexion();
        }
    }

    public void agregarUsuario(JTextField nombre, JTextField apellido, JComboBox sexo, JTextField edad, JDateChooser fnacimiento, File foto) {

        CConexion cn = new CConexion();

        String consulta = "insert into usuarios (nombres,apellidos,fksexo,edad,fnacimiento,foto) values (?,?,?,?,?,?);";

        try {
            FileInputStream fis = new FileInputStream(foto);

            CallableStatement cs = (CallableStatement) cn.establecerConexion().prepareCall(consulta);
            cs.setString(1, nombre.getText());
            cs.setString(2, apellido.getText());

            int idSexo = (int) sexo.getClientProperty(sexo.getSelectedItem());
            cs.setInt(3, idSexo);

            cs.setInt(4, Integer.parseInt(edad.getText()));

            Date fechaSeleccionada = fnacimiento.getDate();
            java.sql.Date fechaSQL = new java.sql.Date(fechaSeleccionada.getTime());
            cs.setDate(5, fechaSQL);

            cs.setBinaryStream(6, fis, (int) foto.length());

            cs.execute();

            JOptionPane.showMessageDialog(null, "El usuario se guardo correctamente");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error al guardar, error: " + e.toString());
        } finally {

            cn.cerrarConexion();
        }
    }

    public void mostrarUsuarios(JTable tablaTotalUsuarios) {

        CConexion cn = new CConexion();

        DefaultTableModel modelo = new DefaultTableModel();

        String sql = "Select usuarios.id, usuarios.nombres, usuarios.apellidos, sexo.sexo, usuarios.edad, usuarios.fnacimiento, usuarios.foto FROM usuarios INNER JOIN sexo ON usuarios.fksexo = sexo.id";

        modelo.addColumn("id");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Sexo");
        modelo.addColumn("Edad");
        modelo.addColumn("Fnacimiento");
        modelo.addColumn("Foto");

        tablaTotalUsuarios.setModel(modelo);

        try {
            Statement st = cn.establecerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                String id = rs.getString("id");
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                String sexo = rs.getString("sexo");
                String edad = rs.getString("edad");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                java.sql.Date fechaSQL = rs.getDate("fnacimiento");
                String nuevaFecha = sdf.format(fechaSQL);

                byte[] imageBytes = rs.getBytes("foto");
                Image foto = null;

                if (imageBytes != null) {

                    try {
                        ImageIcon img = new ImageIcon(imageBytes);
                        foto = img.getImage();

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.toString());
                    }

                    modelo.addRow(new Object[]{id, nombres, apellidos, sexo, edad, nuevaFecha, foto});
                }

                tablaTotalUsuarios.setModel(modelo);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar usuarios. Error: " + e.toString());
        } finally {
            cn.cerrarConexion();
        }

    }

    public void Seleccionar(JTable totalUsuarios, JTextField id, JTextField nombres, JTextField apellidos, JComboBox sexo, JTextField edad, JDateChooser fnacimiento, JLabel foto) {

        int fila = totalUsuarios.getSelectedRow();

        if (fila >= 0) {
            id.setText(totalUsuarios.getValueAt(fila, 0).toString());
            nombres.setText(totalUsuarios.getValueAt(fila, 1).toString());
            apellidos.setText(totalUsuarios.getValueAt(fila, 2).toString());
            sexo.setSelectedItem(totalUsuarios.getValueAt(fila, 3).toString());
            edad.setText(totalUsuarios.getValueAt(fila, 4).toString());

            String fechaString = totalUsuarios.getValueAt(fila, 5).toString();

            Image imagen = (Image) totalUsuarios.getValueAt(fila, 6);

            ImageIcon origininalIcon = new ImageIcon(imagen);
            int ancho = foto.getWidth();
            int altura = foto.getHeight();

            Image scaledImage = origininalIcon.getImage().getScaledInstance(ancho, altura, Image.SCALE_SMOOTH);

            foto.setIcon(new ImageIcon(scaledImage));

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date fechaDate = (Date) sdf.parse(fechaString);

                fnacimiento.setDate(fechaDate);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            }

        }
    }

    public void modificarUsuario(JTextField id, JTextField nombre, JTextField apellido, JComboBox sexo, JTextField edad, JDateChooser fnacimiento, File foto) {

        CConexion cn = new CConexion();

        String consulta = "UPDATE usuarios SET usuarios.nombres=?, usuarios.apellidos=?, usuarios.fksexo=?, usuarios.edad=?, usuarios.fnacimiento=?, usuarios.foto=? WHERE usuarios.id=?";

        try {
            FileInputStream fis = new FileInputStream(foto);

            CallableStatement cs = (CallableStatement) cn.establecerConexion().prepareCall(consulta);
            
            cs.setString(1, nombre.getText());
            cs.setString(2, apellido.getText());

            int idSexo = (int) sexo.getClientProperty(sexo.getSelectedItem());
            cs.setInt(3, idSexo);
            
            cs.setInt(4, Integer.parseInt(edad.getText()));

            Date fechaSeleccionada = fnacimiento.getDate();
            java.sql.Date fechaSQL = new java.sql.Date(fechaSeleccionada.getTime());
            cs.setDate(5, fechaSQL);

            cs.setBinaryStream(6, fis, (int) foto.length());
            
            cs.setInt(7, Integer.parseInt(id.getText()));
            
            cs.execute();
            
            JOptionPane.showMessageDialog(null, "El usuario se modificó correctamente");
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Error al modificar usuario. Error: "+e.toString());
        }
        
        finally {
            cn.cerrarConexion();
        }
    }
    
    public void eliminarUsuario(JTextField id){
        
        CConexion cn = new CConexion();
        
        String consulta = "DELETE FROM usuarios WHERE usuarios.id =?";
        
        try {
            CallableStatement cs = (CallableStatement) cn.establecerConexion().prepareCall(consulta);
            
            cs.setInt(1, Integer.parseInt(id.getText()));
            cs.execute();
            
            JOptionPane.showMessageDialog(null, "El usuario se eliminó correctamente");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario. Error: "+e.toString());
        }
        finally {
            cn.cerrarConexion();
        }
    }
    
    public void limpiarCampos(JTextField id, JTextField nombre, JTextField apellido, JComboBox sexo, JTextField edad, JDateChooser fnacimiento, JTextField rutafoto, JLabel imagenContenido){
        
       id.setText("");
       nombre.setText("");
       apellido.setText("");
       edad.setText("");
       
       Calendar calendario = Calendar.getInstance();
       fnacimiento.setDate(calendario.getTime());
       
       rutafoto.setText("");
       imagenContenido.setIcon(null);
   
    }

}
