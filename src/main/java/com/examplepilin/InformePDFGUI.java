package com.examplepilin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class InformePDFGUI extends JFrame {

    public InformePDFGUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Informe de Servicios Disponibles");

        JButton generarInformeButton = new JButton("Generar Informe de Servicios Disponibles");
        generarInformeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarInformePDF();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(150, 150, 150)
                                .addComponent(generarInformeButton)
                                .addContainerGap(150, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(100, 100, 100)
                                .addComponent(generarInformeButton)
                                .addContainerGap(100, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

// ...
// ...

private void generarInformePDF() {
    Document document = new Document();

    try {
        PdfWriter.getInstance(document, new FileOutputStream("InformeServicios.pdf"));
        document.open();

        // Crear un párrafo para la imagen y el título
        Paragraph titleAndImage = new Paragraph();

        // Añadir la imagen al párrafo
        String imagePath = "logo.png";
        Image image = Image.getInstance(getClass().getClassLoader().getResource(imagePath));
        image.setAlignment(Element.ALIGN_LEFT);
        image.scaleAbsolute(100, 100); // ajusta el tamaño de la imagen según tus necesidades

        // Añadir espacio entre la imagen y el título
        titleAndImage.add(image);

        // Añadir el título en negrita y un poco más grande al párrafo
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph title = new Paragraph("Informe de Servicios Disponibles", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        titleAndImage.add(title);

        // Añadir el párrafo al documento
        document.add(titleAndImage);

        // Separador
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("---------------------------------------------"));

        // Obtener información de la base de datos y agregarla al PDF
        List<Servicio> servicios = obtenerServiciosDesdeBD();
        for (Servicio servicio : servicios) {
            document.add(new Paragraph("Nombre del Servicio: " + servicio.getNombre()));
            document.add(new Paragraph("Descripción: " + servicio.getDescripcion()));
            document.add(new Paragraph("Precio: " + servicio.getPrecio()));
            document.add(new Paragraph("---------------------------------------------"));
        }

        document.close();
        JOptionPane.showMessageDialog(this, "Informe de Servicios Disponibles generado exitosamente.");
    } catch (DocumentException | FileNotFoundException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al generar el informe de Servicios Disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// ...








    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InformePDFGUI().setVisible(true);
            }
        });
    }

    private static class Servicio {
        private String nombre;
        private String descripcion;
        private String precio;

        public Servicio(String nombre, String descripcion, String precio) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precio = precio;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getPrecio() {
            return precio;
        }
    }

    private List<Servicio> obtenerServiciosDesdeBD() {
        List<Servicio> servicios = new ArrayList<>();

        // Configuración de la conexión a la base de datos
        String url = "jdbc:postgresql://localhost:5432/servicio_ejempl";
        String usuario = "postgres";
        String contraseña = "1234";

        try (Connection connection = DriverManager.getConnection(url, usuario, contraseña)) {
            // Consulta SQL para obtener servicios
            String sql = "SELECT c.nombre AS nombre_cocina, c.descripcion AS desc_cocina, c.precio AS precio_cocina, " +
                    "e.nombre AS nombre_electricidad, e.descripcion AS desc_electricidad, e.precio AS precio_electricidad, " +
                    "p.nombre AS nombre_plomeria, p.descripcion AS desc_plomeria, p.precio AS precio_plomeria " +
                    "FROM cocina c " +
                    "JOIN electricidad e ON c.id_servicio = e.id_servicio " +
                    "JOIN plomeria p ON c.id_servicio = p.id_servicio";

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    // Crear instancias de Servicio con datos de la base de datos
                    servicios.add(new Servicio(
                            resultSet.getString("nombre_cocina"),
                            resultSet.getString("desc_cocina"),
                            resultSet.getString("precio_cocina")
                    ));
                    servicios.add(new Servicio(
                            resultSet.getString("nombre_electricidad"),
                            resultSet.getString("desc_electricidad"),
                            resultSet.getString("precio_electricidad")
                    ));
                    servicios.add(new Servicio(
                            resultSet.getString("nombre_plomeria"),
                            resultSet.getString("desc_plomeria"),
                            resultSet.getString("precio_plomeria")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return servicios;
    }
}
