package me.rred.splinter.client.routing.triggers;

public class TradeEndTrigger extends Trigger {
    private int barterCap;

    public TradeEndTrigger(Trigger.TriggerSlot triggerSlot, int barterCap) {
        super(triggerSlot);
        this.barterCap = barterCap;
    }

    @Override
    public Trigger copy() {
        return new TradeEndTrigger(triggerSlot, barterCap);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TradeEndTrigger other)) return false;
        return other.barterCap == barterCap;
    }

    public TriggerType getType() {
        return TriggerType.TRADE_END;
    }

    public int getBarterCap() {
        return barterCap;
    }
}
