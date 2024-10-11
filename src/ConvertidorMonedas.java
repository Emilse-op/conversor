import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.text.DecimalFormat;

public class ConvertidorMonedas {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD"; // Reemplaza con la URL de tu API

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        float cantidad, resultado;
        String monedaOrigen, monedaDestino;

        System.out.println("Ingresa la cantidad de dinero a convertir: ");
        cantidad = input.nextFloat();

        // Validar que la cantidad sea positiva
        while (cantidad < 0) {
            System.out.println("Por favor, ingresa un valor positivo: ");
            cantidad = input.nextFloat();
        }

        System.out.println("Ingresa la moneda de origen (MXN, PEN, USD, JPY, EUR): ");
        monedaOrigen = input.next().toUpperCase();

        System.out.println("Ingresa la moneda de destino (MXN, PEN, USD, JPY, EUR): ");
        monedaDestino = input.next().toUpperCase();

        try {
            // Obtener tasas de conversión
            String response = obtenerTasas();
            float tasaOrigen = obtenerTasa(response, monedaOrigen);
            float tasaDestino = obtenerTasa(response, monedaDestino);
            
            // Calcular el resultado
            resultado = (cantidad * tasaDestino) / tasaOrigen;

            // Usar DecimalFormat para un mejor formato
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.println(cantidad + " " + monedaOrigen + " convertidos a " + monedaDestino + ": " + df.format(resultado));
        } catch (Exception e) {
            System.out.println("Error al obtener las tasas de cambio: " + e.getMessage());
        } finally {
            input.close();
        }
    }

    private static String obtenerTasas() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Error en la conexión: " + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static float obtenerTasa(String jsonResponse, String moneda) {
        // Buscar la tasa en la respuesta JSON
        String buscar = "\"" + moneda + "\":";
        int startIndex = jsonResponse.indexOf(buscar) + buscar.length();
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        String tasaString = jsonResponse.substring(startIndex, endIndex).trim();
        return Float.parseFloat(tasaString);
    }
}