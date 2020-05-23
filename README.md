# Movie Socket Api v1.0

This application is a TCP Server that queries the https://www.imdb.com/ web site to find movies.

## Details

All the request must respect the text protocol  (query length):(query)

* query length : Size of request/response content
* query : Request/response content

##### Example:

Request payload: 5:teste
Response payload: 3000:Movie 1\nMovie 2\n....

### Technologies Used

 * Java 8
 * Maven
 * Jsoup (HTML parser)
 * Guice (Simple DI Framework)
 * Junit
 * Mockito
 
 
 ###Author
 Lucas Frederico Mançan(lucasfmancan@gmail.com)
 
 ##### References
 
 * http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 * https://www.baeldung.com/guice