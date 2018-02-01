package preprocessing.string;

import edu.stanford.nlp.util.StringUtils;
import org.aksw.fox.data.Entity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Based on:
 * {@see https://github.com/dice-group/FOX/blob/master/src/main/java/org/aksw/fox/data/TokenManager.java}
 *
 * @author Cedric Richter
 */
public class TokenManager {
    public static Logger LOG        = LogManager.getLogger(TokenManager.class);

    protected String               input      = "";

    protected String               tokenInput = "";
    protected String[]             tokenSplit = null;

    public static final String     SEP        = "fFWAeRQ9w0L";

    protected Map<Integer, String> indexToken = new LinkedHashMap<>();
    protected Map<Integer, String> indexLabel = new LinkedHashMap<>();
    protected Map<String, Integer> labelIndex = new LinkedHashMap<>();

    /**
     *
     * @param sentences
     */
    public TokenManager(String sentences) {

        LOG.info("TokenManager ...");
        if (LOG.isDebugEnabled())
            LOG.debug(sentences);

        // clean sentences
        input = StringUtils.join(TextUtil.getSentences(sentences), " ").trim();
        sentences = null;

        // token
        tokenSplit = TextUtil.getSentencesToken(input);

        // token input
        tokenInput = "";
        for (String token : tokenSplit) {
            if (!token.trim().isEmpty()) {
                tokenInput += " " + token;
            } else {
                if (token.isEmpty())
                    tokenInput += " ";
                else
                    tokenInput += token;
            }
        }
        if (tokenInput.length() > 1)
            tokenInput = tokenInput.substring(1, tokenInput.length());

        if (LOG.isDebugEnabled()) {
            LOG.debug(input);
            LOG.debug(tokenInput);
        }

        // initializes indexToken, indexLabel and labelIndex
        int pointer = 0;
        for (int i = 0; i < tokenSplit.length; i++) {
            String token = tokenSplit[i].trim();
            if (!token.isEmpty()) {
                int index = tokenInput.substring(pointer, tokenInput.length()).indexOf(token);
                if (index != -1) {
                    pointer = index + pointer;
                    indexToken.put(pointer, token);
                    setlabel(pointer, token + SEP + pointer);
                } else {
                    LOG.debug("token not found:" + token);
                }
            }
        }
    }

    public void repairEntities(Set<Entity> entities) {

        for (Entity entity : entities)
            repairEntity(entity);
    }

    private void repairEntity(Entity entity) {

        Set<Integer> occurrence = TextUtil.getIndices(entity.getText(), getTokenInput());
        if (occurrence.size() != 0) {

        } else {
            LOG.debug("can't find entity:" + entity.getText() + "(" + entity.getTool() + "), try to fix ...");

            String fix = entity.getText().replaceAll("([\\p{Punct}&&[^\")\\]}.]])(\\s+)", "$1");
            occurrence = TextUtil.getIndices(fix, tokenInput);

            if (occurrence.size() != 0) {
                entity.setText(fix);
                LOG.debug("fixed.");
            } else {
                fix = fix.replaceAll("(\\s+)([\\p{Punct}&&[^\"(\\[{]])", "$2");
                occurrence = TextUtil.getIndices(fix, tokenInput);

                if (occurrence.size() != 0) {
                    entity.setText(fix);
                    LOG.debug("fixed.");
                } else {

                    if (entity.getText().endsWith("."))
                        fix = entity.getText().substring(0, entity.getText().length() - 1);
                    else
                        fix = entity.getText() + ".";

                    occurrence = TextUtil.getIndices(fix, tokenInput);
                    if (occurrence.size() != 0) {
                        entity.setText(fix);
                        LOG.debug("fixed.");
                    } else {
                        LOG.debug("can't fix it.");
                        entity.setText("");
                    }
                }
            }
        }
        entity.setText(entity.getText().trim());
    }

    // private
    private void setlabel(int index, String label) {
        indexLabel.put(index, label);
        labelIndex.put(label, index);
    }

    // getter
    public String getTokenInput() {
        return tokenInput;
    }

    public String[] getTokenSplit() {
        return tokenSplit;
    }

    public Set<String> getLabeledInput() {
        for (Integer index : indexToken.keySet()) {
            String label = indexLabel.get(index);
            if (label == null)
                setlabel(index, indexToken.get(index));
        }
        return labelIndex.keySet();
    }

    //
    public String getInput() {
        return input;
    }

    public int getLabelIndex(String label) {
        return labelIndex.get(label);
    }

    public String getLabel(int index) {
        return indexLabel.get(index);
    }

    public String getToken(int index) {
        return indexToken.get(index);
    }
}
