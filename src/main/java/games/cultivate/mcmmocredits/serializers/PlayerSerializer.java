//
// MIT License
//
// Copyright (c) 2022 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.serializers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import games.cultivate.mcmmocredits.data.JSONDatabase;
import games.cultivate.mcmmocredits.util.JSONUser;

import java.io.IOException;
import java.util.UUID;

/**
 * Class responsible for serializing and deserializing a {@link JSONUser} from {@link JSONDatabase}
 */
public final class PlayerSerializer extends TypeAdapter<JSONUser> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final JsonWriter out, final JSONUser value) throws IOException {
        out.beginObject().name("UUID").value(value.uuid().toString())
                .name("name").value(value.username())
                .name("credits").value(value.credits())
                .endObject().flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONUser read(final JsonReader in) throws IOException {
        UUID uuid = new UUID(0, 0);
        String username = "";
        String fieldName = "";
        int credits = 0;
        in.beginObject();
        while (in.hasNext()) {
            JsonToken token = in.peek();
            if (token.equals(JsonToken.NAME)) {
                fieldName = in.nextName();
            }
            if (fieldName.equalsIgnoreCase("UUID")) {
                in.peek();
                uuid = UUID.fromString(in.nextString());
            }

            if (fieldName.equalsIgnoreCase("username")) {
                in.peek();
                username = in.nextString();
            }

            if (fieldName.equalsIgnoreCase("credits")) {
                in.peek();
                credits = in.nextInt();
            }
        }
        in.endObject();
        return new JSONUser(uuid, username, credits);
    }
}
