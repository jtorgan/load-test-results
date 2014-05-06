package load_test_service.teamcity;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class RESTHttpClient {
    private final String server;
    private final String serverRestPrefix;

    private HttpClient httpClient;

    public static RESTHttpClient newDefaultInstance() {
        return new RESTHttpClient("http://buildserver.labs.intellij.net", "Yuliya.Torhan", "k.nbrb");
    }

    public RESTHttpClient(String tcServer, String user, String password) {
        server = tcServer;
        serverRestPrefix = server + "/httpAuth/app/rest/";

        Credentials credentials = new UsernamePasswordCredentials(user, password);
        httpClient = new HttpClient();
        httpClient.getState().setCredentials(AuthScope.ANY, credentials);
    }

    @Nullable
    public Document execute(String url) {
        GetMethod method = new GetMethod(serverRestPrefix + url);
        int status;
        try {
            status = httpClient.executeMethod(method);
            if (status == 200) {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                return documentBuilder.parse(method.getResponseBodyAsStream());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Document executeWithoutPrefix(String url) {
        GetMethod method = new GetMethod(server + url);
        int status;
        try {
            status = httpClient.executeMethod(method);
            if (status == 200) {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                return documentBuilder.parse(method.getResponseBodyAsStream());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public InputStream loadFile(String path) {
        GetMethod method = new GetMethod(server + path);
        int status;
        try {
            status = httpClient.executeMethod(method);
            if (status == 200) {
                return method.getResponseBodyAsStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
