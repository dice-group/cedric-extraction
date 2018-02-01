package preprocessing.string;


import org.aksw.fox.data.Entity;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import java.io.OutputStreamWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Based on:
 * {@see https://github.com/dice-group/FOX/blob/master/src/main/java/org/aksw/fox/tools/linking/common/Agdistis.java}
 *
 * @author Cedric Richter
 */
public class Agdistis {

    // maps AGDISTIS index to real index
    protected Map<Integer, Entity> indexMap = new HashMap<>();
    protected String endpoint;


    public Agdistis() {
        endpoint = "http://titan.informatik.uni-leipzig.de:8080/AGDISTIS";
    }

    public void setUris(final Set<Entity> entities, final String input) {

        String agdistis_input = makeInput(entities, input);



        String agdistis_output = "";
        try {
            agdistis_output = send(agdistis_input);
            agdistis_input = null;
        } catch (final Exception e) {
            e.printStackTrace();
        }


        addURItoEntities(agdistis_output, entities);

        indexMap.clear();
    }

    private String makeInput(final Set<Entity> entities, final String input) {

        final Map<Integer, Entity> indexEntityMap = new HashMap<>();
        entities.forEach(entity -> entity.getIndices().forEach(i -> indexEntityMap.put(i, entity)));

        final Set<Integer> startIndices = new TreeSet<>(indexEntityMap.keySet());
        String agdistis_input = "";
        int last = 0;
        for (final Integer index : startIndices) {
            final Entity entity = indexEntityMap.get(index);

            agdistis_input += input.substring(last, index);
            // int fakeindex = agdistis_input.length() + "<entity>".length();

            agdistis_input += "<entity>" + entity.getText() + "</entity>";

            last = index + entity.getText().length();
            // indexMap.put(fakeindex + indexOffset, entity);
            indexMap.put(index, entity);
        }
        agdistis_input += input.substring(last);

        return agdistis_input;
    }

    protected String send(final String agdistis_input) throws Exception {

        // String data = parameter + agdistis_input;
        final String urlParameters =
                "text=" + URLEncoder.encode(agdistis_input, "UTF-8") + "&type=agdistis&heuristic=false";
        final URL url = new URL(endpoint);

        final HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod("POST");
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setUseCaches(false);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
        // http.setRequestProperty("Content-Length",
        // String.valueOf(data.length()));

        final OutputStreamWriter writer = new OutputStreamWriter(http.getOutputStream());
        writer.write(urlParameters);
        writer.flush();

        return IOUtils.toString(http.getInputStream(), "UTF-8");
    }


    private void addURItoEntities(String json, Set<Entity> entities) {
        if (json != null && json.length() > 0) {

            JSONArray array = (JSONArray) JSONValue.parse(json);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {

                    Integer start = ((Long) ((JSONObject) array.get(i)).get("start")).intValue();
                    String disambiguatedURL = (String) ((JSONObject) array.get(i)).get("disambiguatedURL");

                    if (start != null && start > -1) {
                        Entity entity = indexMap.get(start);

                        if (disambiguatedURL == null) {
                            URI uri;
                            try {
                                uri = new URI(
                                        "http",
                                        "scms.eu",
                                        "/" + entity.getText().replaceAll(" ", "_"),
                                        null);
                                entity.uri = uri.toASCIIString();
                            } catch (URISyntaxException e) {
                                entity.uri = "http://scms.eu/" + entity.getText();
                                System.out.println(entity.uri);
                                e.printStackTrace();
                            }

                        } else {
                            entity.uri = urlencode(disambiguatedURL);
                        }
                    }
                }
            }
        }
    }

    protected String urlencode(final String disambiguatedURL) {
        try {
            final String encode = URLEncoder.encode(disambiguatedURL
                    .substring(disambiguatedURL.lastIndexOf('/') + 1, disambiguatedURL.length()), "UTF-8");
            return disambiguatedURL.substring(0, disambiguatedURL.lastIndexOf('/') + 1).concat(encode);

        } catch (final Exception e) {
            return disambiguatedURL;
        }
    }

}
