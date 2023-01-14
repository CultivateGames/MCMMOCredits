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
import com.google.gson.stream.JsonWriter;
import games.cultivate.mcmmocredits.data.JSONDatabase;
import games.cultivate.mcmmocredits.util.User;

import java.io.IOException;
import java.util.UUID;

/**
 * Class responsible for serializing and deserializing a {@link User} from {@link JSONDatabase}
 */
public final class PlayerSerializer extends TypeAdapter<User> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final JsonWriter out, final User value) throws IOException {
        out.beginObject();
        out.name("UUID").value(value.uuid().toString());
        out.name("name").value(value.username());
        out.name("credits").value(value.credits());
        out.name("redeemed").value(value.redeemed());
        out.endObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User read(final JsonReader in) throws IOException {
        UUID uuid = new UUID(0, 0);
        String username = "";
        int credits = 0;
        int redeemed = 0;
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName().toLowerCase()) {
                case "uuid" -> uuid = UUID.fromString(in.nextString());
                case "username" -> username = in.nextString();
                case "credits" -> credits = in.nextInt();
                case "redeemed" -> redeemed = in.nextInt();
                default -> { // do nothing
                }
            }
        }
        in.endObject();
        return new User(uuid, username, credits, redeemed);
    }
}
