# HTTPS requests in org.springframework.web

## CA Cert Validation when using org.springframework.web.client.RestTemplate 
This example uses org.springframework.web.client.RestTemplate to make http requests. This make use of the SSLSocketFactoryImp, and SSLContextImpl$DefaultSSLContext classes, through which get access to the default cacert store file (loaded through X509TrustManagerImpl).

Code references: 
* http://www.docjar.com/html/api/sun/security/ssl/SSLContextImpl.java.html - (DefaultSSLContext-> getDefaultTrustManager)
* http://www.docjar.com/html/api/sun/security/ssl/TrustManagerFactoryImpl.java.html - (TrustManagerFactoryImpl-> getCacertsKeyStore)

So by default it looks for env variable "javax.net.ssl.trustStore" for trustStore path.

```java
  127       /**
  128        * Returns the keystore with the configured CA certificates.
  129        */
  130       static KeyStore getCacertsKeyStore(String dbgname) throws Exception
  131       {
  132           String storeFileName = null;
  133           File storeFile = null;
  134           FileInputStream fis = null;
  135           String defaultTrustStoreType;
  136           String defaultTrustStoreProvider;
  137           final HashMap<String,String> props = new HashMap<>();
  138           final String sep = File.separator;
  139           KeyStore ks = null;
  140   
  141           AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
  142               public Void run() throws Exception {
  143                   props.put("trustStore", System.getProperty(
  144                                   "javax.net.ssl.trustStore"));
  145                   props.put("javaHome", System.getProperty(
  146                                           "java.home"));
  147                   props.put("trustStoreType", System.getProperty(
  148                                   "javax.net.ssl.trustStoreType",
  149                                   KeyStore.getDefaultType()));
  150                   props.put("trustStoreProvider", System.getProperty(
  151                                   "javax.net.ssl.trustStoreProvider", ""));
  152                   props.put("trustStorePasswd", System.getProperty(
  153                                   "javax.net.ssl.trustStorePassword", ""));
  154                   return null;
  155               }
  156           });

```  
If "javax.net.ssl.trustStore" is not set, then it falls back to 'jssecacert' and 'cacert' files under JAVA_HOME\lib\security folder.

```java
  158           /*
  159            * Try:
  160            *      javax.net.ssl.trustStore  (if this variable exists, stop)
  161            *      jssecacerts
  162            *      cacerts
  163            *
  164            * If none exists, we use an empty keystore.
  165            */
  166   
  167           storeFileName = props.get("trustStore");
  168           if (!"NONE".equals(storeFileName)) {
  169               if (storeFileName != null) {
  170                   storeFile = new File(storeFileName);
  171                   fis = getFileInputStream(storeFile);
  172               } else {
  173                   String javaHome = props.get("javaHome");
  174                   storeFile = new File(javaHome + sep + "lib" + sep
  175                                                   + "security" + sep +
  176                                                   "jssecacerts");
  177                   if ((fis = getFileInputStream(storeFile)) == null) {
  178                       storeFile = new File(javaHome + sep + "lib" + sep
  179                                                   + "security" + sep +
  180                                                   "cacerts");
  181                       fis = getFileInputStream(storeFile);
  182                   }
  183               }
```

## How to use Self-Singed-Certificates
So with this default implemention as mentioned above, the validation of self signed certificates will fail because the cert is not part of the default truststore or it's root is not signed by a CA. So the recommended approach is to create a keystore file and import the X509 cert or the publicKey to it using keytool. 

```bash
keytool -import -alias susan -file Example.cer -keystore exampleraystore
```
set password when prompted.
ref: https://docs.oracle.com/javase/tutorial/security/toolsign/rstep2.html

Then set the "javax.net.ssl.trustStore" to the path of the keystore file.


### Blog post: http://egazer.blogspot.com/2017/04/https-requests-using.html
