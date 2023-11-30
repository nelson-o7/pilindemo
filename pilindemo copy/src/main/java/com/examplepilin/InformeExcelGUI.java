package com.examplepilin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InformeExcelGUI extends JFrame {

    public InformeExcelGUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Informe de Servicios Disponibles");

        JButton generarInformeButton = new JButton("Generar Informe de Servicios Disponibles");
        generarInformeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarInformeExcel();
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

    private void generarInformeExcel() {
        Workbook workbook = new XSSFWorkbook();

        try {
            // Crear una nueva hoja de trabajo (sheet) en el libro (workbook)
            Sheet sheet = workbook.createSheet("Servicios Disponibles");

            // Crear la primera fila que contendrá los encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre del Servicio", "Descripción", "Precio"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Obtener información de la base de datos y agregarla al archivo Excel
            List<Servicio> servicios = obtenerServiciosDesdeBD();
            int rowNum = 1;
            for (Servicio servicio : servicios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(servicio.getNombre());
                row.createCell(1).setCellValue(servicio.getDescripcion());
                row.createCell(2).setCellValue(servicio.getPrecio());
            }

            // Guardar el libro en un archivo
            try (FileOutputStream fileOut = new FileOutputStream("InformeServicios.xlsx")) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(this, "Informe de Servicios Disponibles generado exitosamente.");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el informe de Servicios Disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener los servicios desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InformeExcelGUI().setVisible(true);
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

    private List<Servicio> obtenerServiciosDesdeBD() throws SQLException {
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
                }
        
                return servicios;
            }
        }
        
