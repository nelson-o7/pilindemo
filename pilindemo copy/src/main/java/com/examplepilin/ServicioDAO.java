package com.examplepilin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public List<Servicio> obtenerServiciosDesdeBD() {
        List<Servicio> servicios = new ArrayList<>();

        // Configurar la conexión a la base de datos
        String url = "jdbc:postgresql://localhost:5432/servicio_ejempl";
        String usuario = "postgres";
        String contraseña = "1234";

        try (Connection connection = DriverManager.getConnection(url, usuario, contraseña);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT nombre, descripcion, precio FROM servicios")) {

            while (resultSet.next()) {
                // Crear instancias de Servicio con datos de la base de datos
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                double precio = resultSet.getDouble("precio");

                servicios.add(new Servicio(nombre, descripcion, precio));
            }

        } catch (SQLException e) {
            // Manejar la excepción de manera significativa, como lanzar una excepción personalizada o mostrar un mensaje de error
            e.printStackTrace();
        }

        return servicios;
    }
}