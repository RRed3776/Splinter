package me.rred.splinter.client.routing;

import com.google.gson.*;
import me.rred.splinter.client.routing.triggers.*;

import java.lang.reflect.Type;

// https://www.baeldung.com/gson-polymorphism
public class TriggerTypeAdapter implements JsonSerializer<Trigger>, JsonDeserializer<Trigger> {
    @Override
    public JsonElement serialize(Trigger trigger, Type type, JsonSerializationContext context) {
        JsonElement elem = new Gson().toJsonTree(trigger);
        Trigger.TriggerType triggerType = trigger.getType();
        elem.getAsJsonObject().addProperty("type", triggerType.name());
        return elem;
    }

    @Override
    public Trigger deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Trigger.TriggerType triggerType = Trigger.TriggerType.valueOf(jsonObject.get("type").getAsString());

        return switch (triggerType) {
            case BLOCK_BREAK -> context.deserialize(jsonObject, BlockBreakTrigger.class);
            case MAP -> context.deserialize(jsonObject, MapTrigger.class);
            case POSITION -> context.deserialize(jsonObject, PositionTrigger.class);
            case TRADE_START -> context.deserialize(jsonObject, TradeStartTrigger.class);
            case TRADE_END -> context.deserialize(jsonObject, TradeEndTrigger.class);
        };
    }
}
