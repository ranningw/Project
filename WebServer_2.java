import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class WebServer_2
{
	public static void main(String argv[]) throws Exception
	{
		// Set the port number.
	    int port = 6789;

        // Establish the listen socket.
       ServerSocket s = new ServerSocket(port);

        // Process HTTP service requests in an infinite loop.
        while (true) {
            // Listen for a TCP connection request.
            Socket sock = s.accept();

            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(sock);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();

	
        }

	}
}

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
    public void run()
    {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

	private void processRequest() throws Exception
    {
	// Get a reference to the socket's input and output streams.
	InputStream is = socket.getInputStream();
	DataOutputStream os = new DataOutputStream(socket.getOutputStream());

	// Set up input stream filters.
	InputStreamReader sr = new InputStreamReader(is);
	BufferedReader br = new BufferedReader(sr);

	// Get the request line of the HTTP request message.
    String requestLine = br.readLine();

    // Display the request line.
    System.out.println();
    System.out.println(requestLine);


     // Extract the filename from the request line.
     StringTokenizer tokens = new StringTokenizer(requestLine);
     tokens.nextToken();
    // skip over the method, which should be "GET"
     String fileName = tokens.nextToken();
 
     // Prepend a "." so that file request is within the current directory.
     fileName = "." + fileName;

    //After obtaining the request line of the message header, we obtain the header lines.
    // Since we don't know ahead of time how many header lines the client will send, 
    //we must get these lines within a looping operation.


    // Get and display the header lines.
    String headerLine = null;
    while ((headerLine = br.readLine()).length() != 0) {
        System.out.println(headerLine);
    }


    //begin B

   

    // Open the requested file.
    FileInputStream fis = null;
    boolean fileExists = true;
    try {
        fis = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
        fileExists = false;
    }


    // Construct the response message.
    String statusLine = null;
    String contentTypeLine = null;
    String entityBody = null;
    if (fileExists) {
        statusLine = "HTTP1.1 200 OK" + CRLF;
        contentTypeLine = "Content-type: " + 
            contentType( fileName ) + CRLF;
    } else {
        statusLine = "HTTP1.1 404 Not Found" + CRLF;
        contentTypeLine = "Content-type: " + 
		contentType( fileName ) + CRLF;;
        entityBody = "<HTML>" + 
            "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
            "<BODY>Not Found</BODY></HTML>";
    }


    // Send the status line.
    os.writeBytes(statusLine);

    // Send the content type line.
    os.writeBytes(contentTypeLine);

    // Send a blank line to indicate the end of the header lines.
    os.writeBytes(CRLF);


     // Send the entity body.
     if (fileExists)	{
        sendBytes(fis, os);
        fis.close();
    } else {
        os.writeBytes(entityBody);
    }



    //end B

    // Close streams and socket.
    os.close();
    br.close();
    socket.close();

    }


    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
   // Construct a 1K buffer to hold bytes on their way to the socket.
   byte[] buffer = new byte[1024];
   int bytes = 0;

   // Copy requested file into the socket's output stream.
   while((bytes = fis.read(buffer)) != -1 ) {
      os.write(buffer, 0, bytes);
    }

    }

    private static String contentType(String fileName)
    {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if(fileName.endsWith(".gif") ) {
            return "image/gif";
        }
        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }


    
}
