package p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import t02.pg02.c01.IParque;


public class Parque implements IParque{

	
	//Personas totales en el parque
	private int contadorPersonasTotales; 
	//Personas que han entrado por cada puerta
	private Hashtable<String,Integer> contadoresPersonasPuerta; 
	//Momento incial de la aplicacion
	private long tInicial;
	//Momento en el que entra una persona
	private long tEntrada;
	//Momento en el que sale una persona
	private long tSalida;
	//Calculo del tiempo medio
	private double tMedio;


		
	public Parque() {	
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String,Integer>();
		tInicial = System.currentTimeMillis();
		tMedio = 0;
	}


	@Override
	public synchronized void entrarAlParque(String puerta){		
	
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){ 
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		//Comprobamos si no sobrepasa el limite de personas
		comprobarAntesDeEntrar(puerta);
		
		//Calculamos el tiempo de entrada 
		tEntrada = System.currentTimeMillis();		
							
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
	
		
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		//Comprobamos el invariante
		checkInvariante();
		
		notifyAll();
		
	}
	
	public synchronized void salirDelParque(String puerta){
		// Si no hay salidas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){ 
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		//Comprobamos si no sobrepasa el limite
		comprobarAntesDeSalir(puerta);
		
		//Calcula el tiempo de entrada 
		tSalida = System.currentTimeMillis();		
		
		// Decrementamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);
		
		// Si la puerta se queda a 0, se elimina ¿por qué?
		/*if (contadoresPersonasPuerta.get(puerta) == 0){
			contadoresPersonasPuerta.remove(puerta);
		}*/
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");
					
		//Comprobamos el invariante
		checkInvariante();
		
		notifyAll();
	}
	
	
	private void imprimirInfo (String puerta, String movimiento){
		
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales + " tiempo medio de estancia: "  + obtenerTmedio());
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parque";
		assert contadorPersonasTotales >= 0 : "INV: El parque no puede tener menos de 0 personas";

	}

	protected synchronized void comprobarAntesDeEntrar(String p){	// String p lo pongo yo					
		/*try{				
			assert contadoresPersonasPuerta.get(p) <= 20;				
		}catch(AssertionError e){
			System.out.println("Puerta " + p + " completa");
		}	*/	
		System.out.println("Intento entrar por puerta " + p);
		while ( contadorPersonasTotales > 40 )
			try { wait(); }
			catch(InterruptedException e) {}
		//assert contadorPersonasTotales <= 40 : "Parque completo";
	}

	protected synchronized void comprobarAntesDeSalir(String p){		// String p lo pongo yo
		
			//Comprueba que existe la puerta y que su contador sea mayor que 0
			//assert contadoresPersonasPuerta.get(p) != null && contadoresPersonasPuerta.get(p) > 0 : "Salida cancelada";			
		/*
			System.out.println("Salida cancelada");
			System.out.println("Puerta " + p + " vacia");
			System.out.println(" ");
		}*/
		System.out.println("Intento salir por puerta " + p);
		//assert contadorPersonasTotales > 0 : "Parque vacio";
		//assert contadoresPersonasPuerta.containsKey(p): "Puerta inexistente";
		//assert contadorPersonasTotales > 0 : "Puerta vacia";
		while ( contadorPersonasTotales < 0 )
			try { wait(); }
			catch(InterruptedException e) {}
	}
	
	private double obtenerTmedio() {
		tMedio = (tMedio + (tEntrada - tSalida))/2.0; 
		return tMedio; 
	}


}
