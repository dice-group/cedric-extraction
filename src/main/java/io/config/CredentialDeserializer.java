package io.config;

import com.google.gson.*;
import io.IDBCredentials;
import io.simple.SimpleDBCredentials;

import java.lang.reflect.Type;

public class CredentialDeserializer implements JsonDeserializer<IDBCredentials> {
    @Override
    public IDBCredentials deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if(jsonElement instanceof JsonObject) {
            JsonObject obj = (JsonObject)jsonElement;
            String name = null;
            char[] password = null;

            if(obj.has("name")){
                JsonElement nameEl = obj.get("name");
                if(nameEl.isJsonPrimitive()){
                    name = nameEl.getAsString();
                }
            }

            if(obj.has("password")){
                JsonElement passEl = obj.get("password");
                if(passEl.isJsonPrimitive()){
                    password = passEl.getAsString().toCharArray();
                }
            }

            if(name != null && password != null){
                return new SimpleDBCredentials(name, password);
            }

        }
        return null;
    }
}
