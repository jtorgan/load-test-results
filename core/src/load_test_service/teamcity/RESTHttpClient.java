package load_test_service.teamcity;

import load_test_service.teamcity.exceptions.TCException;
import load_test_service.teamcity.exceptions.TCParseHttpResultException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.http.HTTPException;
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

    @NotNull
    public Document execute(String url) throws TCException {
        GetMethod method = new GetMethod(serverRestPrefix + url);
        return executeHTTPMethod(method);
    }

    @NotNull
    public Document executeWithoutPrefix(String url) throws TCException {
        GetMethod method = new GetMethod(server + url);
        return executeHTTPMethod(method);
    }


    @Nullable
    public InputStream loadFile(String path) throws TCException {
        GetMethod method = new GetMethod(server + path);
        int status;
        try {
            status = httpClient.executeMethod(method);
            if (status != 200)
                throw new HTTPException(status);

            return method.getResponseBodyAsStream();
        } catch (IOException e) {
            throw new TCException(e);
        }
    }


    private Document executeHTTPMethod(GetMethod method) throws TCException {
        int status;
        try {
            status = httpClient.executeMethod(method);
            if (status != 200)
                throw new HTTPException(status);
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return documentBuilder.parse(method.getResponseBodyAsStream());
        } catch (IOException e) {
            throw new TCException(e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new TCParseHttpResultException(e);
        } catch (Exception e) {
            throw new TCException(e);
        }
    }


}
