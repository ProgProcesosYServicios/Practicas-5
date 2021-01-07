package es.pps.serviciosredseguridad.descargarpagina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Programa que recibe como argumento de la línea de órdenes una
 * URL, y escribe por la salida estándar su contenido.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class DescargarPagina {

	/**
	 * Programa principal de la aplicación.
	 * 
	 * @param args Argumentos del programa. Se espera que el primer
	 * parámetro contenga una URL válida.
	 */
	public static void main(String[] args) {

		String strUrl; // URL del parámetro (cadena)
		URL url;	// Objeto URL 

		// Extraemos la URL a la que conectarnos del argumento al
		// programa.
		if (args.length < 1) {
			//System.err.println("Indica la URL como parámetro.");
			//return;
			strUrl = "http://www.educa2.madrid.org/educamadrid/";
		}
		else
			strUrl = args[0];

		// Intentamos crear un objeto URL a partir de la cadena
		// recibida como parámetro. Si la URL es incorrecta (por
		// ejemplo no empieza por http:// o similar) saltará una
		// excepción de URL mal formada.
		try {
			url = new URL(strUrl);
		} catch (MalformedURLException e) {
			System.err.println("Dirección inválida: " + e.getLocalizedMessage());
			return;
		}

		// Le pedimos a la URL que nos de un stream de bytes con el
		// contenido. Ese stream lo envolvemos en un Reader para que
		// se haga la conversión a char con el formato predefinido
		// (confiemos que el servidor tenga el mismo que nosotros, que
		// quizá sea mucho decir) y luego lo metemos en un BufferedReader
		// para sacar línea a línea.
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		}
		catch (IOException ioe) {
			System.err.println("No pude obtener el canal de lectura: " + ioe.getLocalizedMessage());
			return;
		}

		// Leemos todas las líneas que nos llegan como contenido de la URL
		// y las escribimos por pantalla.
		String inputLine;
		try {
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
		}
		catch(IOException ioe) {
			System.err.println("Error leyendo el contenido de la URL.");
		}

	} // main

} // DescargarPagina