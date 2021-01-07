package es.pps.serviciosredseguridad.descargarimagenes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Clase que hereda del interfaz runnable para
 * ser lanzada en otra hebra. En el constructor
 * recibe una URL ya creada. El método run() se
 * conecta a esa URL y se descarga el contenido, y lo
 * escribe en un fichero en el directorio actual.
 * El nombre del fichero lo extrae de la URL, quedándose
 * con el nombre del recurso.
 * 
 * @author Pedro Pablo Gómez Martín
 */
class GrabarURL implements Runnable {

	/**
	 * Constructor.
	 * 
	 * @param url URL de donde descargar el contenido. 
	 */
	GrabarURL(URL url) {

		_url = url;
		
	} // Constructor

	//--------------------------------------------------

	/**
	 * Método run() para ser ejecutado en otra hebra.
	 * Descarga el contenido de la URL y lo graba en un
	 * fichero.
	 */
	@Override
	public void run() {

		// Mensaje de que vamos a descargarnos el fichero...
		System.out.println("Descargando " + _url.toString());
		
		// Obtenemos el nombre del fichero. Lo extraemos del "fichero"
		// de la URL. Pero el fichero podría tener ruta
		// (http://www.educa.madrid.org/images/logos/madrid.png nos
		// dejaría /images/logos/madrid.png). Quitamos todas las
		// rutas cortando desde la última '/'
		String filename = _url.getFile();
		filename = filename.substring(filename.lastIndexOf('/') + 1);
		if ((filename == null) || filename.isEmpty())
			filename="noname";

		// Estamos preparados. Obtenemos el streams de bytes de
		// la conexión, creamos un fichero (de bytes) con el
		// nombre deducido, y leemos del stream en un buffer
		// y lo volcamos al fichero hasta que no podamos más.
		try {
			InputStream is = _url.openStream();
			FileOutputStream fos;
			fos = new FileOutputStream(filename);
			byte[] b = new byte[4096];
			int noOfBytes = 0;
			while( (noOfBytes = is.read(b)) != -1 )
				fos.write(b, 0, noOfBytes);
			
			// Ya lo hemos volcado todo. Cerramos.
			is.close();
			fos.close();
		}
		catch(IOException ioe) {
			System.out.println("Error descargando el fichero " +
			                   _url.toString() + ": " + ioe);
		}
		System.out.println("Fin descarga " + _url.toString());

	} // run

	//--------------------------------------------------

	/**
	 * URL recibida en el constructor.
	 */
	protected URL _url;

} // class DownloadImage

//--------------------------------------------------
//--------------------------------------------------

/**
 * Clase principal de la aplicación. Recibe como parámetro
 * una URL que asume que será un HTML, y lo procesa con
 * Jsoup para obtener los elementos de tipo <img> y
 * descargarse todas las imágenes de la página y grabarlas
 * en el disco duro en el directorio actual. Ten en cuenta
 * que la página podría hacer referencia a una imagen
 * logos/madrid.png y a mapas/madrid.png. En ese caso
 * ambas descargas irían a madrid.png y el resultado sería
 * incierto... No me he preocupado por mejorar estas cosas.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class DescargarImagenes {

	/**
	 * Programa principal.
	 * 
	 * @param args Argumentos enla línea de órdenes.
	 */
	public static void main(String[] args) {

		String sourceUrlStr;

		// Extraemos la URL a la que conectarnos del argumento al
		// programa.
		if (args.length < 1) {
			System.err.println("Indica la URL como parámetro.");
			return;
		}
		sourceUrlStr = args[0];

		try {
			// Nos aseguramos de que la URL que nos han dado es
			// correcta.
			new URL(sourceUrlStr);
		}
		catch(MalformedURLException mue) {
			System.out.println("URL no válida");
			return;
		}

		// Creamos una conexión HTTP. Es similar en esencia a
		// usar URL y URLConnection, pero con las clases
		// proporcionadas por Jsoup.
		Connection con = Jsoup.connect(sourceUrlStr);

		// Obtenemos el documento de la conexión. Será el
		// HTML.
		Document doc;
		try {
			doc = con.get();
		} catch (IOException e) {
			System.out.println("No pude obtener el documento: " + e.getLocalizedMessage());
			return;
		}

		// En breve necesitaremos la URL del documento para construir
		// la URL completa de las imágenes que usen URLs relativas.
		// En principio, la URL del documento es sourceUrl, pero si
		// la respuesta del navegador fue, por ejemplo, un mensaje 302
		// que nos redirigió a algún otro sitio, Jsoup habrá hecho
		// la redirección por nosotros y la URL habrá cambiado. Para
		// evitar problemas de referencias mal construídas, obtenemos
		// la URL real desde la que nos hemos descargado la página.
		URL documentUrl;
		try {
			documentUrl = new URL(doc.baseUri());
		} catch (MalformedURLException e1) {
			System.out.println("No pude obtener el documento.");
			return;
		}
		// Usando "sintáxis CSS" (también usada en jQuery)
		// filtramos los elementos del HTML para quedarnos con
		// aquellos que sean <img>. Podríamos poner condiciones
		// más sofisticadas, como imágenes que tengan un identificador
		// o una clase específica, y cosas así.
		Elements images = doc.select("img");

		// Recorremos todos los elementos (imágenes).
		for (Element e : images) {
			if (e.attr("src") != null) {
				// Si la imagen tiene atributo src, entonces tiene
				// un fuente, que es el que nos descargaremos.
				// Creamos una URL con la localización de la imagen.
				// Combinamos la URL del atributo src del <img> que
				// estamos procesando con la URL del documento.
				URL url;
				try {
					url = new URL(documentUrl, e.attr("src"));
				}
				catch(MalformedURLException mur) {
					System.out.println("URL de imagen inválida: " + mur.getLocalizedMessage());
					continue;
				}
				// Creamos una nueva hebra que se encargará de
				// la descarga.
				GrabarURL di = new GrabarURL(url);
				new Thread(di).start();
			} // if (hay atributo src)
		} // for
		
		// Ya tenemos todas las hebras en marcha descargandose imágenes.
		// El main no tiene nada más que hacer.

	} // main
	
} // class DescargarImagenes