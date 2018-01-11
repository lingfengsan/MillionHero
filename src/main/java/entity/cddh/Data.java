package entity.cddh;

public class Data {
    private  Event event;

    public Data(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Data{" +
                "event=" + event +
                '}';
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
