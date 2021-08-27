package practise.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import practise.dto.User;
import practise.jdbc_dao.User1Dao;
import practise.jdbc_dao.UserDao;
import practise.util.ByteCompressor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
//import practise.jdbc_dao.UserDao;
/*The order in which Spring container loads beans cannot be predicted. There's no specific ordering logic specification given by Spring framework. But Spring guarantees
        if a bean A has dependency of B (e.g. bean A has an instance variable @Autowired B b;) then B will be initialized first*/

/*To demo Spring jdbc based local, distributed transactions (programmatic, declarative)*/
@RestController
@RequestMapping("/jdbc_trans")
public class UserController1 {


    @Autowired
    ServletWebServerApplicationContext servletContainer; // understand Spring boot embedded API for tomcat webserver.

    @Autowired
    UserDao userDao;
    @Autowired
    User1Dao user1Dao;

    @GetMapping("/hi")
    public String sampleEndUrl() {
//        System.out.println("server.servlet.context-path"+servletContainer.getServletConfig().getServletContext().getContextPath());
        //Print Server configurations to know spring web-server configurations
        System.out.println("server.port" + servletContainer.getWebServer().getPort()); //port
        System.out.println(servletContainer.getServletContext().getContextPath()); //context root url


        return "hi";

    }

    @PostMapping("/getFile")
    public ResponseEntity<String> getFile() throws Exception {
        //read file into stream

        File file = new File("Passport.pdf");
        InputStream is = new FileInputStream(file);
        byte[] b = new byte[is.available()];
        is.read(b); // read byte buffer into array of bytes.

        //compress above stream to gzip encoded
        ByteCompressor.gzipEncode(b);
        //encode stream to base64 string and return and encoding response header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_ENCODING, "gzip");

        return new ResponseEntity<String>(Base64.encodeBase64String(ByteCompressor.gzipEncode(b)),httpHeaders, HttpStatus.OK);


/*   //write the compressed to new file, to check change in size.
    FileOutputStream os=new FileOutputStream("compressed.pdf");
    os.write(ByteCompressor.gzipEncode(b));
        return "succes";*/
    }

    @GetMapping("/writeFile")
    public String writeFile() throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity=new HttpEntity(new String());
     ResponseEntity<String>response=   restTemplate.exchange("http://localhost:8080/rest/jdbc_trans/getFile",HttpMethod.POST,httpEntity ,String.class );
       // String s = restTemplate.getForObject("http://localhost:8080/rest/jdbc_trans/getFile", String.class);
        //compare the size of the received encoded string with and without gzip compression encoding//
       // System.out.println("received length of encoded string with compression:" + s.getBytes().length); //length in bytes  (1kb=1024)
        // Note: base64 encoded version would have size of ~ 33 % higher than original source, where it's higher with lesser encoding data.
if(response.getHeaders().get(HttpHeaders.CONTENT_ENCODING).get(0).equals("gzip")) {

            //configure outputStream for destination which is a file here
            File file = new File("Passport1.pdf");
            OutputStream os = new FileOutputStream(file);
            //decoded-decompressed byte[]. convert to string and write into file
            os.write(ByteCompressor.gzipDecode(Base64.decodeBase64(response.getBody())));
        }
        return "file written";
    }


    @RequestMapping(value = "user_local", method = RequestMethod.GET)
    public ResponseEntity<User> getUser(@RequestParam("username") String userName) {

        //can we retreive Tomcat thread pool properties here, to validate the default values.
        User user = userDao.getUserFromDbWithJdbc(userName);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "user_distributed", method = RequestMethod.GET)
    public ResponseEntity<User> getUser1(@RequestParam("username") String userName) {
        User user = user1Dao.getUserFromDbWithJdbc(userName);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @PutMapping("user_distributed_update")
    public boolean updateFirstName(@RequestParam("email") String email) {

        return user1Dao.updateUserEmail(email);
    }

    @PutMapping("user_distributed_update1")
    public boolean updateFirstName1(@RequestParam("email") String email) {

        return user1Dao.updateUser(email);
    }
}
