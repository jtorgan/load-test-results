package load_test_service.teamcity;

import load_test_service.api.model.*;
import load_test_service.teamcity.exceptions.TCException;
import load_test_service.teamcity.exceptions.TCParseHttpResultException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RESTBuildLoader {
    RESTHttpClient client;


    public RESTBuildLoader(RESTHttpClient client) {
        this.client = client;
    }

    @NotNull
    public TestBuild loadBuild(@NotNull String btID, @NotNull String buildID) throws TCException {
        TestBuild build = new TestBuild(new BuildID(btID, buildID));
        Document doc = setBuildInfo(build);
        setArtifactDependencies(doc, build);
        return build;
    }

    @NotNull
    private Document setBuildInfo(BaseBuildInfo build) throws TCException {
        String query = "builds/id:" + build.getID().getBuildID();
        Document doc = client.execute(query);

        Element buildEl = (Element) doc.getElementsByTagName("build").item(0);
        build.setStatus(buildEl.getAttribute("status"));
        build.setBuildNumber(buildEl.getAttribute("number"));
        String finishDate = getText(doc, "finishDate");
        if (finishDate != null)
            build.setFinishDate(finishDate);
        else
            throw new TCParseHttpResultException("Finish date is null! Doc: " + doc.toString());
        return doc;
    }

    private void setArtifactDependencies(@NotNull Document baseDoc, @NotNull TestBuild build) throws TCException {
        NodeList dependencies = baseDoc.getElementsByTagName("artifact-dependencies");
        if (dependencies != null && dependencies.getLength() > 0) {
            NodeList depBuilds = ((Element)dependencies.item(0)).getElementsByTagName("build");
            for (int i = 0 ; i < depBuilds.getLength(); i++) {
                Element el = (Element) depBuilds.item(i);
                DependencyBuild dependency = new DependencyBuild(new BuildID(el.getAttribute("buildTypeId"), el.getAttribute("id")));

                Document doc = setBuildInfo(dependency);
                String projectName = getAttribute(doc, "buildType", "projectName");
                if (projectName == null)
                    throw new TCParseHttpResultException("There is no project name for dependency " + dependency.getID() + ". Test build: " + build);
                dependency.setName(getAttribute(doc, "buildType", "projectName") + " > " + getAttribute(doc, "buildType", "name"));
                setChanges(doc, dependency);

                build.addDependency(dependency);
            }
        }
    }

    private void setChanges(@NotNull Document baseDoc, @NotNull DependencyBuild build) throws TCException {
        String query = getAttribute(baseDoc, "changes", "href");
        if (query == null) return;
        Document doc = client.executeWithoutPrefix(query);
        NodeList changes = doc.getElementsByTagName("change");
        for (int i = 0 ; i < changes.getLength(); i++) {
            Element el = (Element) changes.item(i);

            Change change = new Change();
            change.setAuthor(el.getAttribute("username"));
            change.setRevision(el.getAttribute("version"));

            build.addChange(change);
        }
    }




    @Nullable
    private String getAttribute(@NotNull Document doc, String tagName, String attrName) {
        NodeList tags = doc.getElementsByTagName(tagName);
        if (tags != null && tags.getLength() > 0) {
            return ((Element) tags.item(0)).getAttribute(attrName);
        }
        return null;
    }

    @Nullable
    private String getText(@NotNull Document doc, String tagName) {
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
