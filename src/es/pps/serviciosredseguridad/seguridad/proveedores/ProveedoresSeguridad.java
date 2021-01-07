package es.pps.serviciosredseguridad.seguridad.proveedores;

import java.security.Provider;
import java.security.Security;

public class ProveedoresSeguridad {

	public static void main(String[] args) {
		
		Provider[] providers = Security.getProviders();

		System.out.println("Encontrados " + providers.length + " proveedores de seguridad.");
		for (Provider p : providers) {
			System.out.println(p.getInfo());
		}

	} // main
	
} // class ProveedoresSeguridad