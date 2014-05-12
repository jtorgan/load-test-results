package load_test_service.teamcity;

import load_test_service.api.model.TestBuild;
import load_test_service.teamcity.exceptions.TCException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public enum RESTCommandImpl implements RESTCommand {
    GET_PROJECT_NAME() {
        @Override
        @NotNull
        public String execute(@NotNull final RESTHttpClient client, @NotNull final Object ... params) throws TCException {
            String query = "projects/id:" + params[0];
            Document doc = client.execute(query);
            NodeList nodeList = doc.getElementsByTagName("project");
            if (nodeList.getLength() > 0) {
                Element el = (Element) nodeList.item(0);
                return el.getAttribute("name");
            }
            return EMPTY_STRING;
        }
    },
    GET_SUB_PROJECTS() {
        @Override
        @NotNull
        public Map<String, String> execute(@NotNull final RESTHttpClient client, @NotNull final Object... params) throws TCException {
            String query = "projects/id:" + params[0];
            Document doc = client.execute(query);
            Map<String, String> sub = new HashMap<>();
            NodeList nodeList = doc.getElementsByTagName("project");
            for (int i = 0 ; i < nodeList.getLength(); i++) {
                Element project = (Element) nodeList.item(i);
                String id = project.getAttribute("id");
                if (!params[0].equals(id)) {
                    sub.put(id, project.getAttribute("name"));
                }
            }
            return sub;
        }
    },

    GET_BUILD_TYPES() {
        @Override
        @NotNull
        public Map<String, String> execute(@NotNull final RESTHttpClient client, @NotNull final Object ... params) throws TCException {
            String query = "projects/id:" + params[0] + "/buildTypes";
            Document doc = client.execute(query);

            Map<String, String> projects = new HashMap<>();
            NodeList nodeList = doc.getElementsByTagName("buildType");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                projects.put(el.getAttribute("id"), el.getAttribute("name"));
            }
            return projects;
        }
    },

    GET_ALL_BUILDS(){
        @Override
        @NotNull
        public List<TestBuild> execute(@NotNull final RESTHttpClient client, @NotNull final Object ... params) throws TCException {
            String query = "buildTypes/id:" + params[0] + "/builds/";
            Document doc = client.execute(query);

            List<TestBuild> builds = new ArrayList<>();
            NodeList nodeList = doc.getElementsByTagName("build");
            for (int i = 0 ; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if ("finished".equals(el.getAttribute("state"))) {
                    RESTBuildLoader loader = new RESTBuildLoader(client);
                    TestBuild build = loader.loadBuild(el.getAttribute("buildTypeId"), el.getAttribute("id"));
                    builds.add(build);
                }
            }
            return builds;
        }
    },
    GET_BUILDS_FROM_LAST() {
        @NotNull
        @Override
        public List<TestBuild> execute(@NotNull final RESTHttpClient client, @NotNull final Object... params) throws TCException {
            String query = "builds?buildType=id:" + params[0] + "&sinceBuild=id:" + params[1];
            Document doc = client.execute(query);

            List<TestBuild> builds = new ArrayList<>();
            NodeList nodeList = doc.getElementsByTagName("build");
            for (int i = 0 ; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if ("finished".equals(el.getAttribute("state"))) {
                    RESTBuildLoader loader = new RESTBuildLoader(client);
                    TestBuild build = loader.loadBuild(el.getAttribute("buildTypeId"), el.getAttribute("id"));
                    builds.add(build);
                }
            }
            return builds;
        }
    },

    GET_ARTIFACT_PATHS() {
        private final List<Pattern> EMPTY = Arrays.asList(Pattern.compile(""));
        @NotNull
        @Override
        public Map<String, String> execute(@NotNull RESTHttpClient client, Object[] params) throws TCException {
            String buildID = params[0].toString();

            List<Pattern> compiled;
            if (params[1] == null) {
                compiled = EMPTY;
            } else {
                List<String> patterns = (List<String>) params[1];
                compiled = new ArrayList<>(patterns.size());
                for (String pattern : patterns) {
                    if (pattern.startsWith("*."))
                        pattern = pattern.replace("*.", ".*.");
                    compiled.add(Pattern.compile(pattern));
                }
            }

            String query = "builds/id:" + buildID + "/artifacts";
            Document doc = client.execute(query);

            Map<String, String> paths = new HashMap<>();
            NodeList nodeList = doc.getElementsByTagName("file");
            for (int i = 0; i < nodeList.getLength(); i++) {
                processFile(client, nodeList.item(i), compiled, paths);
            }
            return paths;
        }
        private void processFile(RESTHttpClient client, Node file, List<Pattern> patterns, Map<String, String> result) throws TCException {
            Element children = null;
            Element content = null;

            NodeList fileChildNodes = file.getChildNodes();
            for (int i = 0; i < fileChildNodes.getLength(); i++) {
                Node el = fileChildNodes.item(i);
                if ("content".equals(el.getNodeName()))
                    content = (Element) el;
                else if ("children".equals(el.getNodeName()))
                    children = (Element) el;
            }
            if (content != null) {
                checkArtifactContent((Element) file, content, patterns, result);
            } else if (children != null){
                try {
                    processChildren(client, children, patterns, result);
                } catch (Exception e) {
                    throw new TCException(e);
                }
            }
        }
        private void checkArtifactContent(Element file, Element content, List<Pattern> patterns, Map<String, String> result) {
            String name = file.getAttribute("name");
            String href = content.getAttribute("href");
            boolean matched = false;
            for (Pattern pattern : patterns)
                matched |= pattern.matcher(href).find();
            if (matched)
                result.put(name, href);
        }

        private void processChildren(RESTHttpClient client, Element children, List<Pattern> patterns, Map<String, String> result) throws TCException {
            Document doc = client.executeWithoutPrefix(children.getAttribute("href"));
            NodeList nodeList = doc.getElementsByTagName("file");
            for (int i = 0; i < nodeList.getLength(); i++) {
                processFile(client, nodeList.item(i), patterns, result);
            }
        }

    },
    GET_ARTIFACT_STREAM() {
        @Nullable
        @Override
        public InputStream execute(@NotNull RESTHttpClient client, @NotNull Object... params) throws TCException {
            return client.loadFile(params[0].toString());
        }
    }
    ;
    private static final String EMPTY_STRING = "";

}
