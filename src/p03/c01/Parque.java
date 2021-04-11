package p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import p03.c01.IParque;


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
		
	public Parque() {	//Constructor del parque
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String,Integer>();
		tInicial = System.currentTimeMillis();
		tMedio = 0;
	}

	@Override
	public synchronized void entrarAlParque(String puerta){		
		
		//Comprobamos si no sobrepasa el limite de personas
		comprobarAntesDeEntrar();		
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){ 
			contadoresPersonasPuerta.put(puerta, 0);
		}	
		
		//Calculamos el tiempo de entrada 
		tEntrada = System.currentTimeMillis();									
		
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);		
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");		
		
		//Notificamos al resto de hilos
		notifyAll();		
		
		//Comprobamos el invariante
		checkInvariante();
	}
	
	public synchronized void salirDelParque(String puerta){
		
		//Comprobamos si no sobrepasa el limite
		comprobarAntesDeSalir();		
		// Si no hay salidas por esa puerta, inicializamos
		
		if (contadoresPersonasPuerta.get(puerta) == null){ 
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		//Calculamos el tiempo de entrada 
		tSalida = System.currentTimeMillis();				
		
		// Decrementamos el contador total y el individual
		contadorPersonasTotales--;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)-1);		
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");
		
		//Notificamos al resto de hilos
		notifyAll();		
		
		//Comprobamos el invariante
		checkInvariante();
	}
		
	private void imprimirInfo (String puerta, String movimiento){	
		
		//Imprimimos el movimiento y las personas que hay en el parque
		System.out.println(movimiento + " por puerta " + puerta);
		if (movimiento =="Entrada")
			System.out.println("--> Personas en el parque " + contadorPersonasTotales);
		else //Imprimimos el tiempo medio a la salida
			System.out.println("--> Personas en el parque " + contadorPersonasTotales + " tiempo medio de estancia: "  + obtenerTmedio());		
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		//Acumulamos los contadores de cada puerta
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {		
		//Comprobamos que la suma de los contadores de las puertas sea igual a la suma de presonas totales
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parque";
		assert contadorPersonasTotales >= 0 : "INV: El parque no puede tener menos de 0 personas";
	}

	protected synchronized void comprobarAntesDeEntrar(){	
		//Cuando se alcanza el limite del parque, el hilo se pone a la espera
		while(contadorPersonasTotales == 40 ){ 
	           try {
				wait(); 
			} catch (InterruptedException e) {}
	        }
	}

	protected synchronized void comprobarAntesDeSalir(){
		//Cuando no hay nadie en el parque, el hilo se pone a la espera
		while (contadorPersonasTotales == 0 ) 	
			try { wait(); } 
			catch(InterruptedException e) {}	
	}
	
	private double obtenerTmedio() {
		//Calculamos el tiempo medio de estancia
		tMedio = (tMedio + (tSalida - tEntrada))/2.0; 
		return tMedio; 
	}

}