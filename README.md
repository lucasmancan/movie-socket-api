# Movie Socket Api v1.0

This application is a TCP Server that queries the https://www.imdb.com/ web site to find movies.

## Details

All the request must respect the text protocol  (query length):(query)

* query length : number that represents the size of request/response content
* query : Request/response content text
* Default port 8085 (I dont want to conflict with your running process on 8080 ;) ) 
* Execution 'java -jar movie-socket-api-1.0.jar'
- The program should give you a list of related titles and close your connection.

##### Example:

* Request payload: 5:teste
* Response payload: 3000:Movie 1\nMovie 2\n....

###### How to Debug?

* git clone https://github.com/lucasmancan/movie-socket-api.git.
* Open the project in your favorite IDEA or go to the project directory and use maven lifecycle manualy to install dependencies and generate build file.
* Run the project in your IDE or use javac

##### Testing...
 - Run 'mvn test'
 - open your windows terminal and type 'telnet localhost 8085'
 - Now that the connection is open, input the size of title and then type the title  you want. (You should respect the text protocol present in details section)

#### Error handling

 If the message doesn't follow the protocol, an error message is sent to the client and the connection is closed.
 
###### Default Errors

    * 63:The <query> should not be empty, please rewrite your message... (when the <query length> parameter is empty>)
    * 85:The <query> can only have alphanumeric chars and '/:', please rewrite your message... (when the <query> has special chars, except  '/' and ':')
    * 90:The <query length> is out of 'Long' range and is not valid, please rewrite your message... (when the <query length> has a invalid number)

### Technologies Used

 * Java 8
 * Maven
 * Jsoup (HTML parser)
 * Guice (Simple DI Framework)
 * Junit
 
 
 ### Author
 Lucas Frederico Mançan(lucasfmancan@gmail.com)
 
 ##### References
 
 * http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 * https://www.baeldung.com/guice