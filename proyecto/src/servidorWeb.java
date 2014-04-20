import java.io.*;
import java.net.*;
import java.util.*;

public class servidorWeb
{
	int puerto = 6666;
		
	// Inicio de ejecucion
	public static void main(String [] array)	
	{
		servidorWeb instanciaServ = new servidorWeb();	
		instanciaServ.correr();
	}
	
	boolean correr()
	{
		System.out.println("MSJ: -Servidor corriendo-");
		
		try
		{
			ServerSocket servSock = new ServerSocket(6666);

			System.out.println("MSJ: -Esperando conexion...-");
			
			// Loop infinito que queda en espera de conexiones que entren, a las que se les asocia un thread
			while(true)
			{
				Socket entrante = servSock.accept();
				request rqCliente = new request(entrante);
				rqCliente.start();
			}
			
		}
		catch(Exception e)
		{
			System.out.println("MSJ: -Error en el servidor: " + e.toString());
		}
		
		return true;
	}	
}

class request extends Thread
{
	int contador = 0;

	private Socket scliente = null;	// Request del cliente de turno
   	private PrintWriter out = null;	// Buffer donde se escribe respuesta a las peticiones

   	request(Socket ps)
   	{
		System.out.println(currentThread().toString() + " - " + "El contador es: " + contador);
   		
		contador++;
		
		// El socket de cliente enviado como request se asocia a scliente
		scliente = ps;
		
		// Se setea una baja prioridad
		setPriority(NORM_PRIORITY - 1);
   	}

   	// Se sobrecarga el metodo run asociado a los threads
	public void run()
	{
		System.out.println(currentThread().toString() + " - " + "Procesando conexion...");

		try
		{
			// Flujo de entrada desde el cliente al servidor
			BufferedReader in = new BufferedReader (new InputStreamReader(scliente.getInputStream()));
			
			// Flujo de salida desde el servidor al cliente
  			out = new PrintWriter(new OutputStreamWriter(scliente.getOutputStream(),"8859_1"),true) ;
  			
  			// Aca se guarda lo que se lee
  			String cadLeida = "";
  			
  			// Flag
			int i = 0, leiTodo = 0;
	
			// Lee una linea mientras no este vacia y su largo sea diferente de cero
			//do			
			while(leiTodo == 0){
				// Lee una linea
				cadLeida = in.readLine();

				// Si la cadena no es vacia, la imprime en el log
				if(cadLeida != null)
				{
					System.out.println(currentThread().toString() + " - " + "--" + cadLeida + "-");
					
					// Aca se toma lo ingresado por el usuario y se guarda en strings para uso posterior
					if(cadLeida.contains("nombre"))
					{
						// Llegamos a la parte de nombre, podriamos guardar aca el nombre ingresado
					}
				}

				// Hace esto solo para la primera linea que se lee; la que tiene el request url
				if(i == 0) 
				{
					// Esto para que se lea solo la primera
			        i++;
			        
			        // StringTokenizer rompe el string en los tokens; st guarda los pedazos de "cadena"
			        StringTokenizer st = new StringTokenizer(cadLeida);
                    
			        // Si se han contado mas de dos tokens y el que sigue es un GET
                    if((st.countTokens() >= 2) && st.nextToken().equals("GET")) 
                    {
                    	// Devuelve el archivo que se pidio en el request
                    	retornaArchivo(st.nextToken());
                    }
                    else 
                    {
                    	// AQUI HACER ELSE IF PARA POST
                    	StringTokenizer stp = new StringTokenizer(cadLeida);
                    	
                    	if((stp.countTokens() >= 2) && stp.nextToken().equals("POST"))
                    	{
                    		// Aca deberiamos mandar los datos obtenidos a una funcion que retorne la pagina con la lista
                        	System.out.println("tetafatfatftdsbsgfgdfgggggggggggggggggggggggggggg");
                        }
                    	else{
                    		// Si es que no es GET o POST
                    		out.println("400: Solicitud Incorrecta");
                    	}
                    }
				}
				
				if(cadLeida == null)
				{
					leiTodo = 1;
					System.out.println(cadLeida + " nyanyanyanyanyanya");
					// Falta matar el thread
					//currentThread().interrupt();
				}
			}
			//while (cadLeida != null && cadLeida.length() != 0);

		}
		catch(Exception e)
		{
			System.out.println(currentThread().toString() + " - " + "Error en servidor: " + e.toString());
		}
			
		System.out.println(currentThread().toString() + " - " + "Fin de la ejecucion");
	}
	
	void retornaArchivo(String strArchivo)
	{
		System.out.println(currentThread().toString() + " - " + "Recuperando archivo: " + strArchivo);
		
		// Se revisa si es que tiene una barra
		if (strArchivo.startsWith("/"))
		{
			strArchivo = strArchivo.substring(1) ;
		}
        
        // Si termina en /, le retornamos el HTML en aquel directorio
        // Si la cadena esta vacia, no retorna el HTML de ese directorio
        if (strArchivo.endsWith("/") || strArchivo.equals(""))
        {
        	strArchivo = strArchivo + "index.htm" ;
        }
        
        try
        {	        
		    // Lectura y retorno del archivo
		    File archPedido = new File(strArchivo);
		        
		    // Si existe el archivo que se pidio
		    if (archPedido.exists()) 
		    {
		    	// Imprime datos de respuesta
	      		out.println("HTTP/1.0 200 ok");
				out.println("Server: ServidorWeb/1.0");
				out.println("Date: " + new Date());
				out.println("Content-Type: text/html");
				out.println("Content-Length: " + archPedido.length());
				out.println("\n");
				
				// El lector se asocia al archivo que se pidio
				BufferedReader archLocal = new BufferedReader(new FileReader(archPedido));
				
				String linea = "";
				
				do			
				{
					linea = archLocal.readLine();
	
					if (linea != null )
					{
						out.println(linea);
					}
				}
				while (linea != null);
				
				System.out.println(currentThread().toString() + " - " + "Envio del archivo finalizado");
				
				archLocal.close();
				out.close();
				
			} // Finalizacion si es que existe el archivo 
			else
			{
				System.out.println(currentThread().toString() + " - " + "No se encuentra el archivo: " + archPedido.toString());
	      		out.println("HTTP/1.0 400 ok");
	      		out.close();
			} // Finalizacion de no encontrarse el archivo
			
		}
		catch(Exception e)
		{
			System.out.println(currentThread().toString() + " - " + "Error al retornar el archivo");
		}
	}
}