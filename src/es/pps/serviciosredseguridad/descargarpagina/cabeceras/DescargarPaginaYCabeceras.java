package es.pps.serviciosredseguridad.descargarpagina.cabeceras;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Programa que recibe como argumento de la línea de órdenes una
 * URL, y escribe por la salida estándar su contenido.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class DescargarPaginaYCabeceras {

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
			System.err.println("Indica la URL como parámetro.");
			return;
		}
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

		// Construímos explícitamente una conexión a una URL, en
		// lugar de utilizar URL.openStream() que nos oculta todos los
		// detalles y nos quita posibilidades.
		// Creamos una URLConnection a partir de la URL.
		URLConnection urlCon;
		try {
			urlCon = url.openConnection();
		}
		catch (IOException ioe) {
			System.err.println("Imposible 'conectar': " + ioe.getLocalizedMessage());
			return;
		}

		// Antes de descargar el contenido, mostramos las cabeceras
		// HTTP que se han recibido en la conexión.
		if (urlCon instanceof HttpURLConnection) {
			// La URL tenía el protocolo http (o https), por lo que
			// tenemos un código de respuesta. Lo mostramos.
			HttpURLConnection huc;
			huc = (HttpURLConnection) urlCon;
			try {
				System.out.println("Respuesta: " + huc.getResponseCode() +
				                   " (" + huc.getResponseMessage() + ")\n");
			}
			catch(IOException ioe) {
			}
		} // if (urlCon instanceof HttpURLConnection)

		int i = 0;
		while (urlCon.getHeaderField(i) != null) {
			if (urlCon.getHeaderFieldKey(i) != null) {
				System.out.print(urlCon.getHeaderFieldKey(i));
				System.out.print(": ");
				System.out.println(urlCon.getHeaderField(i));
			}
			++i;
		} // while
		System.out.println("-----------------------------------");
		
		// Vamos con el contenido. El stream que conseguíamos antes
		// lo proporciona realmente la URLConnection. Se lo pedimos
		// a ella, que nos dará un stream de bytes con el contenido.
		// Como antes, ese stream lo envolvemos en un Reader para que
		// se haga la conversión a char con el formato predefinido
		// (confiemos que el servidor tenga el mismo que nosotros, que
		// quizá sea mucho decir) y luego lo metemos en un BufferedReader
		// para sacar linea a linea.
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
		}
		catch (IOException ioe) {
			System.err.println("No pude obtener el canal de lectura");
			return;
		}
		
		// Leemos todas las líneas que nos llega como contenido de la URL
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

} // DescargarPaginaYCabeceras