package es.pps.serviciosredseguridad.seguridad.desencriptar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Programa que recibe el nombre de un fichero y lo desencripta usando
 * DES sobre un fichero nuevo cuyo nombre se recibe como segundo
 * parámetro.
 * 
 * La contraseña de encriptación está, por comodidad, cableada en el
 * código fuente.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class Desencriptar {

	/**
	 * Programa principal.
	 * 
	 * @param args Argumentos de la aplicación. El primero es el fichero
	 * fuente y el segundo el destino.
	 */
	public static void main(String[] args) {
		String nombreFuente, nombreDestino;

		if (args.length < 2) {
			System.err.println("Especifica el nombre del fichero fuente y el destino");
			return;
		}
		nombreFuente = args[0];
		nombreDestino = args[1];
		
		// Obtenemos el proveedor de encriptación/desencriptación
		// que implemente el algoritmo DES.
		Cipher encriptador;
		try {
			// Solicitamos una implementación del algoritmo DES, que
			// funcione en modo ECB (Electronic Codebook) y
			// y el estándar de padding PKCS5. Bastaría con poner
			// DES y se usaría el modo y padding predefinido del
			// proveedor que escoja el sistema.
			encriptador = Cipher.getInstance("DES/ECB/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("No tienes ningún proveedor de DES");
			return;
		} catch (NoSuchPaddingException e) {
			System.err.println("No se soporta el padding PKCS5");
			return;
		}

		// Ya tenemos la implementación del algoritmo. Tenemos que
		// inicializarlo en modo "desencriptar" y con una clave
		// (simétrica). Construimos la clave, cableándola en código.
		String strClave = "M1Cl@v3!";
		SecretKeySpec clave = new SecretKeySpec(strClave.getBytes(), "DES");
		try {
			encriptador.init(Cipher.DECRYPT_MODE, clave);
		} catch (InvalidKeyException e) {
			// La clave no es correcta (longitud errónea, por ejemplo).
			System.err.println("Clave incorrecta: " + e.getLocalizedMessage());
			return;
		}

		// Abrimos el fichero fuente (en modo binario). No
		// leeremos de él directamente.
		FileInputStream fichEntrada;
		try {
			fichEntrada = new FileInputStream(nombreFuente);
		} catch (FileNotFoundException e) {
			System.err.println("No pude acceder al fichero " + nombreFuente);
			return;
		}
		// Creamos el fichero destino (también en modo binario).
		FileOutputStream fichSalida;
		try {
			fichSalida = new FileOutputStream(nombreDestino);
		} catch (FileNotFoundException e) {
			System.out.println("No pude crear el fichero " + nombreDestino);
			// Deberíamos cerrar fichEntrada...
			return;
		}
		// Envolvemos el stream de bytes del fichero de entrada en
		// un stream que desencripte antes.
		CipherInputStream streamDesencriptado;
		streamDesencriptado = new CipherInputStream(fichEntrada, encriptador);
		
		// Leemos todo el contenido del fichero y lo enviamos al
		// stream de salida. El encriptador lo codificará y lo enviará
		// al stream del FileOutputStream.
		byte[] buffer = new byte[4096];
		int noOfBytes = 0;
		try {
			while( (noOfBytes = streamDesencriptado.read(buffer)) != -1 )
				fichSalida.write(buffer, 0, noOfBytes);
		}
		catch (IOException e) {
			System.err.println("Error durante la encriptación: " + e.getLocalizedMessage());
		}
		try {
			streamDesencriptado.close();
			fichEntrada.close();
		}
		catch (IOException ioe) {
		}

	} // main

} // Desencriptar