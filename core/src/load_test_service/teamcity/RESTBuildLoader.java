package load_test_service.teamcity;

import load_test_service.api.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RESTBuildLoader {
    RESTHttpClient client;


    public RESTBuildLoader(RESTHttpClient client) {
        this.client = client;
    }

    public TestBuild loadBuild(String btID, String buildID) {
        TestBuild build = new TestBuild(new BuildID(btID, buildID));
        Document doc = setBuildInfo(build);
        setArtifactDependencies(doc, build);
        return build;
    }

    private Document setBuildInfo(BaseBuildInfo build) {
        String query = "builds/id:" + build.getID().getBuildID();
        Document doc = client.execute(query);
        if (doc != null) {
            Element buildEl = (Element) doc.getElementsByTagName("build").item(0);
            build.setStatus(buildEl.getAttribute("status"));
            build.setBuildNumber(buildEl.getAttribute("number"));
            build.setFinishDate(getText(doc, "finishDate"));
        }
        return doc;
    }

    private void setArtifactDependencies(Document baseDoc, TestBuild build){
        NodeList dependencies = baseDoc.getElementsByTagName("artifact-dependencies");
        if (dependencies != null && dependencies.getLength() > 0) {
            NodeList depBuilds = ((Element)dependencies.item(0)).getElementsByTagName("build");
            for (int i = 0 ; i < depBuilds.getLength(); i++) {
                Element el = (Element) depBuilds.item(i);
                DependencyBuild dependency = new DependencyBuild(new BuildID(el.getAttribute("buildTypeId"), el.getAttribute("id")));
                Document doc = setBuildInfo(dependency);
                dependency.setName(getAttribute(doc, "buildType", "projectName") + " > " + getAttribute(doc, "buildType", "name"));
                setChanges(doc, dependency);
                build.addDependency(dependency);
            }
        }
    }

    private void setChanges(Document baseDoc, DependencyBuild build) {
        String query = getAttribute(baseDoc, "changes", "href");
        Document doc = client.executeWithoutPrefix(query);
        if (doc != null) {
            NodeList changes = doc.getElementsByTagName("change");
            for (int i = 0 ; i < changes.getLength(); i++) {
                Element el = (Element) changes.item(i);

                Change change = new Change();
                change.setAuthor(el.getAttribute("username"));
                change.setRevision(el.getAttribute("version"));

                build.addChange(change);
            }
        }
    }





    private String getAttribute(Document doc, String tagName, String attrName) {
        NodeList tags = doc.getElementsByTagName(tagName);
        if (tags != null && tags.getLength() > 0) {
            return ((Element) tags.item(0)).getAttribute(attrName);
        }
        return null;
    }

    private String getText(Document doc, String tagName) {
        NodeList tags = doc.getElementsByTagName(tagName);
        if (tags != null && tags.getLength() > 0) {
            return tags.item(0).getTextContent();
        }
        return null;
    }


    /*    private void setLoadStatistic(Document baseDoc, BaseBuildInfo build) {
        String query = getAttribute(baseDoc, "statistics", "href");
        Document doc = client.execute(query);
        if (doc != null) {
            NodeList properties = doc.getElementsByTagName("property");
            for (int i = 0 ; i < properties.getLength(); i++) {
                Element el = (Element) properties.item(i);

                String key = el.getAttribute("name");
                if (!key.startsWith(build.getID().getBuildTypeID()))
                    continue;
                build.addStatisticValue(key, el.getAttribute("key"));
            }
        }
    }*/
}
